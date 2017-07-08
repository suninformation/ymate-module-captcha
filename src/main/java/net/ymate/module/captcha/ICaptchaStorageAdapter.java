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

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午5:21
 * @version 1.0
 */
public interface ICaptchaStorageAdapter {

    void init(ICaptcha owner) throws Exception;

    CaptchaTokenBean load(String tokenId) throws Exception;

    boolean saveOrUpdate(String tokenId, String target, String token) throws Exception;

    void cleanup(String tokenId) throws Exception;
}
