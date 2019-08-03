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

import com.github.cage.image.Painter;
import net.ymate.module.captcha.*;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.support.IConfigReader;
import net.ymate.platform.core.support.impl.MapSafeConfigReader;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午4:59
 * @version 1.0
 */
public class DefaultCaptchaModuleCfg implements ICaptchaModuleCfg {

    private final boolean __isDisabled;

    private boolean __isDevelopMode;

    private ICaptchaProvider __provider;

    private ICaptchaTokenGenerator __tokenGenerator;

    private ICaptchaStorageAdapter __storageAdapter;

    private ICaptchaScopeProcessor __scopeProcessor;

    private ICaptchaSendProvider __sendProvider;

    private int __needCaptchaWrongTimes;

    private String __cacheNamePrefix;

    private int tokenLengthMin;

    private Integer tokenTimeout;

    private Integer height;

    private Integer width;

    private java.util.List<Color> foregrounds;

    private Color background;

    private Painter.Quality quality;

    private Float compressRatio;

    private String format;

    private java.util.List<Font> fonts;

    private float[] effectScale;

    private boolean effectRipple;

    private boolean effectBlur;

    private boolean effectOutline;

    private boolean effectRotate;

    public DefaultCaptchaModuleCfg(YMP owner) throws Exception {
        IConfigReader _moduleCfg = MapSafeConfigReader.bind(owner.getConfig().getModuleConfigs(ICaptcha.MODULE_NAME));
        //
        __isDisabled = _moduleCfg.getBoolean(DISABLED);

        if (!__isDisabled) {
            __isDevelopMode = _moduleCfg.getBoolean(DEV_MODE);
            //
            foregrounds = new ArrayList<Color>();
            fonts = new ArrayList<Font>();
            //
            if ((__provider = _moduleCfg.getClassImpl(PROVIDER_CLASS, ICaptchaProvider.class)) == null) {
                __provider = new DefaultCaptchaProvider();
            }
            //
            if ((__storageAdapter = _moduleCfg.getClassImpl(STORAGE_ADAPTER_CLASS, ICaptchaStorageAdapter.class)) == null) {
                throw new NullArgumentException(STORAGE_ADAPTER_CLASS);
            }
            //
            __tokenGenerator = _moduleCfg.getClassImpl(TOKEN_GENERATOR_CLASS, ICaptchaTokenGenerator.class);
            //
            __scopeProcessor = _moduleCfg.getClassImpl(SCOPE_PROCESSOR_CLASS, ICaptchaScopeProcessor.class);
            //
            __sendProvider = _moduleCfg.getClassImpl(SEND_PROCESSOR_CLASS, ICaptchaSendProvider.class);
            //
            __needCaptchaWrongTimes = _moduleCfg.getInt(NEED_CAPTCHA_WRONG_TIMES);
            if (__needCaptchaWrongTimes < 0) {
                __needCaptchaWrongTimes = 0;
            } else if (__needCaptchaWrongTimes > 0 && __scopeProcessor == null) {
                throw new NullArgumentException(SCOPE_PROCESSOR_CLASS);
            }
            //
            __cacheNamePrefix = _moduleCfg.getString(CACHE_NAME_PREFIX);
            //
            tokenLengthMin = _moduleCfg.getInt(TOKEN_LENGTH_MIN, 4);
            //
            tokenTimeout = (tokenTimeout = _moduleCfg.getInt(TOKEN_TIMEOUT)) <= 0 ? null : tokenTimeout;
            //
            height = _moduleCfg.getInt(HEIGHT, 70);
            //
            width = _moduleCfg.getInt(WIDTH, 200);
            //
            String[] _colorsArr = _moduleCfg.getArray(FOREGROUNDS);
            if (_colorsArr != null) {
                for (String _color : _colorsArr) {
                    if (_color.contains(",")) {
                        String[] _rgb = StringUtils.split(_color, ",");
                        if (_rgb != null && _rgb.length == 3) {
                            int _r = Integer.parseInt(_rgb[0]);
                            int _g = Integer.parseInt(_rgb[1]);
                            int _b = Integer.parseInt(_rgb[2]);
                            if (_r <= 255 && _g <= 255 & _b <= 255) {
                                foregrounds.add(new Color(_r, _g, _b));
                            }
                        }
                    }
                }
            }
            //
            String[] _bgColorRGB = StringUtils.split(_moduleCfg.getString(BACKGROUND), ",");
            if (_bgColorRGB != null && _bgColorRGB.length == 3) {
                int _r = Integer.parseInt(_bgColorRGB[0]);
                int _g = Integer.parseInt(_bgColorRGB[1]);
                int _b = Integer.parseInt(_bgColorRGB[2]);
                if (_r <= 255 && _g <= 255 & _b <= 255) {
                    background = new Color(_r, _g, _b);
                }
            }
            //
            quality = Painter.Quality.valueOf(_moduleCfg.getString(QUALITY, "max").toUpperCase());
            //
            compressRatio = (compressRatio = _moduleCfg.getFloat(COMPRESS_RATIO)) <= 0 ? null : compressRatio;
            //
            format = _moduleCfg.getString(FORMAT, "jpeg");
            //
            ICaptchaFontsParser fontsParser = _moduleCfg.getClassImpl(FONTS_PARSER_CLASS, DefaultCaptchaFontsParser.class.getName(), ICaptchaFontsParser.class);
            if (fontsParser == null) {
                fontsParser = new DefaultCaptchaFontsParser();
            }
            List<Font> fontList = fontsParser.parse(_moduleCfg, height / 2);
            if (!fontList.isEmpty()) {
                fonts.addAll(fontList);
            }
            //
            String[] _scales = StringUtils.split(_moduleCfg.getString(EFFECT_SCALE, "1,1"), ",");
            if (_scales != null && _scales.length == 2) {
                float _x = BlurObject.bind(_scales[0]).toFloatValue();
                float _y = BlurObject.bind(_scales[1]).toFloatValue();
                if ((_x >= 0 && _x <= 1) && (_y >= 0 && _y <= 1)) {
                    effectScale = new float[]{_x, _y};
                }
            }
            effectRipple = _moduleCfg.getBoolean(EFFECT_RIPPLE, true);
            effectBlur = _moduleCfg.getBoolean(EFFECT_BLUR, true);
            effectOutline = _moduleCfg.getBoolean(EFFECT_OUTLINE, false);
            effectRotate = _moduleCfg.getBoolean(EFFECT_ROTATE, true);
        }
    }

