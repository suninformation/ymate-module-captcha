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
 * @author 刘镇 (suninformation@163.com) on 17/4/6 上午11:37
 * @version 1.0
 */
public interface ICaptchaSmsSendProvider {

    /**
     * 初始化
     *
     * @param owner 所属模块管理器
     * @throws Exception 可以产生的任何异常
     */
    void init(ICaptcha owner) throws Exception;

    /**
     * 发送短信验证码
     *
     * @param mobile  手机号码
     * @param captcha 验证码
     * @throws Exception 可能产生的任何异常
     */
    void send(String mobile, String captcha) throws Exception;
}
