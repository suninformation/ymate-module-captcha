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
package net.ymate.module.captcha.impl;

import net.ymate.module.captcha.CaptchaTokenBean;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaStorageAdapter;
import net.ymate.platform.webmvc.context.WebContext;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午7:10
 * @version 1.0
 */
public class DefaultCaptchaStorageAdapter implements ICaptchaStorageAdapter {

    public void init(ICaptcha owner) throws Exception {
    }

    public CaptchaTokenBean load(String tokenId) throws Exception {
        HttpSession _session = WebContext.getRequest().getSession(false);
        if (_session != null) {
            return (CaptchaTokenBean) _session.getAttribute(StringUtils.defaultIfBlank(tokenId, CaptchaTokenBean.class.getName()));
        }
        return null;
    }

    public boolean saveOrUpdate(String tokenId, String token) throws Exception {
        HttpSession _session = WebContext.getRequest().getSession(false);
        if (_session != null) {
            _session.setAttribute(StringUtils.defaultIfBlank(tokenId, CaptchaTokenBean.class.getName()), new CaptchaTokenBean(token));
            return true;
        }
        return false;
    }

    public void cleanup(String tokenId) throws Exception {
        HttpSession _session = WebContext.getRequest().getSession(false);
        if (_session != null) {
            _session.removeAttribute(StringUtils.defaultIfBlank(tokenId, CaptchaTokenBean.class.getName()));
        }
    }
}
