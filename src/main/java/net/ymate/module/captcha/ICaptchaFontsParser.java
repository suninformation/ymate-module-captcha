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
package net.ymate.module.captcha;

import net.ymate.platform.core.beans.annotation.Ignored;
import net.ymate.platform.core.configuration.IConfigReader;

import java.awt.*;
import java.util.List;

/**
 * 自定义字体配置分析器
 *
 * @author 刘镇 (suninformation@163.com) on 2019-08-02 23:56
 * @since 1.0.2
 */
@Ignored
public interface ICaptchaFontsParser {

    String FONT_BOLD = "bold";

    String FONT_ITALIC = "italic";

    String TYPE1_FONT = "type1";

    /**
     * 分析字体配置
     *
     * @param configReader 配置读取器
     * @param fontSize     字号
     * @return 返回字体对象集合
     * @throws Exception 可能产生的任何异常
     */
    List<Font> parse(IConfigReader configReader, int fontSize) throws Exception;
}
