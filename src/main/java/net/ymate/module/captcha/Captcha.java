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

import net.ymate.module.captcha.impl.DefaultModuleCfg;
import net.ymate.platform.core.Version;
import net.ymate.platform.core.YMP;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.annotation.Module;
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

    public static final Version VERSION = new Version(1, 0, 0, Captcha.class.getPackage().getImplementationVersion(), Version.VersionType.Alphal);

    private static volatile ICaptcha __instance;

    private YMP __owner;

    private ICaptchaModuleCfg __moduleCfg;

    private boolean __inited;

    public static ICaptcha get() {
        if (__instance == null) {
            synchronized (VERSION) {
                if (__instance == null) {
                    __instance = YMP.get().getModule(Captcha.class);
                }
            }
        }
        return __instance;
    }

    public String getName() {
        return MODULE_NAME;
    }

    public void init(YMP owner) throws Exception {
        if (!__inited) {
            //
            __owner = owner;
            __moduleCfg = new DefaultModuleCfg(owner);
            //
            _LOG.info("Initializing ymate-module-captcha-" + VERSION + " - debug:" + __moduleCfg.isDevelopMode());
            //
            __moduleCfg.getCaptchaProvider().init(this);
            __moduleCfg.getCaptchaStorageAdapter().init(this);
            if (__moduleCfg.getCaptchaTokenProcessor() != null) {
                __moduleCfg.getCaptchaTokenProcessor().init(this);
            }
            if (__moduleCfg.getCaptchaSmsSendProvider() != null) {
                __moduleCfg.getCaptchaSmsSendProvider().init(this);
            }
            //
            __inited = true;
        }
    }

    public boolean isInited() {
        return __inited;
    }

    public void destroy() throws Exception {
        if (__inited) {
            __inited = false;
            //
            __moduleCfg = null;
            __owner = null;
        }
    }

    public ICaptchaModuleCfg getModuleCfg() {
        return __moduleCfg;
    }

    public YMP getOwner() {
        return __owner;
    }

    public String generate(String tokenId, OutputStream output) throws Exception {
        String _token = __moduleCfg.getCaptchaProvider().createCaptcha(output);
        __moduleCfg.getCaptchaStorageAdapter().saveOrUpdate(tokenId, _token);
        return _token;
    }

    public String generate(String tokenId) throws Exception {
        String _token = UUIDUtils.randomStr(__moduleCfg.getTokenLengthMin(), true);
        __moduleCfg.getCaptchaStorageAdapter().saveOrUpdate(tokenId, _token);
        return _token;
    }

    public void invalidate(String tokenId) throws Exception {
        __moduleCfg.getCaptchaStorageAdapter().cleanup(tokenId);
    }

    public Status validate(String tokenId, String token, boolean invalid) throws Exception {
        Status _returnStatus = Status.INVALID;
        if (token != null) {
            CaptchaTokenBean _tokenBean = __moduleCfg.getCaptchaStorageAdapter().load(tokenId);
            if (_tokenBean != null) {
                if (__moduleCfg.getTokenTimeout() != null && System.currentTimeMillis() - _tokenBean.getCreateTime() > __moduleCfg.getTokenTimeout() * 1000) {
                    _returnStatus = Status.EXPIRED;
                } else if (StringUtils.equalsIgnoreCase(_tokenBean.getToken(), token)) {
                    _returnStatus = Status.MATCHED;
                }
            }
        }
        if (invalid) {
            invalidate(tokenId);
        }
        return _returnStatus;
    }

    public boolean isWrongTimesEnabled() {
        return __moduleCfg.getCaptchaTokenProcessor() != null && __moduleCfg.getNeedCaptchaWrongTimes() > 0;
    }

    public boolean isValidationNeedSkip(String tokenId) {
        return isWrongTimesEnabled() && __moduleCfg.getCaptchaTokenProcessor().isNeedSkipValidation(tokenId);
    }

    public void resetWrongTimes(String tokenId) {
        if (isWrongTimesEnabled()) {
            __moduleCfg.getCaptchaTokenProcessor().resetWrongTimes(tokenId);
        }
    }
}
