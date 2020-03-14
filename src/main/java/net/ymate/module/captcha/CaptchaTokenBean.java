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

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 08:27
 */
public class CaptchaTokenBean implements Serializable {

    /**
     * 目标(手机号码或邮件地址)
     */
    private String target;

    /**
     * 令牌
     */
    private final String token;

    /**
     * 作用域
     */
    private String scope;

    /**
     * 验证码状态
     */
    private final ICaptcha.Status status = ICaptcha.Status.NORMAL;

    /**
     * 验证码生成时间
     */
    private final long createTime = System.currentTimeMillis();

    public CaptchaTokenBean(String token) {
        if (StringUtils.isBlank(token)) {
            throw new NullArgumentException("token");
        }
        this.token = token;
    }

    public CaptchaTokenBean(String token, String scope) {
        this(token);
        this.scope = scope;
    }

    public String getTarget() {
        return target;
    }

    public CaptchaTokenBean setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getToken() {
        return token;
    }

    public ICaptcha.Status getStatus() {
        return status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return String.format("CaptchaTokenBean{target='%s', token='%s', scope='%s', status=%s, createTime=%d}", target, token, scope, status, createTime);
    }
}
