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
package net.ymate.module.captcha.validate;

import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaConfig;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 10:55
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VCaptcha {

    /**
     * @return 作用域标识参数名称
     */
    String scopeName() default ICaptchaConfig.DEFAULT_CAPTCHA_SCOPE_PARAM_NAME;

    /**
     * @return 验证码类型
     */
    ICaptcha.Type type() default ICaptcha.Type.DEFAULT;

    /**
     * @return 目标参数名称(当验证手机号码或邮件地址时使用)
     */
    String targetName() default StringUtils.EMPTY;

    /**
     * @return 方法被调用后是否使Token失效
     */
    boolean invalid() default false;

    /**
     * @return 是否允许跳过
     */
    boolean allowSkip() default true;

    /**
     * @return 自定义验证消息
     */
    String msg() default StringUtils.EMPTY;
}