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
import org.apache.commons.lang.StringUtils;

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
        List<Font> fonts = new ArrayList<Font>();
        String[] _fontArr = configReader.getArray(ICaptchaModuleCfg.FONTS);
        if (_fontArr != null) {
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
                    fonts.add(new Font(_fArr[0], _fontStyle, fontSize));
                }
            }
        }
        return fonts;
    }
}
