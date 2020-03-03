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

import net.ymate.platform.cache.Caches;
import net.ymate.platform.cache.ICache;
import net.ymate.platform.commons.lang.BlurObject;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

/**
 * 默认身份令牌标识扩展处理器
 *
 * @author 刘镇 (suninformation@163.com) on 2017/4/22 12:07
 */
public abstract class AbstractCaptchaScopeProcessor implements ICaptchaScopeProcessor {

    private ICaptcha owner;

    private String cacheName;

    private ICache tokenWrongTimesCache;

    private boolean initialized;

    @Override
    public void initialize(ICaptcha owner) throws Exception {
        if (!initialized) {
            this.owner = owner;
            cacheName = String.format("%s%s_wrong_times", StringUtils.trimToEmpty(owner.getConfig().getCacheNamePrefix()), ICaptcha.MODULE_NAME);
            tokenWrongTimesCache = owner.getOwner().getModuleManager().getModule(Caches.class).getConfig().getCacheProvider().getCache(cacheName);
            initialized = true;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    public ICaptcha getOwner() {
        return owner;
    }

    /**
     * 基于scope参数构建缓存键名
     *
     * @param scope 作用域
     * @return 返回缓存键名
     */
    protected abstract String buildCacheKey(String scope);

    /**
     * 若需要验证则可以通过实现此方法写入Cookie便于页面判断是不是需要展示验证码组件
     *
     * @param cacheName    缓存名称
     * @param currentValue 当前错误次数
     * @param removed      是否为移除操作(若是则currentValue值可能为0)
     */
    protected abstract void processWrongTimes(String cacheName, int currentValue, boolean removed);

    @Override
    public boolean isNeedSkipValidation(ICaptcha.Type type, String scope) {
        if (StringUtils.isBlank(scope)) {
            throw new NullArgumentException("scope");
        }
        switch (type) {
            case SMS:
            case MAIL:
                return false;
            default:
                String cacheKey = buildCacheKey(scope);
                int currentCount = BlurObject.bind(tokenWrongTimesCache.get(cacheKey)).toIntValue();
                tokenWrongTimesCache.put(cacheKey, currentCount++);
                //
                boolean result = currentCount < owner.getConfig().getNeedCaptchaWrongTimes();
                if (!result) {
                    processWrongTimes(cacheName + (StringUtils.isNotBlank(scope) ? "_" + scope : StringUtils.EMPTY), currentCount, false);
                }
                return result;
        }
    }

    @Override
    public void resetWrongTimes(ICaptcha.Type type, String scope) {
        if (ICaptcha.Type.DEFAULT.equals(type)) {
            if (StringUtils.isBlank(scope)) {
                throw new NullArgumentException("scope");
            }
            tokenWrongTimesCache.remove(buildCacheKey(scope));
            processWrongTimes(cacheName + (StringUtils.isNotBlank(scope) ? "_" + scope : StringUtils.EMPTY), 0, true);
        }
    }

    @Override
    public boolean isAllowSendCode(ICaptcha.Type type, String scope, String target) {
        return true;
    }
}