    @Override
    public boolean isDisabled() {
        return __isDisabled;
    }

    @Override
    public boolean isDevelopMode() {
        return __isDevelopMode;
    }

    @Override
    public ICaptchaProvider getCaptchaProvider() {
        return __provider;
    }

    @Override
    public ICaptchaTokenGenerator getTokenGenerator() {
        return __tokenGenerator;
    }

    @Override
    public ICaptchaStorageAdapter getStorageAdapter() {
        return __storageAdapter;
    }

    @Override
    public ICaptchaSendProvider getSendProvider() {
        return __sendProvider;
    }

    @Override
    public ICaptchaScopeProcessor getScopeProcessor() {
        return __scopeProcessor;
    }

    @Override
    public int getNeedCaptchaWrongTimes() {
        return __needCaptchaWrongTimes;
    }

    @Override
    public String getCacheNamePrefix() {
        return __cacheNamePrefix;
    }

    @Override
    public int getTokenLengthMin() {
        return tokenLengthMin;
    }

    @Override
    public Integer getTokenTimeout() {
        return tokenTimeout;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public java.util.List<Color> getForegrounds() {
        return foregrounds;
    }

    @Override
    public Color getBackground() {
        return background;
    }

    @Override
    public Painter.Quality getQuality() {
        return quality;
    }

    @Override
    public Float getCompressRatio() {
        return compressRatio;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public java.util.List<Font> getFonts() {
        return fonts;
    }

    @Override
    public float[] getEffectScale() {
        return effectScale;
    }

    @Override
    public boolean isEffectRipple() {
        return effectRipple;
    }

    @Override
    public boolean isEffectBlur() {
        return effectBlur;
    }

    @Override
    public boolean isEffectOutline() {
        return effectOutline;
    }

    @Override
    public boolean isEffectRotate() {
        return effectRotate;
    }
}
