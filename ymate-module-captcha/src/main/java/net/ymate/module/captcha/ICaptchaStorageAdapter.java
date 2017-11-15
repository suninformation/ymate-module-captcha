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
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午5:21
 * @version 1.0
 */
public interface ICaptchaStorageAdapter {

    /**
     * 初始化
     *
     * @param owner 所属模块管理器
     * @throws Exception 可能产生的任何异常
     */
    void init(ICaptcha owner) throws Exception;

    /**
     * 获取指定作用域下的验证码令牌对象
     *
     * @param scope 作用域标识
     * @return 返回验证码令牌对象
     * @throws Exception 可能产生的任何异常
     */
    CaptchaTokenBean load(String scope) throws Exception;

    /**
     * 存储验证码令牌
     *
     * @param scope  作用域标识
     * @param target 目标
     * @param token  令牌
     * @return 返回生成的验证码令牌对象
     * @throws Exception 可能产生的任何异常
     */
    CaptchaTokenBean saveOrUpdate(String scope, String target, String token) throws Exception;

    /**
     * 清除指定作用域下的验证码令牌对象
     *
     * @param scope 作用域标识
     * @throws Exception 可能产生的任何异常
     */
    void cleanup(String scope) throws Exception;
}
