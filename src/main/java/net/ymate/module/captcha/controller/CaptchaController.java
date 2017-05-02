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
package net.ymate.module.captcha.controller;

import net.ymate.framework.validation.VMobile;
import net.ymate.framework.webmvc.ErrorCode;
import net.ymate.framework.webmvc.WebResult;
import net.ymate.module.captcha.*;
import net.ymate.platform.core.lang.PairObject;
import net.ymate.platform.core.util.RuntimeUtils;
import net.ymate.platform.validation.validate.VEmail;
import net.ymate.platform.validation.validate.VLength;
import net.ymate.platform.validation.validate.VNumeric;
import net.ymate.platform.validation.validate.VRequried;
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
public class CaptchaController {

    private static final Log _LOG = LogFactory.getLog(CaptchaController.class);

    /**
     * @param tokenId 身份令牌标识ID, 用于区分不同客户端及数据存储范围
     * @param type    仅当type=1时采用Base64编码输出图片
     * @return 返回生成的验证码图片
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping("/")
    public IView create(@VLength(max = 32)
                        @RequestParam String tokenId,

                        @VNumeric
                        @RequestParam Integer type) throws Exception {

        ICaptcha _captcha = Captcha.get();
        //
        ByteArrayOutputStream _output = new ByteArrayOutputStream();
        String _token = _captcha.generate(tokenId, _output);
        //
        if (_captcha.getModuleCfg().isDevelopMode()) {
            _LOG.debug("Generate captcha['" + StringUtils.trimToEmpty(tokenId) + "']: " + _token);
        }
        //
        String _contentType = "image/" + _captcha.getModuleCfg().getFormat();
        //
        if (type != null && type == 1) {
            return View.textView("data:" + _contentType + ";base64," + Base64.encodeBase64String(_output.toByteArray()));
        }
        return new BinaryView(new ByteArrayInputStream(_output.toByteArray()), _output.size()).setContentType(_contentType);
    }

    /**
     * @param tokenId 身份令牌标识ID, 用于区分不同客户端及数据存储范围
     * @param token   预验证的令牌值
     * @return 返回判断token是否匹配的验证结果（主要用于客户端验证）
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/match", method = {Type.HttpMethod.GET, Type.HttpMethod.POST})
    public IView match(@VLength(max = 32)
                       @RequestParam String tokenId,

                       @VLength(max = 10)
                       @RequestParam String token) throws Exception {

        return WebResult.CODE(0).dataAttr("matched", ICaptcha.Status.MATCHED.equals(Captcha.get().validate(tokenId, token, false))).toJSON();
    }

    private CaptchaTokenBean __doGetCaptchaToken(ICaptchaModuleCfg captchaCfg, String tokenId, boolean isNeedSend, boolean isSms) throws Exception {
        CaptchaTokenBean _tokenBean = captchaCfg.getCaptchaStorageAdapter().load(tokenId);
        if (_tokenBean == null || (captchaCfg.getTokenTimeout() != null && System.currentTimeMillis() - _tokenBean.getCreateTime() >= captchaCfg.getTokenTimeout())) {
            Captcha.get().generate(tokenId);
            _tokenBean = captchaCfg.getCaptchaStorageAdapter().load(tokenId);
            //
            if (_tokenBean != null) {
                isNeedSend = true;
                if (captchaCfg.isDevelopMode()) {
                    _LOG.debug("Generate captcha['" + tokenId + "']: " + _tokenBean.getToken());
                }
            }
        }
        int interval = isSms ? captchaCfg.getCaptchaSmsSendTimeInterval() : captchaCfg.getCaptchaMailSendTimeInterval();
        if (_tokenBean != null && (isNeedSend || System.currentTimeMillis() - _tokenBean.getCreateTime() > interval * 1000)) {
            return _tokenBean;
        }
        return null;
    }

    /**
     * @param tokenId 身份令牌标识ID, 用于区分不同客户端及数据存储范围, 默认值: sms
     * @param mobile  手机号码
     * @return 发送手机短信验证码, ret=0表示发送成功, ret=-1表示参数验证错误, ret=-6表示发送频率过快或其它消息, ret=-50表示发送异常
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/sms_code", method = Type.HttpMethod.POST)
    public IView smsCode(@VLength(max = 32)
                         @RequestParam(defaultValue = ICaptcha.Const.TOKEN_SMS) String tokenId,

                         @VRequried
                         @VMobile
                         @RequestParam String mobile) throws Exception {
        ICaptchaModuleCfg _captchaCfg = Captcha.get().getModuleCfg();
        ICaptchaSmsSendProvider _sender = _captchaCfg.getCaptchaSmsSendProvider();
        if (_sender != null) {
            boolean _needSend = false;
            ICaptchaTokenProcessor _processor = _captchaCfg.getCaptchaTokenProcessor();
            if (_processor != null) {
                PairObject<Integer, String> _allowSmsSend = _processor.isAllowSmsCodeSend(tokenId, mobile);
                if (_allowSmsSend != null) {
                    _needSend = _allowSmsSend.getKey() == 0;
                    if (!_needSend) {
                        return WebResult.CODE(ErrorCode.REQUEST_OPERATION_FORBIDDEN).msg(_allowSmsSend.getValue()).toJSON();
                    }
                }
            }
            CaptchaTokenBean _tokenBean = __doGetCaptchaToken(_captchaCfg, tokenId, _needSend, true);
            if (_tokenBean != null) {
                try {
                    if (!_captchaCfg.isDevelopMode()) {
                        _sender.send(mobile, _tokenBean.getToken());
                    }
                    //
                    return WebResult.SUCCESS().toJSON();
                } catch (Exception e) {
                    _LOG.warn("An exception occurred at send sms to " + mobile, RuntimeUtils.unwrapThrow(e));
                }
            } else {
                return WebResult.CODE(ErrorCode.REQUEST_OPERATION_FORBIDDEN).toJSON();
            }
        }
        return WebResult.CODE(ErrorCode.INTERNAL_SYSTEM_ERROR).toJSON();
    }

    /**
     * @param tokenId 身份令牌标识ID, 用于区分不同客户端及数据存储范围, 默认值: sms
     * @param email   邮箱地址
     * @return 发送邮件验证码, ret=0表示发送成功, ret=-1表示参数验证错误, ret=-6表示发送频率过快或其它消息, ret=-50表示发送异常
     * @throws Exception 可能产生的任何异常
     */
    @RequestMapping(value = "/mail_code", method = Type.HttpMethod.POST)
    public IView mailCode(@VLength(max = 32)
                          @RequestParam(defaultValue = ICaptcha.Const.TOKEN_MAIL) String tokenId,

                          @VRequried
                          @VEmail
                          @RequestParam String email) throws Exception {
        ICaptchaModuleCfg _captchaCfg = Captcha.get().getModuleCfg();
        ICaptchaMailSendProvider _sender = _captchaCfg.getCaptchaMailSendProvider();
        if (_sender != null) {
            CaptchaTokenBean _tokenBean = __doGetCaptchaToken(_captchaCfg, tokenId, false, false);
            if (_tokenBean != null) {
                try {
                    if (!_captchaCfg.isDevelopMode()) {
                        _sender.send(email, _tokenBean.getToken());
                    }
                    //
                    return WebResult.SUCCESS().toJSON();
                } catch (Exception e) {
                    _LOG.warn("An exception occurred at send mail to " + email, RuntimeUtils.unwrapThrow(e));
                }
            } else {
                return WebResult.CODE(ErrorCode.REQUEST_OPERATION_FORBIDDEN).toJSON();
            }
        }
        return WebResult.CODE(ErrorCode.INTERNAL_SYSTEM_ERROR).toJSON();
    }
}
