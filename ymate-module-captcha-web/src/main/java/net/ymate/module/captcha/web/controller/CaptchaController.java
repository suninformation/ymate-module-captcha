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
package net.ymate.module.captcha.web.controller;

import net.ymate.framework.validation.VMobile;
import net.ymate.framework.webmvc.ErrorCode;
import net.ymate.framework.webmvc.WebResult;
import net.ymate.module.captcha.Captcha;
import net.ymate.module.captcha.CaptchaTokenBean;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.web.intercept.CaptchaStatusInterceptor;
import net.ymate.platform.core.beans.annotation.Before;
import net.ymate.platform.core.util.RuntimeUtils;
import net.ymate.platform.validation.validate.VEmail;
import net.ymate.platform.validation.validate.VLength;
import net.ymate.platform.validation.validate.VRequired;
import net.ymate.platform.webmvc.annotation.Controller;
import net.ymate.platform.webmvc.annotation.RequestMapping;
import net.ymate.platform.webmvc.annotation.RequestParam;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;
import net.ymate.platform.webmvc.view.impl.BinaryView;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午9:14
 * @version 1.0
 */
@Controller
@RequestMapping("/captcha")
@Before(CaptchaStatusInterceptor.class)
public class CaptchaController {

    private static final Log _LOG = LogFactory.getLog(CaptchaController.class);

    private IView __doCaptchaSend(ICaptcha.Type type, String scope, String target) throws Exception {
        CaptchaTokenBean _tokenBean = Captcha.get().getCaptchaToken(type, scope, target);
        if (_tokenBean != null) {
            try {
                if (Captcha.get().captchaSend(type, scope, _tokenBean)) {
                    return WebResult.formatView(WebResult.SUCCESS(), "json");
                }
                return WebResult.formatView(WebResult.CODE(ErrorCode.REQUEST_OPERATION_FORBIDDEN), "json");
            } catch (Exception e) {
                _LOG.warn("An exception occurred at send to " + target, RuntimeUtils.unwrapThrow(e));
            }
        }
        return WebResult.formatView(WebResult.CODE(ErrorCode.INTERNAL_SYSTEM_ERROR), "json");
    }

    private String __doCaptchaBase64(String contentType, ByteArrayOutputStream outputStream) {
        return "data:" + contentType + ";base64," + Base64.encodeBase64String(outputStream.toByteArray());
    }

    /**
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     * @param type  输出类型，取值范围：空|data|json，当type=data或type=json时采用Base64编码输出图片
     * @return 返回生成的验证码图片
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping("/")
    public IView create(@VLength(max = 32) @RequestParam String scope,

                        @RequestParam String type) throws Exception {

        ICaptcha _captcha = Captcha.get();
        //
        ByteArrayOutputStream _output = new ByteArrayOutputStream();
        CaptchaTokenBean _bean = _captcha.generate(scope, _output);
        //
        String _contentType = "image/" + _captcha.getModuleCfg().getFormat();
        //
        if (StringUtils.equalsIgnoreCase(type, "data")) {
            return View.textView(__doCaptchaBase64(_contentType, _output));
        } else if (StringUtils.equalsIgnoreCase(type, "json")) {
            return WebResult.SUCCESS()
                    .dataAttr("scope", _bean.getScope())
                    .dataAttr("captcha", __doCaptchaBase64(_contentType, _output)).toJSON();
        }
        return new BinaryView(new ByteArrayInputStream(_output.toByteArray()), _output.size()).setContentType(_contentType);
    }

    /**
     * @param scope  作用域标识，用于区分不同客户端及数据存储范围
     * @param mobile 手机号码
     * @return 发送手机短信验证码, ret=0表示发送成功, ret=-1表示参数验证错误, ret=-6表示发送频率过快或其它消息, ret=-50表示发送异常
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/sms_code", method = Type.HttpMethod.POST)
    public IView sms(@VLength(max = 32) @RequestParam String scope,

                     @VRequired @VMobile @RequestParam String mobile) throws Exception {

        return __doCaptchaSend(ICaptcha.Type.SMS, scope, mobile);
    }

    /**
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     * @param email 邮箱地址
     * @return 发送邮件验证码, ret=0表示发送成功, ret=-1表示参数验证错误, ret=-6表示发送频率过快或其它消息, ret=-50表示发送异常
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/mail_code", method = Type.HttpMethod.POST)
    public IView mail(@VLength(max = 32) @RequestParam String scope,

                      @VRequired @VEmail @RequestParam String email) throws Exception {

        return __doCaptchaSend(ICaptcha.Type.MAIL, scope, email);
    }

    /**
     * @param scope  作用域标识，用于区分不同客户端及数据存储范围
     * @param target 目标(当验证手机号码或邮件地址时使用)
     * @param token  预验证的令牌值
     * @return 返回判断token是否匹配的验证结果（主要用于客户端验证）
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/match", method = {Type.HttpMethod.GET, Type.HttpMethod.POST})
    public IView match(@VLength(max = 32) @RequestParam String scope,

                       @VLength(max = 50) @RequestParam String target,

                       @VLength(max = 10) @RequestParam String token) throws Exception {

        return WebResult.formatView(WebResult.SUCCESS()
                .dataAttr("matched", ICaptcha.Status.MATCHED.equals(Captcha.get().validate(scope, target, token, false))), "json");
    }
}
