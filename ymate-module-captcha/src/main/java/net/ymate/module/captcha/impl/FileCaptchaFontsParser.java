/*
 * Copyright 2007-2019 the original author or authors.
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

import net.ymate.module.captcha.ICaptchaFontsParser;
import net.ymate.module.captcha.ICaptchaModuleCfg;
import net.ymate.platform.core.support.IConfigReader;
import net.ymate.platform.core.util.RuntimeUtils;
import org.apache.commons.lang.StringUtils;

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
        if ("bold".equalsIgnoreCase(fontFormat)) {
            return Font.BOLD;
        } else if ("italic".equalsIgnoreCase(fontFormat)) {
            return Font.ITALIC;
        }
        return Font.PLAIN;
    }

    private int parseFontStyle(String fontStyle) {
        if ("type1".equalsIgnoreCase(fontStyle)) {
            return Font.TYPE1_FONT;
        }
        return Font.TRUETYPE_FONT;
    }

    @Override
    public List<Font> parse(IConfigReader configReader, int fontSize) throws Exception {
        List<Font> fonts = new ArrayList<Font>();
        String[] _fontPathArr = configReader.getArray(ICaptchaModuleCfg.FONTS);
        if (_fontPathArr != null) {
            for (String _fontPath : _fontPathArr) {
                String[] _fArr = StringUtils.split(_fontPath, ",");
                if (_fArr != null) {
                    int _fontFormat = Font.TRUETYPE_FONT;
                    int _fontStyle = Font.PLAIN;
                    switch (_fArr.length) {
                        case 1:
                            _fontPath = _fArr[0];
                            break;
                        case 2:
                            _fontFormat = parseFontFormat(_fArr[0]);
                            _fontPath = _fArr[1];
                            break;
                        case 3:
                            _fontFormat = parseFontFormat(_fArr[0]);
                            _fontStyle = parseFontStyle(_fArr[1]);
                            _fontPath = _fArr[2];
                            break;
                        default:
                            throw new IllegalArgumentException(String.format("Illegal argument '%s'.", ICaptchaModuleCfg.FONTS));
                    }
                    if (StringUtils.isNotBlank(_fontPath)) {
                        _fontPath = RuntimeUtils.replaceEnvVariable(_fontPath);
                        fonts.add(Font.createFont(_fontFormat, new File(_fontPath)).deriveFont(_fontStyle, fontSize));
                    }
                }
            }
        }
        return fonts;
    }
}
