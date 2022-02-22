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

import com.github.cage.image.Painter;
import net.ymate.module.captcha.*;
import net.ymate.module.captcha.annotation.CaptchaConf;
import net.ymate.platform.commons.lang.BlurObject;
import net.ymate.platform.commons.util.ClassUtils;
import net.ymate.platform.core.configuration.IConfigReader;
import net.ymate.platform.core.module.IModuleConfigurer;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 04:59
 */
public final class DefaultCaptchaConfig implements ICaptchaConfig {

    private boolean enabled = true;

    private boolean developMode;

    private final Set<ICaptcha.Type> captchaTypes = new HashSet<>();

    private ICaptchaProvider captchaProvider;

    private ICaptchaTokenGenerator tokenGenerator;

    private ICaptchaStorageAdapter storageAdapter;

    private ICaptchaScopeProcessor scopeProcessor;

    private ICaptchaSendProvider sendProvider;

    private int needCaptchaWrongTimes;

    private String cacheNamePrefix;

    private int tokenLengthMin;

    private int tokenTimeout;

    private String servicePrefix;

    private boolean serviceEnabled;

    private int height;

    private int width;

    private final List<Color> foregrounds = new ArrayList<>();

    private Color background;

    private Painter.Quality quality;

    private Float compressRatio;

    private String format;

    private final List<Font> fonts = new ArrayList<>();

    private float[] effectScale;

    private boolean effectRipple;

    private boolean effectBlur;

    private boolean effectOutline;

    private boolean effectRotate;

    private boolean initialized;

    public static DefaultCaptchaConfig defaultConfig() {
        return builder().build();
    }

    public static DefaultCaptchaConfig create(IModuleConfigurer moduleConfigurer) throws Exception {
        return new DefaultCaptchaConfig(null, moduleConfigurer);
    }

    public static DefaultCaptchaConfig create(Class<?> mainClass, IModuleConfigurer moduleConfigurer) throws Exception {
        return new DefaultCaptchaConfig(mainClass, moduleConfigurer);
    }

    public static Builder builder() {
        return new Builder();
    }

    private DefaultCaptchaConfig() {
    }

