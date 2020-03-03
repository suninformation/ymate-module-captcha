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

import net.ymate.module.captcha.Captcha;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.platform.core.beans.annotation.Inject;
import net.ymate.platform.core.beans.intercept.AbstractInterceptor;
import net.ymate.platform.core.beans.intercept.InterceptContext;
import net.ymate.platform.webmvc.view.View;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/08/01 10:21
 */
public final class CaptchaStatusCheckInterceptor extends AbstractInterceptor {

    @Inject
    private Captcha captcha;

    @Override
    protected Object before(InterceptContext context) {
        boolean matched = captcha.isDisabled();
        if (!matched) {
            CaptchaStatusCheck ann = findInterceptAnnotation(context, CaptchaStatusCheck.class);
            if (ann != null && !ICaptcha.Type.ALL.equals(ann.type())) {
                matched = !captcha.checkType(ann.type());
            }
        }
        if (matched) {
            return View.httpStatusView(HttpServletResponse.SC_FORBIDDEN);
        }
        return null;
    }

    @Override
    protected Object after(InterceptContext context) {
        return null;
    }
}