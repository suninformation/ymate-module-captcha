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
package net.ymate.module.captcha.controller;

import net.ymate.module.captcha.Captcha;
import net.ymate.module.captcha.CaptchaTokenBean;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaConfig;
import net.ymate.module.captcha.intercept.CaptchaStatusCheck;
import net.ymate.module.captcha.validate.VCaptcha;
import net.ymate.platform.commons.util.RuntimeUtils;
import net.ymate.platform.core.beans.annotation.Inject;
import net.ymate.platform.core.support.ErrorCode;
import net.ymate.platform.validation.annotation.VField;
import net.ymate.platform.validation.validate.*;
import net.ymate.platform.webmvc.annotation.RequestMapping;
import net.ymate.platform.webmvc.annotation.RequestParam;
import net.ymate.platform.webmvc.base.Type;
import net.ymate.platform.webmvc.util.WebErrorCode;
import net.ymate.platform.webmvc.util.WebResult;
import net.ymate.platform.webmvc.view.IView;
import net.ymate.platform.webmvc.view.View;
import net.ymate.platform.webmvc.view.impl.BinaryView;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 09:14
 */
@RequestMapping("/captcha")
public class CaptchaController {

    private static final Log LOG = LogFactory.getLog(CaptchaController.class);

    private static final String TYPE_DATA = "data";

    private static final String TYPE_JSON = "json";

    @Inject
    private Captcha captcha;

    private IView sendCaptcha(ICaptcha.Type type, String scope, String target) throws Exception {
        CaptchaTokenBean tokenBean = captcha.getCaptchaToken(type, scope, target);
        if (tokenBean != null) {
            try {
                if (captcha.captchaSend(type, scope, tokenBean)) {
                    return WebResult.succeed().withContentType().toJsonView();
                }
                return WebResult.create(WebErrorCode.requestOperationForbidden()).withContentType().toJsonView();
            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(String.format("An exception occurred at send to %s", target), RuntimeUtils.unwrapThrow(e));
                }
            }
        }
        return WebResult.create(ErrorCode.internalSystemError()).withContentType().toJsonView();
    }

    private String buildCaptchaBase64(String contentType, ByteArrayOutputStream outputStream) {
        return String.format("data:%s;base64,%s", contentType, Base64.encodeBase64String(outputStream.toByteArray()));
    }

    /**
     * @param scope 作用域标识，用于区分不同客户端及数据存储范围
     * @param type  输出类型，取值范围：空|data|json，当type=data或type=json时采用Base64编码输出图片
     * @return 返回生成的验证码图片
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping("/")
    @CaptchaStatusCheck(type = ICaptcha.Type.DEFAULT)
    public IView create(@VRequired @VLength(max = 32) @RequestParam String scope, @RequestParam @VDataRange({TYPE_DATA, TYPE_JSON}) String type) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CaptchaTokenBean tokenBean = captcha.generate(scope, outputStream);
        String contentType = String.format("image/%s", captcha.getConfig().getFormat());
        if (StringUtils.equalsIgnoreCase(type, TYPE_DATA)) {
            return View.textView(buildCaptchaBase64(contentType, outputStream));
        } else if (StringUtils.equalsIgnoreCase(type, TYPE_JSON)) {
            return WebResult.succeed()
                    .dataAttr("scope", tokenBean.getScope())
                    .dataAttr("captcha", buildCaptchaBase64(contentType, outputStream)).withContentType().toJsonView();
        }
        return new BinaryView(new ByteArrayInputStream(outputStream.toByteArray()), outputStream.size()).setContentType(contentType);
    }

    /**
     * @param captcha 图片验证码
     * @param scope   作用域标识，用于区分不同客户端及数据存储范围
     * @param mobile  手机号码
     * @return 发送手机短信验证码, ret=0表示发送成功, ret=-1表示参数验证错误, ret=-6表示发送频率过快或其它消息, ret=-50表示发送异常
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/sms_code", method = Type.HttpMethod.POST)
    @CaptchaStatusCheck(type = ICaptcha.Type.SMS)
    public IView sms(@VCaptcha(allowSkip = false, invalid = true, scopeName = ICaptchaConfig.DEFAULT_SCOPE_NAME)
                     @VField(label = "ymp.module.captcha.field.captcha")
                     @RequestParam String captcha,

                     @VRequired @VLength(max = 32) @RequestParam String scope,

                     @VRequired @VMobile @RequestParam String mobile) throws Exception {

        return sendCaptcha(ICaptcha.Type.SMS, scope, mobile);
    }

    /**
     * @param captcha 邮件验证码
     * @param scope   作用域标识，用于区分不同客户端及数据存储范围
     * @param email   邮箱地址
     * @return 发送邮件验证码, ret=0表示发送成功, ret=-1表示参数验证错误, ret=-6表示发送频率过快或其它消息, ret=-50表示发送异常
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/mail_code", method = Type.HttpMethod.POST)
    @CaptchaStatusCheck(type = ICaptcha.Type.MAIL)
    public IView mail(@VCaptcha(allowSkip = false, invalid = true, scopeName = ICaptchaConfig.DEFAULT_SCOPE_NAME)
                      @VField(label = "ymp.module.captcha.field.captcha")
                      @RequestParam String captcha,

                      @VRequired @VLength(max = 32) @RequestParam String scope,

                      @VRequired @VEmail @RequestParam String email) throws Exception {

        return sendCaptcha(ICaptcha.Type.MAIL, scope, email);
    }

    /**
     * @param scope  作用域标识，用于区分不同客户端及数据存储范围
     * @param target 目标(当验证手机号码或邮件地址时使用)
     * @param token  预验证的令牌值
     * @return 返回判断token是否匹配的验证结果（主要用于客户端验证）
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/match", method = {Type.HttpMethod.GET, Type.HttpMethod.POST})
    @CaptchaStatusCheck
    public IView match(@VRequired @VLength(max = 32) @RequestParam String scope,

                       @VLength(max = 50) @RequestParam String target,

                       @VRequired @VLength(max = 10) @RequestParam String token) throws Exception {

        return WebResult.succeed().dataAttr("matched", ICaptcha.Status.MATCHED.equals(captcha.validate(scope, target, token, false))).withContentType().toJsonView();
    }
}
