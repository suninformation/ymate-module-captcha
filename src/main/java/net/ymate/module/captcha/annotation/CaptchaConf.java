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
package net.ymate.module.captcha.annotation;

import com.github.cage.image.Painter;
import net.ymate.module.captcha.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

/**
 * @author 刘镇 (suninformation@163.com) on 2020/03/11 21:51
 * @since 2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CaptchaConf {

    /**
     * @return 模块是否已启用, 默认值: true
     */
    boolean enabled() default true;

    /**
     * @return 是否开启调试模式(调试模式下控制台将输出生成的验证码, 同时短信验证码也不会被真正发送), 默认值: false
     */
    boolean developMode() default false;

    /**
     * @return 开启验证码类型, 取值范围: ALL|DEFAULT|SMS|MAIL, 默认值: ALL
     */
    ICaptcha.Type[] captchaTypes() default ICaptcha.Type.ALL;

    /**
     * @return 验证码服务提供者, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaProvider
     */
    Class<? extends ICaptchaProvider> providerClass() default ICaptchaProvider.class;

    /**
     * @return 自定义验证码生成器, 默认值: 空
     */
    Class<? extends ICaptchaTokenGenerator> tokenGeneratorClass() default ICaptchaTokenGenerator.class;

    /**
     * @return 验证码存储适配器, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaStorageAdapter
     */
    Class<? extends ICaptchaStorageAdapter> storageAdapterClass() default ICaptchaStorageAdapter.class;

    /**
     * @return 手机短信或邮件验证码发送服务提供者, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaSendProvider
     */
    Class<? extends ICaptchaSendProvider> sendProviderClass() default ICaptchaSendProvider.class;

    /**
     * @return 作用域标识扩展处理器, 开启错误计数时为必须, 默认值: 空
     */
    Class<? extends ICaptchaScopeProcessor> scopeProcessor() default ICaptchaScopeProcessor.class;

    /**
     * @return 设置在达到指定错误次数上限后开启验证码, 默认为0则表示不开启错误计数特性
     */
    int needCaptchaWrongTimes() default 0;

    /**
     * @return 缓存名称前缀, 默认值: ""
     */
    String cacheNamePrefix() default StringUtils.EMPTY;

    /**
     * @return 默认验证码控制器服务请求映射前缀(不允许 ' / ' 开始和结束), 默认值: ""
     */
    String servicePrefix() default StringUtils.EMPTY;

    /**
     * @return 是否注册默认验证码控制器, 默认值: false
     */
    boolean serviceEnabled() default false;

    // ----------

    /**
     * @return 验证码最小字符长度, 默认值: 4
     */
    int tokenLengthMin() default 0;

    /**
     * @return 验证码超时时间, 单位: 秒, 默认: 0, 小于等于0均表示不限制
     */
    int tokenTimeout() default 0;

    /**
     * @return 高度, 默认: 70px
     */
    int height() default 0;

    /**
     * @return 宽度, 默认: 200px
     */
    int width() default 0;

    /**
     * @return 前景色, RGB值, 如: 0,0,0|1,2,3, 多个颜色用'|'分隔, 默认: 随机
     */
    String[] foregrounds() default {};

    /**
     * @return 背景色, RBG值, 默认: 255,255,255
     */
    String background() default StringUtils.EMPTY;

    /**
     * @return 质量, 可选值: min|default|max, 默认: max
     */
    Painter.Quality quality() default Painter.Quality.MAX;

    /**
     * @return 压缩比, 0-1之间, 默认: 空
     */
    float compressRatio() default 0;

    /**
     * @return 图片格式, 可选值: png|jpeg, 默认: jpeg
     */
    String format() default StringUtils.EMPTY;

    /**
     * @return 自定义字体, 可选参数, 如: SansSerif,plain|Serif,bold|Monospaced,plain, 多个字体用'|'分隔, 默认: 随机
     */
    String[] fonts() default {};

    /**
     * @return 自定义字体配置分析器
     */
    Class<? extends ICaptchaFontsParser> fontsParserClass() default ICaptchaFontsParser.class;

    /**
     * @return 图片生成效果参数设置: 缩放, 取值范围: [0-1, 0-1], 默认: 1,1
     */
    float[] effectScale() default {};

    /**
     * @return 图片生成效果参数设置: 波浪, 默认值: true
     */
    boolean effectRipple() default true;

    /**
     * @return 图片生成效果参数设置: 模糊, 默认值: true
     */
    boolean effectBlur() default true;

    /**
     * @return 图片生成效果参数设置: 轮廓, 默认值: false
     */
    boolean effectOutline() default false;

    /**
     * @return 图片生成效果参数设置: 旋转, 默认值: true
     */
    boolean effectRotate() default true;
}
