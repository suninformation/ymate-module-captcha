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

import net.ymate.module.captcha.CaptchaTokenBean;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaStorageAdapter;
import net.ymate.platform.cache.Caches;
import net.ymate.platform.cache.ICache;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 2018-12-11 02:05
 */
public class DefaultCaptchaStorageAdapter implements ICaptchaStorageAdapter {

    private ICache dataCache;

    private boolean initialized;

    @Override
    public void initialize(ICaptcha owner) throws Exception {
        if (!initialized) {
            String cacheName = String.format("%s%s_data", StringUtils.trimToEmpty(owner.getConfig().getCacheNamePrefix()), ICaptcha.MODULE_NAME);
            dataCache = owner.getOwner().getModuleManager().getModule(Caches.class).getConfig().getCacheProvider().getCache(cacheName);
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public CaptchaTokenBean load(String scope) throws Exception {
        if (StringUtils.isNotBlank(scope)) {
            return (CaptchaTokenBean) dataCache.get(scope);
        }
        return null;
    }

    @Override
    public CaptchaTokenBean saveOrUpdate(String scope, String target, String token) throws Exception {
        if (StringUtils.isBlank(scope)) {
            throw new NullArgumentException("scope");
        }
        if (StringUtils.isBlank(token)) {
            throw new NullArgumentException("token");
        }
        CaptchaTokenBean tokenBean = new CaptchaTokenBean(token, scope).setTarget(target);
        dataCache.put(scope, tokenBean);
        //
        return tokenBean;
    }

    @Override
    public void cleanup(String scope) throws Exception {
        if (StringUtils.isNotBlank(scope)) {
            dataCache.remove(scope);
        }
    }
}
