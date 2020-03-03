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

import com.github.cage.image.Painter;
import net.ymate.platform.core.beans.annotation.Ignored;
import net.ymate.platform.core.support.IInitialization;

import java.awt.*;
import java.util.Set;

/**
 * @author 刘镇 (suninformation@163.com) on 2016/11/27 04:58
 */
@Ignored
public interface ICaptchaConfig extends IInitialization<ICaptcha> {

    String DEFAULT_SCOPE = "default";

    String DEFAULT_SCOPE_NAME = "scope";

    String DEFAULT_CAPTCHA_SCOPE_PARAM_NAME = "captcha_scope";

    String DEFAULT_CAPTCHA_SCOPE_COOKIE_NAME = "module.captcha_scope";

    String DEFAULT_CAPTCHA_SCOPE_HEADER_NAME = "X-ModuleCaptcha-Scope";

    //

    String ENABLED = "enabled";

    String DEV_MODE = "dev_mode";

    String CAPTCHA_TYPES = "captcha_types";

    String PROVIDER_CLASS = "provider_class";

    String STORAGE_ADAPTER_CLASS = "storage_adapter_class";

    String TOKEN_GENERATOR_CLASS = "token_generator_class";

    String SCOPE_PROCESSOR_CLASS = "scope_processor_class";

    String SEND_PROCESSOR_CLASS = "send_provider_class";

    String NEED_CAPTCHA_WRONG_TIMES = "need_captcha_wrong_times";

    String CACHE_NAME_PREFIX = "cache_name_prefix";

    String TOKEN_LENGTH_MIN = "token_length_min";

    String TOKEN_TIMEOUT = "token_timeout";

    String SERVICE_PREFIX = "service_prefix";

    String SERVICE_ENABLED = "service_enabled";

    String HEIGHT = "height";

    String WIDTH = "width";

    String FOREGROUNDS = "foregrounds";

    String BACKGROUND = "background";

    String QUALITY = "quality";

    String COMPRESS_RATIO = "compress_ratio";

    String FORMAT = "format";

    String FONTS_PARSER_CLASS = "fonts_parser_class";

    String FONTS = "fonts";

    String EFFECT_SCALE = "effect.scale";

    String EFFECT_RIPPLE = "effect.ripple";

    String EFFECT_BLUR = "effect.blur";

    String EFFECT_OUTLINE = "effect.outline";

    String EFFECT_ROTATE = "effect.rotate";

    /**
     * 模块是否已启用, 默认值: true
     *
     * @return 返回false表示禁用
     */
    boolean isEnabled();

    /**
     * 是否开启调试模式(调试模式下控制台将输出生成的验证码, 同时短信验证码也不会被真正发送), 默认值: false
     *
     * @return 返回true表示已开启调试模式
     */
    boolean isDevelopMode();

    /**
     * 开启验证码类型, 取值范围: ALL|DEFAULT|SMS|MAIL, 默认值: ALL
     *
     * @return 返回验证码类型集合
     */
    Set<ICaptcha.Type> getCaptchaTypes();

    /**
     * 验证码服务提供者, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaProvider
     *
     * @return 返回验证码服务提供者
     */
    ICaptchaProvider getCaptchaProvider();

    /**
     * 自定义验证码生成器, 默认值: 空
     *
     * @return 返回自定义验证码生成器
     */
    ICaptchaTokenGenerator getTokenGenerator();

    /**
     * 验证码存储适配器, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaStorageAdapter
     *
     * @return 返回验证码存储适配器
     */
    ICaptchaStorageAdapter getStorageAdapter();

    /**
     * 手机短信或邮件验证码发送服务提供者, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaSendProvider
     *
     * @return 返回发送服务提供者
     */
    ICaptchaSendProvider getSendProvider();

    /**
     * 作用域标识扩展处理器, 开启错误计数时为必须, 默认值: 空
     *
     * @return 返回作用域标识扩展处理器
     */
    ICaptchaScopeProcessor getScopeProcessor();

    /**
     * 设置在达到指定错误次数上限后开启验证码, 默认为0则表示不开启错误计数特性
     *
     * @return 返回错误次数
     */
    int getNeedCaptchaWrongTimes();

    /**
     * 缓存名称前缀, 默认值: ""
     *
     * @return 返回缓存名称前缀
     */
    String getCacheNamePrefix();

    /**
     * 默认验证码控制器服务请求映射前缀(不允许'/'开始和结束), 默认值: ""
     *
     * @return 返回服务请求映射前缀
     */
    String getServicePrefix();

    /**
     * 是否注册默认验证码控制器, 默认值: false
     *
     * @return 返回true表示注册
     */
    boolean isServiceEnabled();

    // ----------

    /**
     * 验证码最小字符长度, 默认值: 4
     *
     * @return 返回验证码最小字符长度
     */
    int getTokenLengthMin();

    /**
     * 验证码超时时间, 单位: 秒, 默认: 0, 小于等于0均表示不限制
     *
     * @return 返回验证码超时时间
     */
    int getTokenTimeout();

    /**
     * 高度, 默认: 70px
     *
     * @return 返回高度
     */
    int getHeight();

    /**
     * 宽度, 默认: 200px
     *
     * @return 返回宽度
     */
    int getWidth();

    /**
     * 前景色, RGB值, 如: 0,0,0|1,2,3, 多个颜色用'|'分隔, 默认: 随机
     *
     * @return 返回前景色集合
     */
    java.util.List<Color> getForegrounds();

    /**
     * 背景色, RBG值, 默认: 255,255,255
     *
     * @return 返回背景色
     */
    Color getBackground();

    /**
     * 质量, 可选值: min|default|max, 默认: max
     *
     * @return 返回质量
     */
    Painter.Quality getQuality();

    /**
     * 压缩比, 0-1之间, 默认: 空
     *
     * @return 返回压缩比
     */
    Float getCompressRatio();

    /**
     * 图片格式, 可选值: png|jpeg, 默认: jpeg
     *
     * @return 返回图片格式
     */
    String getFormat();

    /**
     * 自定义字体, 可选参数, 如: SansSerif,plain|Serif,bold|Monospaced,plain, 多个字体用'|'分隔, 默认: 随机
     *
     * @return 返回自定义字体集合
     */
    java.util.List<Font> getFonts();

    /**
     * 图片生成效果参数设置: 缩放, 取值范围: [0-1, 0-1], 默认: 1,1
     *
     * @return 返回图片缩放设置
     */
    float[] getEffectScale();

    /**
     * 图片生成效果参数设置: 波浪, 默认值: true
     *
     * @return 返回true表示使用波浪
     */
    boolean isEffectRipple();

    /**
     * 图片生成效果参数设置: 模糊, 默认值: true
     *
     * @return 返回true表示使用模糊
     */
    boolean isEffectBlur();

    /**
     * 图片生成效果参数设置: 轮廓, 默认值: false
     *
     * @return 返回true表示使用轮廓
     */
    boolean isEffectOutline();

    /**
     * 图片生成效果参数设置: 旋转, 默认值: true
     *
     * @return 返回true表示使用旋转
     */
    boolean isEffectRotate();
}