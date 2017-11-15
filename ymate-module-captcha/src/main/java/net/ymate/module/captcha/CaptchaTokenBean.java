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

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午8:27
 * @version 1.0
 */
public class CaptchaTokenBean implements Serializable {

    /**
     * 目标(手机号码或邮件地址)
     */
    private String target;

    /**
     * 令牌
     */
    private String token;

    /**
     * 验证码状态
     */
    private ICaptcha.Status status;

    /**
     * 验证码生成时间
     */
    private long createTime;

    public CaptchaTokenBean(String token) {
        if (StringUtils.isBlank(token)) {
            throw new NullArgumentException("token");
        }
        this.token = token;
        this.status = ICaptcha.Status.NORMAL;
        this.createTime = System.currentTimeMillis();
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
}
