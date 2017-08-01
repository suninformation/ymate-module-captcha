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

import com.github.cage.image.Painter;

import java.awt.*;

/**
 * @author 刘镇 (suninformation@163.com) on 16/11/27 上午4:58
 * @version 1.0
 */
public interface ICaptchaModuleCfg {

    /**
     * @return 验证码模块是否已被禁用(禁用后将忽略所有验证码相关参数验证), 默认值: false
     */
    boolean isDisabled();

    /**
     * @return 是否开启调试模式(调试模式下控制台将输出生成的验证码, 同时短信验证码也不会被真正发送), 默认值: false
     */
    boolean isDevelopMode();

    /**
     * @return 验证码服务提供者类, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaProvider
     */
    ICaptchaProvider getCaptchaProvider();

    /**
     * @return 验证码存储适配器类, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaStorageAdapter
     */
    ICaptchaStorageAdapter getCaptchaStorageAdapter();

    /**
     * @return 手机短信验证码发送服务提供者类, 默认值: 空
     */
    ICaptchaSendProvider getCaptchaSmsSendProvider();

    /**
     * @return 邮件验证码发送服务提供者类, 默认值: 空
     */
    ICaptchaSendProvider getCaptchaMailSendProvider();

    /**
     * @return 身份令牌标识扩展处理器, 默认值: 空
     */
    ICaptchaTokenProcessor getCaptchaTokenProcessor();

    /**
     * @return 手机短信验证码内容模板, 默认值: ${captcha}
     */
    String getCaptchaSmsContentTemplate();

    /**
     * @return 相同令牌标识范围的短信验证码重复发送的是时间间隔(秒), 默认为120秒
     */
    int getCaptchaSmsSendTimeInterval();

    /**
     * @return 相同令牌标识范围的邮件验证码重复发送的是时间间隔(秒), 默认为300秒
     */
    int getCaptchaMailSendTimeInterval();

    /**
     * @return 设置在达到指定错误次数上限后开启验证码, 默认为0则表示不开启错误记数特性
     */
    int getNeedCaptchaWrongTimes();

    /**
     * @return 缓存名称前缀, 默认值: ""
     */
    String getCacheNamePrefix();

    // ----------

    /**
     * @return 验证码最小字符长度, 默认值: 4
     */
    int getTokenLengthMin();

    /**
     * @return 验证码超时时间, 单位: 秒, 默认: 空, 空或小于等于0均表示不限制
     */
    Integer getTokenTimeout();

    /**
     * @return 高度, 默认: 70px
     */
    Integer getHeight();

    /**
     * @return 宽度, 默认: 200px
     */
    Integer getWidth();

    /**
     * @return 前景色, RGB值, 如: 0,0,0|1,2,3, 多个颜色用'|'分隔, 默认: 随机
     */
    java.util.List<Color> getForegrounds();

    /**
     * @return 背景色, RBG值, 默认: 255,255,255
     */
    Color getBackground();

    /**
     * @return 质量, 可选值: min|default|max, 默认: max
     */
    Painter.Quality getQuality();

    /**
     * @return 压缩比, 0-1之间, 默认: 空
     */
    Float getCompressRatio();

    /**
     * @return 图片格式, 可选值: png|jpeg, 默认: jpeg
     */
    String getFormat();

    /**
     * @return 自定义字体, 可选参数, 如: SansSerif,plain|Serif,bold|Monospaced,plain, 多个字体用'|'分隔, 默认: 随机
     */
    java.util.List<Font> getFonts();

    /**
     * @return 图片生成效果参数设置: 缩放, 取值范围: [0-1, 0-1], 默认: 1,1
     */
    float[] getEffectScale();

    /**
     * @return 图片生成效果参数设置: 波浪, 默认值: true
     */
    boolean isEffectRipple();

    /**
     * @return 图片生成效果参数设置: 模糊, 默认值: true
     */
    boolean isEffectBlur();

    /**
     * @return 图片生成效果参数设置: 轮廓, 默认值: false
     */
    boolean isEffectOutline();

    /**
     * @return 图片生成效果参数设置: 旋转, 默认值: true
     */
    boolean isEffectRatale();
}
