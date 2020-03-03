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
package net.ymate.module.captcha.impl;

import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaConfig;
import net.ymate.module.captcha.ICaptchaSendProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 验证码发送能力接口默认实现(主要用于测试模拟, 仅日志输出不会发送任何短信或邮件)
 *
 * @author 刘镇 (suninformation@163.com) on 2018/9/12 21:53
 */
public class DefaultCaptchaSendProvider implements ICaptchaSendProvider {

    private static final Log LOG = LogFactory.getLog(DefaultCaptchaSendProvider.class);

    private boolean initialized;

    @Override
    public void initialize(ICaptcha owner) throws Exception {
        if (!initialized) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Initializing DefaultCaptchaSendProvider...");
            }
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void send(ICaptcha.Type type, String scope, String target, String captcha) throws Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("[%s]: Send captcha %s to %s for %s scope.", type.name(), captcha, target, StringUtils.defaultIfBlank(scope, ICaptchaConfig.DEFAULT_SCOPE)));
        }
    }
}
