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
package net.ymate.module.captcha.intercept;

import net.ymate.module.captcha.Captcha;
import net.ymate.platform.core.beans.intercept.IInterceptor;
import net.ymate.platform.core.beans.intercept.InterceptContext;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 刘镇 (suninformation@163.com) on 2017/8/1 上午10:21
 * @version 1.0
 */
public class CaptchaStatusInterceptor implements IInterceptor {

    @Override
    public Object intercept(InterceptContext context) throws Exception {
        IView _view = null;
        switch (context.getDirection()) {
            case BEFORE:
                if (Captcha.get().isDisabled()) {
                    _view = View.httpStatusView(HttpServletResponse.SC_FORBIDDEN, "Captcha module has been disabled");
                }
                break;
        }
        return _view;
    }
}
