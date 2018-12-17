/*
 * Copyright 2007-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ymate.module.captcha.impl;

import com.github.cage.Cage;
import com.github.cage.ObjectRoulette;
import com.github.cage.image.EffectConfig;
import com.github.cage.image.Painter;
import com.github.cage.image.ScaleConfig;
import com.github.cage.token.RandomTokenGenerator;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaProvider;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午6:43
 * @version 1.0
 */
public class DefaultCaptchaProvider implements ICaptchaProvider {

    private ICaptcha __owner;

    private Cage __cage;

    @Override
    public void init(ICaptcha owner) throws Exception {
        __owner = owner;
        //
        Random _random = new Random();
        //
        ObjectRoulette<Color> _foregrounds = null;
        if (!owner.getModuleCfg().getForegrounds().isEmpty()) {
            _foregrounds = new ObjectRoulette<Color>(_random, owner.getModuleCfg().getForegrounds().toArray(new Color[0]));
        }
        ObjectRoulette<Font> _fonts = null;
        if (!owner.getModuleCfg().getFonts().isEmpty()) {
            _fonts = new ObjectRoulette<Font>(_random, owner.getModuleCfg().getFonts().toArray(new Font[0]));
        }
        //
        ScaleConfig _scaleCfg = null;
        if (owner.getModuleCfg().getEffectScale() != null) {
            _scaleCfg = new ScaleConfig(owner.getModuleCfg().getEffectScale()[0], owner.getModuleCfg().getEffectScale()[1]);
        }
        //
        EffectConfig _effect = new EffectConfig(owner.getModuleCfg().isEffectRipple(), owner.getModuleCfg().isEffectBlur(), owner.getModuleCfg().isEffectOutline(), owner.getModuleCfg().isEffectRotate(), _scaleCfg);
        //
        __cage = new Cage(new Painter(owner.getModuleCfg().getWidth(),
                owner.getModuleCfg().getHeight(),
                owner.getModuleCfg().getBackground(),
                owner.getModuleCfg().getQuality(), _effect, _random),
                _fonts, _foregrounds, owner.getModuleCfg().getFormat(),
                owner.getModuleCfg().getCompressRatio(), new RandomTokenGenerator(_random, owner.getModuleCfg().getTokenLengthMin()), _random);
    }

    @Override
    public String createCaptcha(OutputStream output) throws Exception {
        String _token = __owner.generateToken();
        if (StringUtils.isBlank(_token)) {
            _token = __cage.getTokenGenerator().next();
        }
        __cage.draw(_token, output);
        //
        return _token;
    }
}