    private DefaultCaptchaConfig(Class<?> mainClass, IModuleConfigurer moduleConfigurer) throws Exception {
        IConfigReader configReader = moduleConfigurer.getConfigReader();
        //
        CaptchaConf confAnn = mainClass == null ? null : mainClass.getAnnotation(CaptchaConf.class);
        //
        enabled = configReader.getBoolean(ENABLED, confAnn == null || confAnn.enabled());
        if (enabled) {
            String captchaTypeStr = configReader.getString(CAPTCHA_TYPES);
            if (StringUtils.isBlank(captchaTypeStr) && confAnn != null) {
                captchaTypeStr = StringUtils.join(Arrays.stream(confAnn.captchaTypes()).map(Enum::name).toArray(String[]::new), '|');
            }
            String[] captchaTypeArr = StringUtils.split(captchaTypeStr, '|');
            if (captchaTypeArr == null || StringUtils.containsIgnoreCase(captchaTypeStr, ICaptcha.Type.ALL.name())) {
                captchaTypes.addAll(Arrays.asList(ICaptcha.Type.DEFAULT, ICaptcha.Type.SMS, ICaptcha.Type.MAIL));
            } else {
                Arrays.stream(captchaTypeArr).forEach(captchaModeStr -> captchaTypes.add(ICaptcha.Type.valueOf(captchaModeStr.toUpperCase())));
            }
            developMode = configReader.getBoolean(DEV_MODE, confAnn != null && confAnn.developMode());
            captchaProvider = configReader.getClassImpl(PROVIDER_CLASS, confAnn == null || confAnn.providerClass().equals(ICaptchaProvider.class) ? null : confAnn.providerClass().getName(), ICaptchaProvider.class);
            storageAdapter = configReader.getClassImpl(STORAGE_ADAPTER_CLASS, confAnn == null || confAnn.storageAdapterClass().equals(ICaptchaStorageAdapter.class) ? null : confAnn.storageAdapterClass().getName(), ICaptchaStorageAdapter.class);
            tokenGenerator = configReader.getClassImpl(TOKEN_GENERATOR_CLASS, confAnn == null || confAnn.tokenGeneratorClass().equals(ICaptchaTokenGenerator.class) ? null : confAnn.tokenGeneratorClass().getName(), ICaptchaTokenGenerator.class);
            scopeProcessor = configReader.getClassImpl(SCOPE_PROCESSOR_CLASS, confAnn == null || confAnn.scopeProcessor().equals(ICaptchaScopeProcessor.class) ? null : confAnn.scopeProcessor().getName(), ICaptchaScopeProcessor.class);
            sendProvider = configReader.getClassImpl(SEND_PROCESSOR_CLASS, confAnn == null || confAnn.sendProviderClass().equals(ICaptchaSendProvider.class) ? null : confAnn.sendProviderClass().getName(), ICaptchaSendProvider.class);
            needCaptchaWrongTimes = configReader.getInt(NEED_CAPTCHA_WRONG_TIMES, confAnn != null ? confAnn.needCaptchaWrongTimes() : 0);
            cacheNamePrefix = configReader.getString(CACHE_NAME_PREFIX, confAnn != null ? confAnn.cacheNamePrefix() : null);
            tokenLengthMin = configReader.getInt(TOKEN_LENGTH_MIN, confAnn != null && confAnn.tokenLengthMin() > 0 ? confAnn.tokenLengthMin() : 4);
            tokenTimeout = configReader.getInt(TOKEN_TIMEOUT, confAnn != null ? confAnn.tokenTimeout() : 0);
            servicePrefix = configReader.getString(SERVICE_PREFIX, confAnn != null ? confAnn.servicePrefix() : null);
            serviceEnabled = configReader.getBoolean(SERVICE_ENABLED, confAnn != null && confAnn.serviceEnabled());
            height = configReader.getInt(HEIGHT, confAnn != null && confAnn.height() > 0 ? confAnn.height() : 70);
            width = configReader.getInt(WIDTH, confAnn != null && confAnn.width() > 0 ? confAnn.width() : 200);
            //
            String[] colorsArr = configReader.getArray(FOREGROUNDS, confAnn != null ? confAnn.foregrounds() : null);
            if (colorsArr != null) {
                Arrays.stream(colorsArr)
                        .filter(color -> color.contains(","))
                        .map(color -> StringUtils.split(color, ","))
                        .filter(rgb -> rgb != null && rgb.length == 3)
                        .forEachOrdered(rgb -> {
                            int r = Integer.parseInt(rgb[0]);
                            int g = Integer.parseInt(rgb[1]);
                            int b = Integer.parseInt(rgb[2]);
                            if (r <= 255 && g <= 255 & b <= 255) {
                                foregrounds.add(new Color(r, g, b));
                            }
                        });
            }
            String[] bgColor = StringUtils.split(configReader.getString(BACKGROUND, confAnn != null ? confAnn.background() : null), ",");
            if (bgColor != null && bgColor.length == 3) {
                int r = Integer.parseInt(bgColor[0]);
                int g = Integer.parseInt(bgColor[1]);
                int b = Integer.parseInt(bgColor[2]);
                if (r <= 255 && g <= 255 & b <= 255) {
                    background = new Color(r, g, b);
                }
            }
            //
            quality = Painter.Quality.valueOf(configReader.getString(QUALITY, confAnn != null ? confAnn.quality().name() : "max").toUpperCase());
            compressRatio = (compressRatio = configReader.getFloat(COMPRESS_RATIO, confAnn != null ? confAnn.compressRatio() : 0)) <= 0 ? null : compressRatio;
            format = configReader.getString(FORMAT, StringUtils.defaultIfBlank(confAnn != null ? confAnn.format() : null, "jpeg"));
            //
            ICaptchaFontsParser fontsParser = configReader.getClassImpl(FONTS_PARSER_CLASS, confAnn == null || confAnn.fontsParserClass().equals(ICaptchaFontsParser.class) ? null : confAnn.fontsParserClass().getName(), ICaptchaFontsParser.class);
            if (fontsParser == null) {
                fontsParser = new DefaultCaptchaFontsParser();
            }
            List<Font> fontList = fontsParser.parse(configReader, height / 2);
            if (!fontList.isEmpty()) {
                fonts.addAll(fontList);
            }
            //
            String[] scales = StringUtils.split(configReader.getString(EFFECT_SCALE, StringUtils.defaultIfBlank(confAnn != null ? StringUtils.join(confAnn.effectScale(), ',') : null, "1,1")), ",");
            if (scales != null && scales.length == 2) {
                float x = BlurObject.bind(scales[0]).toFloatValue();
                float y = BlurObject.bind(scales[1]).toFloatValue();
                if (x >= 0 && x <= 1 && y >= 0 && y <= 1) {
                    effectScale = new float[]{x, y};
                }
            }
            effectRipple = configReader.getBoolean(EFFECT_RIPPLE, confAnn == null || confAnn.effectRipple());
            effectBlur = configReader.getBoolean(EFFECT_BLUR, confAnn == null || confAnn.effectBlur());
            effectOutline = configReader.getBoolean(EFFECT_OUTLINE, confAnn != null && confAnn.effectOutline());
            effectRotate = configReader.getBoolean(EFFECT_ROTATE, confAnn == null || confAnn.effectRotate());
        }
    }

