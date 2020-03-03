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
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.core.configuration.IConfigReader;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘镇 (suninformation@163.com) on 2019-08-03 00:34
 * @since 1.0.2
 */
public class FileCaptchaFontsParser implements ICaptchaFontsParser {

    private int parseFontFormat(String fontFormat) {
        if (FONT_BOLD.equalsIgnoreCase(fontFormat)) {
            return Font.BOLD;
        } else if (FONT_ITALIC.equalsIgnoreCase(fontFormat)) {
            return Font.ITALIC;
        }
        return Font.PLAIN;
    }

    private int parseFontStyle(String fontStyle) {
        if (TYPE1_FONT.equalsIgnoreCase(fontStyle)) {
            return Font.TYPE1_FONT;
        }
        return Font.TRUETYPE_FONT;
    }

    @Override
    public List<Font> parse(IConfigReader configReader, int fontSize) throws Exception {
        List<Font> fonts = new ArrayList<>();
        String[] fontPathArr = configReader.getArray(ICaptchaConfig.FONTS);
        if (fontPathArr != null) {
            for (String fontPath : fontPathArr) {
                String[] fArr = StringUtils.split(fontPath, ",");
                if (fArr != null) {
                    int fontFormat = Font.TRUETYPE_FONT;
                    int fontStyle = Font.PLAIN;
                    switch (fArr.length) {
                        case 1:
                            fontPath = fArr[0];
                            break;
                        case 2:
                            fontFormat = parseFontFormat(fArr[0]);
                            fontPath = fArr[1];
                            break;
                        case 3:
                            fontFormat = parseFontFormat(fArr[0]);
                            fontStyle = parseFontStyle(fArr[1]);
                            fontPath = fArr[2];
                            break;
                        default:
                            throw new IllegalArgumentException(String.format("Illegal argument '%s'.", ICaptchaConfig.FONTS));
                    }
                    if (StringUtils.isNotBlank(fontPath)) {
                        fontPath = RuntimeUtils.replaceEnvVariable(fontPath);
                        fonts.add(Font.createFont(fontFormat, new File(fontPath)).deriveFont(fontStyle, fontSize));
                    }
                }
            }
        }
        return fonts;
    }
}
