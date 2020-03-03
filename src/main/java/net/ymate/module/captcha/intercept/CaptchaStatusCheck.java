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
package net.ymate.module.captcha.intercept;

import net.ymate.module.captcha.ICaptcha;
import net.ymate.platform.core.beans.annotation.InterceptAnnotation;
import net.ymate.platform.core.beans.intercept.IInterceptor;

import java.lang.annotation.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/03/03 15:31
 * @since 2.0.0
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InterceptAnnotation(IInterceptor.Direction.BEFORE)
public @interface CaptchaStatusCheck {

    /**
     * @return 检查指定类型是否启用, 默认为ALL表示仅检查模块是否启用
     */
    ICaptcha.Type type() default ICaptcha.Type.ALL;
}