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
package net.ymate.module.captcha;

import net.ymate.module.captcha.controller.CaptchaController;
import net.ymate.module.captcha.impl.DefaultCaptchaConfig;
import net.ymate.module.captcha.intercept.CaptchaStatusCheck;
import net.ymate.module.captcha.intercept.CaptchaStatusCheckInterceptor;
import net.ymate.module.captcha.validate.CaptchaValidator;
import net.ymate.module.captcha.validate.VCaptcha;
import net.ymate.platform.commons.util.DateTimeUtils;
import net.ymate.platform.commons.util.UUIDUtils;
import net.ymate.platform.core.*;
import net.ymate.platform.core.beans.BeanMeta;
import net.ymate.platform.core.module.IModule;
import net.ymate.platform.core.module.IModuleConfigurer;
import net.ymate.platform.core.module.impl.DefaultModuleConfigurer;
import net.ymate.platform.validation.IValidation;
import net.ymate.platform.webmvc.IWebMvc;
import net.ymate.platform.webmvc.util.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.OutputStream;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 04:54
 */
public final class Captcha implements IModule, ICaptcha {

    private static final Log LOG = LogFactory.getLog(Captcha.class);

    private static volatile ICaptcha instance;

    private IApplication owner;

    private ICaptchaConfig config;

    private boolean initialized;

    public static ICaptcha get() {
        ICaptcha inst = instance;
        if (inst == null) {
            synchronized (Captcha.class) {
                inst = instance;
                if (inst == null) {
                    instance = inst = YMP.get().getModuleManager().getModule(Captcha.class);
                }
            }
        }
        return inst;
    }

    public Captcha() {
    }

