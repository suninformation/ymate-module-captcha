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
import net.ymate.platform.core.util.ClassUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午4:59
 * @version 1.0
 */
public class DefaultModuleCfg implements ICaptchaModuleCfg {

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

    private boolean effectRatale;

    public DefaultModuleCfg(YMP owner) {
        Map<String, String> _moduleCfgs = owner.getConfig().getModuleConfigs(ICaptcha.MODULE_NAME);
        //
        __isDisabled = new BlurObject(_moduleCfgs.get("disabled")).toBooleanValue();

        if (!__isDisabled) {
            __isDevelopMode = new BlurObject(_moduleCfgs.get("dev_mode")).toBooleanValue();
            //
            foregrounds = new ArrayList<Color>();
            fonts = new ArrayList<Font>();
            //
            if ((__provider = ClassUtils.impl(_moduleCfgs.get("provider_class"), ICaptchaProvider.class, this.getClass())) == null) {
                __provider = new DefaultCaptchaProvider();
            }
            //
            if ((__storageAdapter = ClassUtils.impl(_moduleCfgs.get("storage_adapter_class"), ICaptchaStorageAdapter.class, this.getClass())) == null) {
                throw new NullArgumentException("storage_adapter_class");
            }
            //
            __tokenGenerator = ClassUtils.impl(_moduleCfgs.get("token_generator_class"), ICaptchaTokenGenerator.class, this.getClass());
            //
            __scopeProcessor = ClassUtils.impl(_moduleCfgs.get("token_processor_class"), ICaptchaScopeProcessor.class, this.getClass());
            //
            __sendProvider = ClassUtils.impl(_moduleCfgs.get("send_provider_class"), ICaptchaSendProvider.class, this.getClass());
            //
            __needCaptchaWrongTimes = BlurObject.bind(_moduleCfgs.get("need_captcha_wrong_times")).toIntValue();
            if (__needCaptchaWrongTimes < 0) {
                __needCaptchaWrongTimes = 0;
            } else if (__needCaptchaWrongTimes > 0 && __scopeProcessor == null) {
                throw new NullArgumentException("scope_processor_class");
            }
            //
            __cacheNamePrefix = StringUtils.trimToEmpty(_moduleCfgs.get("cache_name_prefix"));
            //
            tokenLengthMin = BlurObject.bind(StringUtils.defaultIfBlank(_moduleCfgs.get("token_length_min"), "4")).toIntValue();
            //
            tokenTimeout = (tokenTimeout = BlurObject.bind(_moduleCfgs.get("token_timeout")).toIntValue()) <= 0 ? null : tokenTimeout;
            //
            height = BlurObject.bind(StringUtils.defaultIfBlank(_moduleCfgs.get("height"), "70")).toIntValue();
            //
            width = BlurObject.bind(StringUtils.defaultIfBlank(_moduleCfgs.get("width"), "200")).toIntValue();
            //
            String[] _colorsArr = StringUtils.split(_moduleCfgs.get("foregrounds"), "|");
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
            String[] _bgColorRGB = StringUtils.split(_moduleCfgs.get("background"), ",");
            if (_bgColorRGB != null && _bgColorRGB.length == 3) {
                int _r = Integer.parseInt(_bgColorRGB[0]);
                int _g = Integer.parseInt(_bgColorRGB[1]);
                int _b = Integer.parseInt(_bgColorRGB[2]);
                if (_r <= 255 && _g <= 255 & _b <= 255) {
                    background = new Color(_r, _g, _b);
                }
            }
            //
            quality = Painter.Quality.valueOf(StringUtils.defaultIfBlank(_moduleCfgs.get("quality"), "max").toUpperCase());
            //
            compressRatio = (compressRatio = BlurObject.bind(_moduleCfgs.get("compress_ratio")).toFloatValue()) <= 0 ? null : compressRatio;
            //
            format = StringUtils.defaultIfBlank(_moduleCfgs.get("format"), "jpeg");
            //
            String[] _fontArr = StringUtils.split(_moduleCfgs.get("fonts"), "|");
            if (_fontArr != null) {
                int _fontSize = height / 2;
                for (String _font : _fontArr) {
                    String[] _fArr = StringUtils.split(_font, ",");
                    if (_fArr != null) {
                        int _fontStyle = Font.PLAIN;
                        if (_fArr.length > 1) {
                            if ("bold".equalsIgnoreCase(_fArr[1])) {
                                _fontStyle = Font.BOLD;
                            } else if ("italic".equalsIgnoreCase(_fArr[1])) {
                                _fontStyle = Font.ITALIC;
                            }
                        }
                        fonts.add(new Font(_fArr[0], _fontStyle, _fontSize));
                    }
                }
            }
            //
            String[] _scales = StringUtils.split(_moduleCfgs.get(StringUtils.defaultIfBlank("effect.scale", "1,1")), ",");
            if (_scales != null && _scales.length == 2) {
                float _x = BlurObject.bind(_scales[0]).toFloatValue();
                float _y = BlurObject.bind(_scales[1]).toFloatValue();
                if ((_x >= 0 && _x <= 1) && (_y >= 0 && _y <= 1)) {
                    effectScale = new float[]{_x, _y};
                }
            }
            effectRipple = BlurObject.bind(StringUtils.defaultIfBlank(_moduleCfgs.get("effect.ripple"), "true")).toBooleanValue();
            effectBlur = BlurObject.bind(StringUtils.defaultIfBlank(_moduleCfgs.get("effect.blur"), "true")).toBooleanValue();
            effectOutline = BlurObject.bind(StringUtils.defaultIfBlank(_moduleCfgs.get("effect.outline"), "false")).toBooleanValue();
            effectRatale = BlurObject.bind(StringUtils.defaultIfBlank(_moduleCfgs.get("effect.ratale"), "true")).toBooleanValue();
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
    public boolean isEffectRatale() {
        return effectRatale;
    }
}
