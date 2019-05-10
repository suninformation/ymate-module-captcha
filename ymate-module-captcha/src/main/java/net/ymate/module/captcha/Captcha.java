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

import net.ymate.module.captcha.impl.DefaultCaptchaModuleCfg;
import net.ymate.platform.core.Version;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.annotation.Module;
import net.ymate.platform.core.util.DateTimeUtils;
import net.ymate.platform.core.util.UUIDUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.OutputStream;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午4:54
 * @version 1.0
 */
@Module
public class Captcha implements IModule, ICaptcha {

    private static final Log _LOG = LogFactory.getLog(Captcha.class);

    public static final Version VERSION = new Version(1, 0, 1, Captcha.class.getPackage().getImplementationVersion(), Version.VersionType.Release);

    private static volatile ICaptcha __instance;

    private YMP __owner;

    private ICaptchaModuleCfg __moduleCfg;

    private boolean __inited;

    public static ICaptcha get() {
        ICaptcha _inst = __instance;
        if (_inst == null) {
            synchronized (VERSION) {
                _inst = __instance;
                if (_inst == null) {
                    __instance = _inst = YMP.get().getModule(Captcha.class);
                }
            }
        }
        return _inst;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void init(YMP owner) throws Exception {
        if (!__inited) {
            //
            __owner = owner;
            __moduleCfg = new DefaultCaptchaModuleCfg(owner);
            //
            _LOG.info("Initializing ymate-module-captcha-" + VERSION + " - debug:" + __moduleCfg.isDevelopMode());
            //
            if (!__moduleCfg.isDisabled()) {
                //
                __moduleCfg.getCaptchaProvider().init(this);
                __moduleCfg.getStorageAdapter().init(this);
                //
                if (__moduleCfg.getTokenGenerator() != null) {
                    __moduleCfg.getTokenGenerator().init(this);
                }
                if (__moduleCfg.getScopeProcessor() != null) {
                    __moduleCfg.getScopeProcessor().init(this);
                }
                if (__moduleCfg.getSendProvider() != null) {
                    __moduleCfg.getSendProvider().init(this);
                }
            }
            //
            __inited = true;
        }
    }

    @Override
    public boolean isInited() {
        return __inited;
    }

    @Override
    public void destroy() throws Exception {
        if (__inited) {
            __inited = false;
            //
            __moduleCfg = null;
            __owner = null;
        }
    }

    @Override
    public ICaptchaModuleCfg getModuleCfg() {
        return __moduleCfg;
    }

    @Override
    public YMP getOwner() {
        return __owner;
    }

    @Override
    public CaptchaTokenBean generate(String scope, OutputStream output) throws Exception {
        if (!__moduleCfg.isDisabled()) {
            String _token = __moduleCfg.getCaptchaProvider().createCaptcha(output);
            CaptchaTokenBean _bean = __moduleCfg.getStorageAdapter().saveOrUpdate(scope, null, _token);
            //
            if (__moduleCfg.isDevelopMode() && _LOG.isDebugEnabled()) {
                _LOG.debug("Generate captcha['" + StringUtils.trimToEmpty(scope) + "']: " + _token);
            }
            return _bean;
        } else if (_LOG.isWarnEnabled()) {
            _LOG.warn("Captcha module has been disabled.");
        }
        return null;
    }

    @Override
    public CaptchaTokenBean generate(String scope, String target) throws Exception {
        if (!__moduleCfg.isDisabled()) {
            String _token = generateToken();
            if (StringUtils.isBlank(_token)) {
                _token = UUIDUtils.randomStr(__moduleCfg.getTokenLengthMin(), true);
            }
            return __moduleCfg.getStorageAdapter().saveOrUpdate(scope, target, _token);
        } else if (_LOG.isWarnEnabled()) {
            _LOG.warn("Captcha module has been disabled.");
        }
        return null;
    }

    @Override
    public CaptchaTokenBean generate(String scope) throws Exception {
        return generate(scope, (String) null);
    }

    @Override
    public String generateToken() {
        if (__moduleCfg.getTokenGenerator() != null) {
            return __moduleCfg.getTokenGenerator().generate();
        }
        return null;
    }

    @Override
    public void invalidate(String scope) throws Exception {
        if (!__moduleCfg.isDisabled()) {
            __moduleCfg.getStorageAdapter().cleanup(scope);
        } else if (_LOG.isWarnEnabled()) {
            _LOG.warn("Captcha module has been disabled.");
        }
    }

    @Override
    public Status validate(String scope, String target, String token, boolean invalid) throws Exception {
        Status _returnStatus = Status.INVALID;
        if (!__moduleCfg.isDisabled()) {
            if (token != null) {
                CaptchaTokenBean _tokenBean = __moduleCfg.getStorageAdapter().load(scope);
                if (_tokenBean != null) {
                    if (__moduleCfg.getTokenTimeout() != null && System.currentTimeMillis() - _tokenBean.getCreateTime() > __moduleCfg.getTokenTimeout() * DateTimeUtils.SECOND) {
                        _returnStatus = Status.EXPIRED;
                    } else if (StringUtils.equalsIgnoreCase(_tokenBean.getTarget(), target) && StringUtils.equalsIgnoreCase(_tokenBean.getToken(), token)) {
                        _returnStatus = Status.MATCHED;
                    }
                }
            }
            if (invalid || Status.MATCHED.equals(_returnStatus) || Status.EXPIRED.equals(_returnStatus)) {
                invalidate(scope);
            }
        } else if (_LOG.isWarnEnabled()) {
            _LOG.warn("Captcha module has been disabled.");
        }
        return _returnStatus;
    }

    @Override
    public Status validate(String scope, String token, boolean invalid) throws Exception {
        return validate(scope, null, token, invalid);
    }

    @Override
    public CaptchaTokenBean getCaptchaToken(ICaptcha.Type type, String scope, String target) throws Exception {
        boolean _canSend = true;
        //
        ICaptchaScopeProcessor _processor = __moduleCfg.getScopeProcessor();
        if (_processor != null) {
            _canSend = _processor.isAllowSendCode(type, scope, target);
        }
        CaptchaTokenBean _tokenBean = __moduleCfg.getStorageAdapter().load(scope);
        if (_tokenBean == null || !StringUtils.equalsIgnoreCase(_tokenBean.getTarget(), target)
                || (__moduleCfg.getTokenTimeout() != null && System.currentTimeMillis() - _tokenBean.getCreateTime() >= __moduleCfg.getTokenTimeout())) {
            _tokenBean = generate(scope, target);
            //
            if (_tokenBean != null) {
                if (__moduleCfg.isDevelopMode()) {
                    _LOG.debug("Generate captcha['" + scope + "']: " + _tokenBean.getToken() + (_canSend ? "" : " - Not send."));
                }
            }
        }
        return _tokenBean == null || !_canSend ? null : _tokenBean;
    }

    @Override
    public boolean captchaSend(ICaptcha.Type type, String scope, CaptchaTokenBean tokenBean) throws Exception {
        ICaptchaSendProvider _sender = __moduleCfg.getSendProvider();
        if (_sender != null) {
            if (!__moduleCfg.isDevelopMode()) {
                _sender.send(type, scope, tokenBean.getTarget(), tokenBean.getToken());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isDisabled() {
        return __moduleCfg.isDisabled();
    }

    @Override
    public boolean isWrongTimesEnabled() {
        return !__moduleCfg.isDisabled() && __moduleCfg.getScopeProcessor() != null && __moduleCfg.getNeedCaptchaWrongTimes() > 0;
    }

    @Override
    public boolean isValidationNeedSkip(ICaptcha.Type type, String scope) {
        return !__moduleCfg.isDisabled() && isWrongTimesEnabled() && __moduleCfg.getScopeProcessor().isNeedSkipValidation(type, scope);
    }

    @Override
    public void resetWrongTimes(ICaptcha.Type type, String scope) {
        if (isWrongTimesEnabled()) {
            __moduleCfg.getScopeProcessor().resetWrongTimes(type, scope);
        }
    }
}
