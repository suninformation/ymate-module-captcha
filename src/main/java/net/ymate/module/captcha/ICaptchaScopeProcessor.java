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

/**
 * 作用域标识扩展处理器
 *
 * @author 刘镇 (suninformation@163.com) on 2017/04/13 10:00
 */
@Ignored
public interface ICaptchaScopeProcessor extends IInitialization<ICaptcha> {

    /**
     * 判断指定令牌标识的验证码是否跳过验证
     *
     * @param type  验证码类型
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     * @return 返回true表示跳过验证
     */
    boolean isNeedSkipValidation(ICaptcha.Type type, String scope);

    /**
     * 重置错误计数器
     *
     * @param type  验证码类型
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     */
    void resetWrongTimes(ICaptcha.Type type, String scope);

    /**
     * 判断指定令牌标识的手机号码或邮件地址是否允许发送验证码
     *
     * @param type   验证码类型
     * @param scope  作用域标识，用于区分不同客户端及数据存储范围
     * @param target 手机号码或邮件地址
     * @return 返回true表示允许发送
     */
    boolean isAllowSendCode(ICaptcha.Type type, String scope, String target);
}
