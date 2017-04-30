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

import net.ymate.module.captcha.Captcha;
import net.ymate.module.captcha.CaptchaTokenBean;
import net.ymate.module.captcha.ICaptcha;
import net.ymate.module.captcha.ICaptchaTokenProcessor;
import net.ymate.platform.cache.Caches;
import net.ymate.platform.cache.ICache;
import net.ymate.platform.core.lang.BlurObject;
import net.ymate.platform.core.lang.PairObject;
import net.ymate.platform.webmvc.WebMVC;
import net.ymate.platform.webmvc.context.WebContext;
import net.ymate.platform.webmvc.util.CookieHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 默认身份令牌标识扩展处理器
 *
 * @author 刘镇 (suninformation@163.com) on 17/4/22 下午12:07
 * @version 1.0
 */
public class DefaultCaptchaTokenProcessor implements ICaptchaTokenProcessor {

    private ICache __tokenWrongTimesCache;

    public DefaultCaptchaTokenProcessor() {
    }

    public void init(ICaptcha owner) throws Exception {
        __tokenWrongTimesCache = Caches.get().getCacheProvider().getCache(owner.getModuleCfg().getCacheNamePrefix().concat("captcha_wrong_times"));
    }

    protected String __buildCacheKey(String tokenId) {
        return DigestUtils.md5Hex(StringUtils.trimToEmpty(WebContext.getRequest().getHeader("User-Agent")) + StringUtils.defaultIfBlank(tokenId, CaptchaTokenBean.class.getName()));
    }

    public boolean isNeedSkipValidation(String tokenId) {
        if (StringUtils.startsWithIgnoreCase(tokenId, ICaptcha.Const.TOKEN_SMS)) {
            return false;
        }
        String _cacheKey = __buildCacheKey(tokenId);
        int _count = BlurObject.bind(__tokenWrongTimesCache.get(_cacheKey)).toIntValue();
        __tokenWrongTimesCache.put(_cacheKey, _count + 1);
        //
        boolean _result = _count < Captcha.get().getModuleCfg().getNeedCaptchaWrongTimes();
        if (!_result) {
            // 若需要验证，则写入Cookie便于页面判断是不是需要展示验证码组件
            CookieHelper.bind(WebMVC.get()).setCookie("captcha_" + StringUtils.trimToEmpty(tokenId), _count + "");
        }
        return _result;
    }

    public void resetWrongTimes(String tokenId) {
        if (!StringUtils.startsWithIgnoreCase(tokenId, ICaptcha.Const.TOKEN_SMS)) {
            __tokenWrongTimesCache.remove(__buildCacheKey(tokenId));
            // 同时移除写入Cookie的内容
            CookieHelper.bind(WebMVC.get()).removeCookie("captcha_" + StringUtils.trimToEmpty(tokenId));
        }
    }

    public PairObject<Integer, String> isAllowSmsCodeSend(String tokenId, String mobile) {
        return null;
    }
}
