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

import net.ymate.module.captcha.Captcha;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaConfig;
import net.ymate.platform.commons.lang.BlurObject;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.core.beans.annotation.CleanProxy;
import net.ymate.platform.core.beans.annotation.Inject;
import net.ymate.platform.validation.AbstractValidator;
import net.ymate.platform.validation.ValidateContext;
import net.ymate.platform.validation.ValidateResult;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.CookieHelper;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 11:11
 */
@CleanProxy
public class CaptchaValidator extends AbstractValidator {

    private static final String I18N_MESSAGE_KEY = "ymp.validation.captcha_invalid";

    private static final String I18N_MESSAGE_DEFAULT_VALUE = "{0} is invalid or expired.";

    @Inject
    private Captcha captcha;

    public static boolean validate(ICaptcha captcha, ICaptcha.Type type, boolean allowSkip, String scope, String target, String token, boolean invalid) throws Exception {
        if (allowSkip && captcha.isValidationNeedSkip(type, scope)) {
            return true;
        }
        return ICaptcha.Status.MATCHED.equals(captcha.validate(scope, target, token, invalid));
    }

    private String getCaptchaScope(String scopeName) {
        HttpServletRequest httpServletRequest = WebContext.getRequest();
        // 尝试从请求参数中获取验证码作用域
        String scopeStr = httpServletRequest.getParameter(scopeName);
        if (StringUtils.isBlank(scopeStr)) {
            // 尝试从请求头中获取验证码作用域
            scopeStr = httpServletRequest.getHeader(ICaptchaConfig.DEFAULT_CAPTCHA_SCOPE_HEADER_NAME);
            if (StringUtils.isBlank(scopeStr)) {
                // 最后从Cookie中获取验证码作用域
                scopeStr = CookieHelper.bind(WebContext.getContext().getOwner())
                        .getCookie(ICaptchaConfig.DEFAULT_CAPTCHA_SCOPE_COOKIE_NAME)
                        .toStringValue();
            }
        }
        return scopeStr;
    }

    @Override
    public ValidateResult validate(ValidateContext context) {
        Object paramValue = context.getParamValue();
        VCaptcha ann = (VCaptcha) context.getAnnotation();
        boolean matched;
        try {
            String scope = getCaptchaScope(ann.scopeName());
            String token = paramValue == null ? null : paramValue.getClass().isArray() ? getParamValue(paramValue, false) : BlurObject.bind(paramValue).toStringValue();
            String target = ann.targetName();
            if (StringUtils.isNotBlank(target)) {
                target = WebContext.getRequest().getParameter(target);
            }
            matched = !validate(captcha, ann.type(), ann.allowSkip(), scope, target, token, ann.invalid());
        } catch (Exception e) {
            throw new Error(RuntimeUtils.unwrapThrow(e));
        }
        if (matched) {
            return ValidateResult.builder(context, ann.msg(), I18N_MESSAGE_KEY, I18N_MESSAGE_DEFAULT_VALUE).matched(true).build();
        }
        return null;
    }
}