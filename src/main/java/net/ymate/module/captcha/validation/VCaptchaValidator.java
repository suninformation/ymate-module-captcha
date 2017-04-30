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
package net.ymate.module.captcha.validation;

import net.ymate.module.captcha.Captcha;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.platform.core.beans.annotation.CleanProxy;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.util.RuntimeUtils;
import net.ymate.platform.validation.AbstractValidator;
import net.ymate.platform.validation.ValidateContext;
import net.ymate.platform.validation.ValidateResult;
import net.ymate.platform.validation.annotation.Validator;
import org.apache.commons.lang.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午11:11
 * @version 1.0
 */
@Validator(VCaptcha.class)
@CleanProxy
public class VCaptchaValidator extends AbstractValidator {

    public ValidateResult validate(ValidateContext context) {
        boolean _matched = false;
        VCaptcha _vCaptcha = (VCaptcha) context.getAnnotation();
        if (!Captcha.get().isValidationNeedSkip(_vCaptcha.tokenId())) {
            if (context.getParamValue() != null) {
                String _token = null;
                if (context.getParamValue().getClass().isArray()) {
                    Object[] _objArr = (Object[]) context.getParamValue();
                    if (_objArr.length > 0) {
                        _token = BlurObject.bind(_objArr[0]).toStringValue();
                    }
                } else {
                    _token = BlurObject.bind(context.getParamValue()).toStringValue();
                }
                try {
                    if (!ICaptcha.Status.MATCHED.equals(Captcha.get().validate(_vCaptcha.tokenId(), _token, _vCaptcha.invalid()))) {
                        _matched = true;
                    }
                } catch (Exception e) {
                    throw new Error(RuntimeUtils.unwrapThrow(e));
                }
            } else {
                _matched = true;
            }
            if (_matched) {
                String _pName = StringUtils.defaultIfBlank(context.getParamLabel(), context.getParamName());
                _pName = __doGetI18nFormatMessage(context, _pName, _pName);
                String _msg = StringUtils.trimToNull(_vCaptcha.msg());
                if (_msg != null) {
                    _msg = __doGetI18nFormatMessage(context, _msg, _msg, _pName);
                } else {
                    _msg = __doGetI18nFormatMessage(context, "ymp.validation.captcha_expired", "{0} is invalid or has expired.", _pName);
                }
                return new ValidateResult(context.getParamName(), _msg);
            }
        }
        return null;
    }
}
