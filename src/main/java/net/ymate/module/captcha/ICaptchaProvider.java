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
import net.ymate.platform.core.support.IInitialization;

import java.io.OutputStream;

/**
 * 验证码图片生成能力接口
 *
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 05:22
 */
@Ignored
public interface ICaptchaProvider extends IInitialization<ICaptcha> {

    /**
     * 生成验证码图片
     *
     * @param output 输出流对象
     * @return 返回验证码字符串
     * @throws Exception 可能产生的任何异常
     */
    String createCaptcha(OutputStream output) throws Exception;
}