    @Override
    public void initialize(ICaptcha owner) throws Exception {
        if (!initialized) {
            if (enabled) {
                if (captchaProvider == null) {
                    captchaProvider = ClassUtils.loadClass(ICaptchaProvider.class, DefaultCaptchaProvider.class);
                }
                captchaProvider.initialize(owner);
                //
                if (storageAdapter == null) {
                    storageAdapter = ClassUtils.loadClass(ICaptchaStorageAdapter.class, DefaultCaptchaStorageAdapter.class);
                }
                storageAdapter.initialize(owner);
                //
                if (tokenGenerator == null) {
                    tokenGenerator = ClassUtils.loadClass(ICaptchaTokenGenerator.class);
                }
                if (tokenGenerator != null) {
                    tokenGenerator.initialize(owner);
                }
                if (sendProvider == null) {
                    sendProvider = ClassUtils.loadClass(ICaptchaSendProvider.class, DefaultCaptchaSendProvider.class);
                }
                sendProvider.initialize(owner);
                //
                if (scopeProcessor == null) {
                    scopeProcessor = ClassUtils.loadClass(ICaptchaScopeProcessor.class);
                }
                if (scopeProcessor != null) {
                    scopeProcessor.initialize(owner);
                }
                //
                if (needCaptchaWrongTimes < 0) {
                    needCaptchaWrongTimes = 0;
                } else if (needCaptchaWrongTimes > 0 && scopeProcessor == null) {
                    throw new NullArgumentException(SCOPE_PROCESSOR_CLASS);
                }
            }
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (!initialized) {
            this.enabled = enabled;
        }
    }

    @Override
    public Set<ICaptcha.Type> getCaptchaTypes() {
        return captchaTypes;
    }

    public void addCaptchaType(ICaptcha.Type captchaType) {
        if (!initialized && captchaType != null) {
            this.captchaTypes.add(captchaType);
        }
    }

    public void addCaptchaTypes(Collection<ICaptcha.Type> captchaTypes) {
        if (!initialized && captchaTypes != null) {
            this.captchaTypes.addAll(captchaTypes);
        }
    }

    @Override
    public boolean isDevelopMode() {
        return developMode;
    }

    public void setDevelopMode(boolean developMode) {
        if (!initialized) {
            this.developMode = developMode;
        }
    }

    @Override
    public ICaptchaProvider getCaptchaProvider() {
        return captchaProvider;
    }

    public void setCaptchaProvider(ICaptchaProvider captchaProvider) {
        if (!initialized) {
            this.captchaProvider = captchaProvider;
        }
    }

    @Override
    public ICaptchaTokenGenerator getTokenGenerator() {
        return tokenGenerator;
    }

    public void setTokenGenerator(ICaptchaTokenGenerator tokenGenerator) {
        if (!initialized) {
            this.tokenGenerator = tokenGenerator;
        }
    }

    @Override
    public ICaptchaStorageAdapter getStorageAdapter() {
        return storageAdapter;
    }

    public void setStorageAdapter(ICaptchaStorageAdapter storageAdapter) {
        if (!initialized) {
            this.storageAdapter = storageAdapter;
        }
    }

    @Override
    public ICaptchaScopeProcessor getScopeProcessor() {
        return scopeProcessor;
    }

    public void setScopeProcessor(ICaptchaScopeProcessor scopeProcessor) {
        if (!initialized) {
            this.scopeProcessor = scopeProcessor;
        }
    }

    @Override
    public ICaptchaSendProvider getSendProvider() {
        return sendProvider;
    }

    public void setSendProvider(ICaptchaSendProvider sendProvider) {
        if (!initialized) {
            this.sendProvider = sendProvider;
        }
    }

    @Override
    public int getNeedCaptchaWrongTimes() {
        return needCaptchaWrongTimes;
    }

    public void setNeedCaptchaWrongTimes(int needCaptchaWrongTimes) {
        if (!initialized) {
            this.needCaptchaWrongTimes = needCaptchaWrongTimes;
        }
    }

    @Override
    public String getCacheNamePrefix() {
        return cacheNamePrefix;
    }

    public void setCacheNamePrefix(String cacheNamePrefix) {
        if (!initialized) {
            this.cacheNamePrefix = cacheNamePrefix;
        }
    }

    @Override
    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    @Override
    public boolean isServiceEnabled() {
        return serviceEnabled;
    }

    public void setServiceEnabled(boolean serviceEnabled) {
        this.serviceEnabled = serviceEnabled;
    }

    @Override
    public int getTokenLengthMin() {
        return tokenLengthMin;
    }

    public void setTokenLengthMin(int tokenLengthMin) {
        if (!initialized) {
            this.tokenLengthMin = tokenLengthMin;
        }
    }

    @Override
    public int getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(int tokenTimeout) {
        if (!initialized) {
            this.tokenTimeout = tokenTimeout;
        }
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (!initialized) {
            this.height = height;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (!initialized) {
            this.width = width;
        }
    }

    @Override
    public List<Color> getForegrounds() {
        return foregrounds;
    }

    public void addForeground(Color foreground) {
        if (!initialized && foreground != null) {
            this.foregrounds.add(foreground);
        }
    }

    public void addForegrounds(List<Color> foregrounds) {
        if (!initialized && foregrounds != null) {
            this.foregrounds.addAll(foregrounds);
        }
    }

    @Override
    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        if (!initialized) {
            this.background = background;
        }
    }

    @Override
    public Painter.Quality getQuality() {
        return quality;
    }

    public void setQuality(Painter.Quality quality) {
        if (!initialized) {
            this.quality = quality;
        }
    }

    @Override
    public Float getCompressRatio() {
        return compressRatio;
    }

    public void setCompressRatio(Float compressRatio) {
        if (!initialized) {
            this.compressRatio = compressRatio;
        }
    }

    @Override
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        if (!initialized) {
            this.format = format;
        }
    }

