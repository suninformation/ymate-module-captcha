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

import net.ymate.module.captcha.ICaptchaConfig;
import net.ymate.module.captcha.ICaptchaFontsParser;
import net.ymate.platform.core.configuration.IConfigReader;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 2019-08-03 00:02
 * @since 1.0.2
 */
public class DefaultCaptchaFontsParser implements ICaptchaFontsParser {

    @Override
    public List<Font> parse(IConfigReader configReader, int fontSize) throws Exception {
        List<Font> fonts = new ArrayList<>();
        String[] fontArr = configReader.getArray(ICaptchaConfig.FONTS);
        if (fontArr != null) {
            for (String font : fontArr) {
                String[] fArr = StringUtils.split(font, ",");
                if (fArr != null) {
                    int fontStyle = Font.PLAIN;
                    if (fArr.length > 1) {
                        if (FONT_BOLD.equalsIgnoreCase(fArr[1])) {
                            fontStyle = Font.BOLD;
                        } else if (FONT_ITALIC.equalsIgnoreCase(fArr[1])) {
                            fontStyle = Font.ITALIC;
                        }
                    }
                    fonts.add(new Font(fArr[0], fontStyle, fontSize));
                }
            }
        }
        return fonts;
    }
}
