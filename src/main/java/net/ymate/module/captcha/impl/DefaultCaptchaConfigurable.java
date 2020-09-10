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
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.core.module.impl.DefaultModuleConfigurable;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/03/03 13:17
 * @since 2.0.0
 */
public final class DefaultCaptchaConfigurable extends DefaultModuleConfigurable {

    public static Builder builder() {
        return new Builder();
    }

    private DefaultCaptchaConfigurable() {
        super(ICaptcha.MODULE_NAME);
    }

    public static final class Builder {

        private final DefaultCaptchaConfigurable configurable = new DefaultCaptchaConfigurable();

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            configurable.addConfig(ICaptchaConfig.ENABLED, String.valueOf(enabled));
            return this;
        }

        public Builder captchaTypes(Set<ICaptcha.Type> captchaTypes) {
            configurable.addConfig(ICaptchaConfig.CAPTCHA_TYPES, captchaTypes.stream().map(Enum::name).collect(Collectors.joining("|")));
            return this;
        }

        public Builder developMode(boolean developMode) {
            configurable.addConfig(ICaptchaConfig.DEV_MODE, String.valueOf(developMode));
            return this;
        }

        public Builder captchaProviderClass(Class<? extends ICaptchaProvider> captchaProviderClass) {
            configurable.addConfig(ICaptchaConfig.PROVIDER_CLASS, captchaProviderClass.getName());
            return this;
        }

        public Builder tokenGeneratorClass(Class<? extends ICaptchaTokenGenerator> tokenGeneratorClass) {
            configurable.addConfig(ICaptchaConfig.STORAGE_ADAPTER_CLASS, tokenGeneratorClass.getName());
            return this;
        }

        public Builder storageAdapterClass(Class<? extends ICaptchaStorageAdapter> storageAdapterClass) {
            configurable.addConfig(ICaptchaConfig.TOKEN_GENERATOR_CLASS, storageAdapterClass.getName());
            return this;
        }

        public Builder scopeProcessorClass(Class<? extends ICaptchaScopeProcessor> scopeProcessorClass) {
            configurable.addConfig(ICaptchaConfig.SCOPE_PROCESSOR_CLASS, scopeProcessorClass.getName());
            return this;
        }

        public Builder sendProviderClass(Class<? extends ICaptchaSendProvider> sendProviderClass) {
            configurable.addConfig(ICaptchaConfig.SEND_PROCESSOR_CLASS, sendProviderClass.getName());
            return this;
        }

        public Builder needCaptchaWrongTimes(int needCaptchaWrongTimes) {
            configurable.addConfig(ICaptchaConfig.NEED_CAPTCHA_WRONG_TIMES, String.valueOf(needCaptchaWrongTimes));
            return this;
        }

        public Builder cacheNamePrefix(String cacheNamePrefix) {
            configurable.addConfig(ICaptchaConfig.CACHE_NAME_PREFIX, cacheNamePrefix);
            return this;
        }

        public Builder servicePrefix(String servicePrefix) {
            configurable.addConfig(ICaptchaConfig.SERVICE_PREFIX, servicePrefix);
            return this;
        }

        public Builder serviceEnabled(boolean serviceEnabled) {
            configurable.addConfig(ICaptchaConfig.SERVICE_ENABLED, String.valueOf(serviceEnabled));
            return this;
        }

        public Builder tokenLengthMin(int tokenLengthMin) {
            configurable.addConfig(ICaptchaConfig.TOKEN_LENGTH_MIN, String.valueOf(tokenLengthMin));
            return this;
        }

        public Builder tokenTimeout(int tokenTimeout) {
            configurable.addConfig(ICaptchaConfig.TOKEN_TIMEOUT, String.valueOf(tokenTimeout));
            return this;
        }

        public Builder height(int height) {
            configurable.addConfig(ICaptchaConfig.HEIGHT, String.valueOf(height));
            return this;
        }

        public Builder width(int width) {
            configurable.addConfig(ICaptchaConfig.WIDTH, String.valueOf(width));
            return this;
        }

        public Builder foregrounds(List<Color> foregrounds) {
            List<String> colors = new ArrayList<>();
            for (Color color : foregrounds) {
                colors.add(String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue()));
            }
            configurable.addConfig(ICaptchaConfig.FOREGROUNDS, StringUtils.join(colors, '|'));
            return this;
        }

        public Builder background(Color background) {
            configurable.addConfig(ICaptchaConfig.BACKGROUND, String.format("%d,%d,%d", background.getRed(), background.getGreen(), background.getBlue()));
            return this;
        }

        public Builder quality(Painter.Quality quality) {
            configurable.addConfig(ICaptchaConfig.QUALITY, quality.name());
            return this;
        }

        public Builder compressRatio(Float compressRatio) {
            configurable.addConfig(ICaptchaConfig.COMPRESS_RATIO, compressRatio.toString());
            return this;
        }

        public Builder format(String format) {
            configurable.addConfig(ICaptchaConfig.FORMAT, format);
            return this;
        }

        public Builder fontsParserClass(Class<? extends ICaptchaFontsParser> fontsParserClass) {
            configurable.addConfig(ICaptchaConfig.FONTS_PARSER_CLASS, fontsParserClass.getName());
            return this;
        }

        public Builder fonts(List<String> fonts) {
            configurable.addConfig(ICaptchaConfig.FONTS, StringUtils.join(fonts, '|'));
            return this;
        }

        public Builder effectScale(float x, float y) {
            configurable.addConfig(ICaptchaConfig.EFFECT_SCALE, StringUtils.join(new float[]{x, y}, ','));
            return this;
        }

        public Builder effectRipple(boolean effectRipple) {
            configurable.addConfig(ICaptchaConfig.EFFECT_RIPPLE, String.valueOf(effectRipple));
            return this;
        }

        public Builder effectBlur(boolean effectBlur) {
            configurable.addConfig(ICaptchaConfig.EFFECT_BLUR, String.valueOf(effectBlur));
            return this;
        }

        public Builder effectOutline(boolean effectOutline) {
            configurable.addConfig(ICaptchaConfig.EFFECT_OUTLINE, String.valueOf(effectOutline));
            return this;
        }

        public Builder effectRotate(boolean effectRotate) {
            configurable.addConfig(ICaptchaConfig.EFFECT_ROTATE, String.valueOf(effectRotate));
            return this;
        }

        public IModuleConfigurer build() {
            return configurable.toModuleConfigurer();
        }
    }
}