    public Captcha(ICaptchaConfig config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void initialize(IApplication owner) throws Exception {
        if (!initialized) {
            //
            YMP.showVersion("Initializing ymate-module-captcha-${version}", new Version(2, 0, 0, Captcha.class, Version.VersionType.Alpha));
            //
            this.owner = owner;
            if (config == null) {
                IApplicationConfigureFactory configureFactory = owner.getConfigureFactory();
                if (configureFactory != null) {
                    IApplicationConfigurer configurer = configureFactory.getConfigurer();
                    IModuleConfigurer moduleConfigurer = configurer == null ? null : configurer.getModuleConfigurer(MODULE_NAME);
                    if (moduleConfigurer != null) {
                        config = DefaultCaptchaConfig.create(configureFactory.getMainClass(), moduleConfigurer);
                    } else {
                        config = DefaultCaptchaConfig.create(configureFactory.getMainClass(), DefaultModuleConfigurer.createEmpty(MODULE_NAME));
                    }
                }
                if (config == null) {
                    config = DefaultCaptchaConfig.defaultConfig();
                }
            }
            if (!config.isInitialized()) {
                config.initialize(this);
                //
                IModule module = owner.getModuleManager().getModule("net.ymate.platform.webmvc.WebMVC");
                if (module != null) {
                    if (config.isServiceEnabled()) {
                        ((IWebMvc) module).registerController(WebUtils.fixUrl(config.getServicePrefix(), false, false), CaptchaController.class);
                    }
                    owner.getInterceptSettings().registerInterceptAnnotation(CaptchaStatusCheck.class, CaptchaStatusCheckInterceptor.class);
                    owner.getBeanFactory().registerBean(BeanMeta.create(CaptchaStatusCheckInterceptor.class, true));
                    //
                    module = owner.getModuleManager().getModule("net.ymate.platform.validation.Validations");
                    if (module != null) {
                        ((IValidation) module).registerValidator(VCaptcha.class, CaptchaValidator.class);
                    }
                }
            }
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void close() throws Exception {
        if (initialized) {
            initialized = false;
            //
            config = null;
            owner = null;
        }
    }

    @Override
    public IApplication getOwner() {
        return owner;
    }

    @Override
    public ICaptchaConfig getConfig() {
        return config;
    }

    @Override
    public CaptchaTokenBean generate(String scope, OutputStream output) throws Exception {
        if (!checkType(Type.DEFAULT)) {
            return null;
        }
        String tokenStr = config.getCaptchaProvider().createCaptcha(output);
        CaptchaTokenBean tokenBean = config.getStorageAdapter().saveOrUpdate(scope, null, tokenStr);
        if (config.isDevelopMode() && LOG.isDebugEnabled()) {
            LOG.debug(String.format("Generate captcha['%s']: %s", StringUtils.trimToEmpty(scope), tokenStr));
        }
        return tokenBean;
    }

    @Override
    public CaptchaTokenBean generate(String scope, String target) throws Exception {
        String tokenStr = generateToken();
        if (StringUtils.isBlank(tokenStr)) {
            tokenStr = UUIDUtils.randomStr(config.getTokenLengthMin(), true);
        }
        return config.getStorageAdapter().saveOrUpdate(scope, target, tokenStr);
    }

    @Override
    public CaptchaTokenBean generate(String scope) throws Exception {
        return generate(scope, (String) null);
    }

    @Override
    public String generateToken() {
        if (config.getTokenGenerator() != null) {
            return config.getTokenGenerator().generate();
        }
        return null;
    }

    @Override
    public void invalidate(String scope) throws Exception {
        config.getStorageAdapter().cleanup(scope);
    }

    @Override
    public Status validate(String scope, String target, String token, boolean invalid) throws Exception {
        Status returnStatus = Status.INVALID;
        if (StringUtils.isNotBlank(token)) {
            CaptchaTokenBean tokenBean = config.getStorageAdapter().load(scope);
            if (tokenBean != null) {
                if (config.getTokenTimeout() > 0 && System.currentTimeMillis() - tokenBean.getCreateTime() > config.getTokenTimeout() * DateTimeUtils.SECOND) {
                    returnStatus = Status.EXPIRED;
                } else if (StringUtils.equalsIgnoreCase(StringUtils.trimToNull(tokenBean.getTarget()), StringUtils.trimToNull(target)) && StringUtils.equalsIgnoreCase(tokenBean.getToken(), token)) {
                    returnStatus = Status.MATCHED;
                }
            }
        }
        if (invalid || Status.MATCHED.equals(returnStatus) || Status.EXPIRED.equals(returnStatus)) {
            invalidate(scope);
        }
        return returnStatus;
    }

    @Override
    public Status validate(String scope, String token, boolean invalid) throws Exception {
        return validate(scope, null, token, invalid);
    }

    @Override
    public CaptchaTokenBean getCaptchaToken(Type type, String scope, String target) throws Exception {
        boolean canSend = isCanSend(type, scope, target);
        CaptchaTokenBean tokenBean = config.getStorageAdapter().load(scope);
        if (tokenBean == null || !StringUtils.equalsIgnoreCase(tokenBean.getTarget(), target)
                || (config.getTokenTimeout() > 0 && System.currentTimeMillis() - tokenBean.getCreateTime() >= config.getTokenTimeout())) {
            tokenBean = generate(scope, target);
            if (tokenBean != null && config.isDevelopMode() && LOG.isDebugEnabled()) {
                LOG.debug(String.format("Generate captcha['%s']: %s%s", scope, tokenBean.getToken(), canSend ? StringUtils.EMPTY : " - Not send."));
            }
        }
        return tokenBean == null || !canSend ? null : tokenBean;
    }

    private boolean isCanSend(Type type, String scope, String target) {
        boolean canSend = true;
        if (config.getScopeProcessor() != null) {
            canSend = config.getScopeProcessor().isAllowSendCode(type, scope, target);
        }
        return canSend;
    }

    @Override
    public boolean captchaSend(Type type, String scope, CaptchaTokenBean tokenBean) throws Exception {
        boolean canSend = isCanSend(type, scope, tokenBean.getTarget());
        if (canSend && !Type.DEFAULT.equals(type) && checkType(type)) {
            if (!config.isDevelopMode()) {
                config.getSendProvider().send(type, scope, tokenBean.getTarget(), tokenBean.getToken());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean checkType(Type type) {
        if (ICaptcha.Type.ALL.equals(type)) {
            throw new IllegalArgumentException("Do not use ICaptcha.Type.ALL.");
        }
        return config.isEnabled() && config.getCaptchaTypes().contains(type);
    }

    @Override
    public boolean isDisabled() {
        return !config.isEnabled();
    }

    @Override
    public boolean isWrongTimesEnabled() {
        return config.isEnabled() && config.getScopeProcessor() != null && config.getNeedCaptchaWrongTimes() > 0;
    }

    @Override
    public boolean isValidationNeedSkip(Type type, String scope) {
        return isWrongTimesEnabled() && config.getScopeProcessor().isNeedSkipValidation(type, scope);
    }

    @Override
    public void resetWrongTimes(Type type, String scope) {
        if (isWrongTimesEnabled()) {
            config.getScopeProcessor().resetWrongTimes(type, scope);
        }
    }
}