    @Override
    public List<Font> getFonts() {
        return fonts;
    }

    public void addFont(Font font) {
        if (!initialized && font != null) {
            this.fonts.add(font);
        }
    }

    public void addFonts(List<Font> fonts) {
        if (!initialized && fonts != null) {
            this.fonts.addAll(fonts);
        }
    }

    @Override
    public float[] getEffectScale() {
        return effectScale;
    }

    public void setEffectScale(float[] effectScale) {
        if (!initialized) {
            this.effectScale = effectScale;
        }
    }

    @Override
    public boolean isEffectRipple() {
        return effectRipple;
    }

    public void setEffectRipple(boolean effectRipple) {
        if (!initialized) {
            this.effectRipple = effectRipple;
        }
    }

    @Override
    public boolean isEffectBlur() {
        return effectBlur;
    }

    public void setEffectBlur(boolean effectBlur) {
        if (!initialized) {
            this.effectBlur = effectBlur;
        }
    }

    @Override
    public boolean isEffectOutline() {
        return effectOutline;
    }

    public void setEffectOutline(boolean effectOutline) {
        if (!initialized) {
            this.effectOutline = effectOutline;
        }
    }

    @Override
    public boolean isEffectRotate() {
        return effectRotate;
    }

    public void setEffectRotate(boolean effectRotate) {
        if (!initialized) {
            this.effectRotate = effectRotate;
        }
    }

