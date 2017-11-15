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
 * 验证码发送能力接口(主要用于发送短信或邮件等验证码)
 *
 * @author 刘镇 (suninformation@163.com) on 17/5/2 上午10:46
 * @version 1.0
 */
public interface ICaptchaSendProvider {

    /**
     * 初始化
     *
     * @param owner 所属模块管理器
     * @throws Exception 可能产生的任何异常
     */
    void init(ICaptcha owner) throws Exception;

    /**
     * 发送验证码
     *
     * @param type    验证码类型
     * @param scope   作用域标识，用于区分不同客户端及数据存储范围
     * @param target  发送目标
     * @param captcha 验证码
     * @throws Exception 可能产生的任何异常
     */
    void send(ICaptcha.Type type, String scope, String target, String captcha) throws Exception;
}
