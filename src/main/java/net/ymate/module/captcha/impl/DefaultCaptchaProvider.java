/*
 * Copyright 2007-2020 the original author or authors.
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
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 06:43
 */
public class DefaultCaptchaProvider implements ICaptchaProvider {

    private ICaptcha owner;

    private Cage cage;

    private boolean initialized;

    @Override
    public void initialize(ICaptcha owner) throws Exception {
        if (!initialized) {
            this.owner = owner;
            //
            Random random = new Random();
            //
            ObjectRoulette<Color> foregrounds = null;
            if (!owner.getConfig().getForegrounds().isEmpty()) {
                foregrounds = new ObjectRoulette<>(random, owner.getConfig().getForegrounds().toArray(new Color[0]));
            }
            ObjectRoulette<Font> fonts = null;
            if (!owner.getConfig().getFonts().isEmpty()) {
                fonts = new ObjectRoulette<>(random, owner.getConfig().getFonts().toArray(new Font[0]));
            }
            //
            ScaleConfig scaleCfg = null;
            if (owner.getConfig().getEffectScale() != null) {
                scaleCfg = new ScaleConfig(owner.getConfig().getEffectScale()[0], owner.getConfig().getEffectScale()[1]);
            }
            //
            EffectConfig effectConfig = new EffectConfig(owner.getConfig().isEffectRipple(), owner.getConfig().isEffectBlur(), owner.getConfig().isEffectOutline(), owner.getConfig().isEffectRotate(), scaleCfg);
            //
            cage = new Cage(new Painter(owner.getConfig().getWidth(),
                    owner.getConfig().getHeight(),
                    owner.getConfig().getBackground(),
                    owner.getConfig().getQuality(), effectConfig, random),
                    fonts, foregrounds, owner.getConfig().getFormat(),
                    owner.getConfig().getCompressRatio(), new RandomTokenGenerator(random, owner.getConfig().getTokenLengthMin()), random);
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public String createCaptcha(OutputStream output) throws Exception {
        String tokenStr = owner.generateToken(ICaptcha.Type.DEFAULT);
        if (StringUtils.isBlank(tokenStr)) {
            tokenStr = cage.getTokenGenerator().next();
        }
        cage.draw(tokenStr, output);
        return tokenStr;
    }
}
