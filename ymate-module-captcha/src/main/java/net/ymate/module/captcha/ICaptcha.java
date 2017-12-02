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

import net.ymate.platform.core.YMP;

import java.io.OutputStream;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午4:25
 * @version 1.0
 */
public interface ICaptcha {

    String MODULE_NAME = "module.captcha";

    /**
     * @return 返回所属YMP框架管理器实例
     */
    YMP getOwner();

    /**
     * @return 返回模块配置对象
     */
    ICaptchaModuleCfg getModuleCfg();

    /**
     * @return 返回模块是否已初始化
     */
    boolean isInited();

    /**
     * @param scope  作用域标识，用于区分不同客户端及数据存储范围
     * @param output 输出流对象
     * @return 生成图片并返回验证码
     * @throws Exception 可能产生的任何异常
     */
    CaptchaTokenBean generate(String scope, OutputStream output) throws Exception;

    /**
     * @param scope  作用域标识，用于区分不同客户端及数据存储范围
     * @param target 目标(当验证手机号码或邮件地址时使用)
     * @return 生成并返回验证码
     * @throws Exception 可能产生的任何异常
     */
    CaptchaTokenBean generate(String scope, String target) throws Exception;

    CaptchaTokenBean generate(String scope) throws Exception;

    /**
     * @return 生成自定义验证码字符串, 若未配置自定义token生成器则返回null
     */
    String generateToken();

    /**
     * 清除验证码数据
     *
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     * @throws Exception 可能产生的任何异常
     */
    void invalidate(String scope) throws Exception;

    /**
     * 检查验证码有效性
     *
     * @param scope   作用域标识，用于区分不同客户端及数据存储范围
     * @param target  目标(当验证手机号码或邮件地址时使用)
     * @param token   验证码
     * @param invalid 方法被调用后是否使Token失效
     * @return 返回验证码状态
     * @throws Exception 可能产生的任何异常
     */
    Status validate(String scope, String target, String token, boolean invalid) throws Exception;

    Status validate(String scope, String token, boolean invalid) throws Exception;

    /**
     * 获取验证码令牌对象
     *
     * @param type   验证码类型
     * @param scope  作用域标识，用于区分不同客户端及数据存储范围
     * @param target 目标(手机号码或邮件地址)
     * @return 返回验证码令牌对象若不存在或已过期则重新生成
     * @throws Exception 可能产生的任何异常
     */
    CaptchaTokenBean getCaptchaToken(ICaptcha.Type type, String scope, String target) throws Exception;

    /**
     * 发送验证码
     *
     * @param type      验证码类型
     * @param scope     作用域标识，用于区分不同客户端及数据存储范围
     * @param tokenBean 验证码令牌对象
     * @return 返回发送是否成功
     * @throws Exception 可能产生的任何异常
     */
    boolean captchaSend(ICaptcha.Type type, String scope, CaptchaTokenBean tokenBean) throws Exception;

    /**
     * @return 是否已禁用验证码模块
     */
    boolean isDisabled();

    /**
     * @return 是否开启错误计数特性
     */
    boolean isWrongTimesEnabled();

    /**
     * @param type  验证码类型
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     * @return 当开启错误计数特性时判断是否需要跳过当前tokeId的验证, 否则将永远返回false
     */
    boolean isValidationNeedSkip(ICaptcha.Type type, String scope);

    /**
     * 重置错误计数器
     *
     * @param type  验证码类型
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     */
    void resetWrongTimes(ICaptcha.Type type, String scope);

    /**
     * 验证码状态
     */
    enum Status {

        /**
         * 正常
         */
        NORMAL,

        /**
         * 匹配
         */
        MATCHED,

        /**
         * 无效
         */
        INVALID,

        /**
         * 过期
         */
        EXPIRED
    }

    /**
     * 验证码类型
     */
    enum Type {

        /**
         * 默认(图片)
         */
        DEFAULT,

        /**
         * 短信
         */
        SMS,

        /**
         * 邮件
         */
        MAIL
    }
}