    public static final class Builder {

        private final DefaultCaptchaConfig config = new DefaultCaptchaConfig();

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            config.setEnabled(enabled);
            return this;
        }

        public Builder captchaTypes(Collection<ICaptcha.Type> captchaTypes) {
            config.addCaptchaTypes(captchaTypes);
            return this;
        }

        public Builder developMode(boolean developMode) {
            config.setDevelopMode(developMode);
            return this;
        }

        public Builder captchaProvider(ICaptchaProvider captchaProvider) {
            config.setCaptchaProvider(captchaProvider);
            return this;
        }

        public Builder tokenGenerator(ICaptchaTokenGenerator tokenGenerator) {
            config.setTokenGenerator(tokenGenerator);
            return this;
        }

        public Builder storageAdapter(ICaptchaStorageAdapter storageAdapter) {
            config.setStorageAdapter(storageAdapter);
            return this;
        }

        public Builder scopeProcessor(ICaptchaScopeProcessor scopeProcessor) {
            config.setScopeProcessor(scopeProcessor);
            return this;
        }

        public Builder sendProvider(ICaptchaSendProvider sendProvider) {
            config.setSendProvider(sendProvider);
            return this;
        }

        public Builder needCaptchaWrongTimes(int needCaptchaWrongTimes) {
            config.setNeedCaptchaWrongTimes(needCaptchaWrongTimes);
            return this;
        }

        public Builder cacheNamePrefix(String cacheNamePrefix) {
            config.setCacheNamePrefix(cacheNamePrefix);
            return this;
        }

        public Builder tokenLengthMin(int tokenLengthMin) {
            config.setTokenLengthMin(tokenLengthMin);
            return this;
        }

        public Builder tokenTimeout(int tokenTimeout) {
            config.setTokenTimeout(tokenTimeout);
            return this;
        }

        public Builder servicePrefix(String servicePrefix) {
            config.setServicePrefix(servicePrefix);
            return this;
        }

        public Builder serviceEnabled(boolean serviceEnabled) {
            config.setServiceEnabled(serviceEnabled);
            return this;
        }

        public Builder height(int height) {
            config.setHeight(height);
            return this;
        }

        public Builder width(int width) {
            config.setWidth(width);
            return this;
        }

        public Builder foregrounds(List<Color> foregrounds) {
            config.addForegrounds(foregrounds);
            return this;
        }

        public Builder background(Color background) {
            config.setBackground(background);
            return this;
        }

        public Builder quality(Painter.Quality quality) {
            config.setQuality(quality);
            return this;
        }

        public Builder compressRatio(Float compressRatio) {
            config.setCompressRatio(compressRatio);
            return this;
        }

        public Builder format(String format) {
            config.setFormat(format);
            return this;
        }

        public Builder fonts(List<Font> fonts) {
            config.addFonts(fonts);
            return this;
        }

        public Builder effectScale(float[] effectScale) {
            config.setEffectScale(effectScale);
            return this;
        }

        public Builder effectRipple(boolean effectRipple) {
            config.setEffectRipple(effectRipple);
            return this;
        }

        public Builder effectBlur(boolean effectBlur) {
            config.setEffectBlur(effectBlur);
            return this;
        }

        public Builder effectOutline(boolean effectOutline) {
            config.setEffectOutline(effectOutline);
            return this;
        }

        public Builder effectRotate(boolean effectRotate) {
            config.setEffectRotate(effectRotate);
            return this;
        }

        public DefaultCaptchaConfig build() {
            return config;
        }
    }
}