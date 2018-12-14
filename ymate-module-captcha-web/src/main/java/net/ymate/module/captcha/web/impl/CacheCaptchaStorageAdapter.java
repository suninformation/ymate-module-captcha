/*
 * Copyright 2007-2018 the original author or authors.
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
package net.ymate.module.captcha.web.impl;

import net.ymate.module.captcha.CaptchaTokenBean;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaStorageAdapter;
import net.ymate.platform.cache.Caches;
import net.ymate.platform.cache.ICache;
import org.apache.commons.lang.StringUtils;

/**
 * @author 刘镇 (suninformation@163.com) on 2018-12-11 02:05
 * @version 1.0
 */
public class CacheCaptchaStorageAdapter implements ICaptchaStorageAdapter {

    private ICache __dataCache;

    @Override
    public void init(ICaptcha owner) throws Exception {
        __dataCache = Caches.get().getCacheProvider().getCache(owner.getModuleCfg().getCacheNamePrefix().concat("captcha_data"));
    }

    @Override
    public CaptchaTokenBean load(String scope) throws Exception {
        if (StringUtils.isNotBlank(scope)) {
            return (CaptchaTokenBean) __dataCache.get(scope);
        }
        return null;
    }

    @Override
    public CaptchaTokenBean saveOrUpdate(String scope, String target, String token) throws Exception {
        CaptchaTokenBean _tokenBean = new CaptchaTokenBean(token, scope).setTarget(target);
        __dataCache.put(StringUtils.defaultIfBlank(scope, CaptchaTokenBean.class.getName()), _tokenBean);
        //
        return _tokenBean;
    }

    @Override
    public void cleanup(String scope) throws Exception {
        if (StringUtils.isNotBlank(scope)) {
            __dataCache.remove(scope);
        }
    }
}
