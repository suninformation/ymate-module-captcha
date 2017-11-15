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
package net.ymate.module.captcha;

/**
 * 作用域标识扩展处理器
 *
 * @author 刘镇 (suninformation@163.com) on 17/4/13 上午10:00
 * @version 1.0
 */
public interface ICaptchaScopeProcessor {

    /**
     * 初始化
     *
     * @param owner 所属模块管理器对象
     * @throws Exception 可能产生的任何异常
     */
    void init(ICaptcha owner) throws Exception;

    /**
     * @param type  验证码类型
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     * @return 判断指定令牌标识的验证码是否跳过验证
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
     * @param type   验证码类型
     * @param scope  作用域标识，用于区分不同客户端及数据存储范围
     * @param target 手机号码或邮件地址
     * @return 判断指定令牌标识的手机号码或邮件地址是否允许发送验证码
     */
    boolean isAllowSendCode(ICaptcha.Type type, String scope, String target);
}
