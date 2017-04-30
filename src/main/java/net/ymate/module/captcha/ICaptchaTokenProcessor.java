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

import net.ymate.platform.core.lang.PairObject;

/**
 * 身份令牌标识扩展处理器
 *
 * @author 刘镇 (suninformation@163.com) on 17/4/13 上午10:00
 * @version 1.0
 */
public interface ICaptchaTokenProcessor {

    /**
     * 初始化
     *
     * @param owner 所属模块管理器对象
     * @throws Exception 可能产生的任何异常
     */
    void init(ICaptcha owner) throws Exception;

    /**
     * @param tokenId 令牌标识ID
     * @return 判断指定令牌标识的验证码是否跳过验证
     */
    boolean isNeedSkipValidation(String tokenId);

    /**
     * 重置错误计数器
     *
     * @param tokenId 令牌标识ID
     */
    void resetWrongTimes(String tokenId);

    /**
     * @param tokenId 令牌标识ID
     * @param mobile  手机号码
     * @return 判断指定令牌标识和手机号码是否允许发送短信码，返回值: &lt;状态码, 提示信息&gt;
     */
    PairObject<Integer, String> isAllowSmsCodeSend(String tokenId, String mobile);
}
