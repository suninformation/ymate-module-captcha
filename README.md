#### YMP-Captcha:

> 验证码模块；
> 
> - 支持图片、邮件和短信三种验证类型；
> - 支持多作用域验证生成；
> - 支持`debug`模式，该模式下短信不会被发送；
> - 支持根据限定条件判断是否启用验证码，如：请求n次后需要填写验证码；
> - 采用`@VCaptcha`注解验证，配置简单、灵活，可自定义扩展；

#### Maven包依赖

    <dependency>
        <groupId>net.ymate.module</groupId>
        <artifactId>ymate-module-captcha</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

#### 搭建模块工程

- 首先，你需要创建一个基于YMPv2框架的JavaWeb工程项目；（[如何快速搭建工程?](http://git.oschina.net/suninformation/ymate-platform-v2/wikis/Quickstart_New)）

- YMP框架扫描包路径要包含`net.ymate.module.captcha`, 调整配置如下:

        # 框架自动扫描的包路径集合，多个包名之间用'|'分隔，默认已包含net.ymate.platform包，其子包也将被扫描
        ymp.autoscan_packages=net.ymate

#### 使用方法说明

- 获取验证码图片

        http://<你的域名>/captcha?tokenId=<TOKEN_ID>&type=<TYPE>

    > tokenId：令牌标识ID, 用于区分不同客户端及数据存储范围，相同令牌ID间仅存在唯一验证码，可选参数；
    >
    > type：仅当`type=1`时采用Base64编码输出图片，可选参数；

- 发送短信验证码

        http://<你的域名>/captcha/sms_code?tokenId=<TOKEN_ID>&mobile=<MOBILE>

    > tokenId：令牌标识ID, 采用`sms`作为前缀，区别于图片验证码，可选参数；
    >
    > mobile：手机号码，必选参数；
    
    返回值说明：
    
        {ret: 0, msg: "..."}
    
    > - `ret=0` 表示发送成功
    > - `ret=-1` 表示参数验证错误
    > - `ret=-6` 表示发送频率过快或其它消息
    > - `ret=-50` 表示发送异常

- 发送邮件验证码

        http://<你的域名>/captcha/mail_code?tokenId=<TOKEN_ID>&email=<EMAIL>

    > tokenId：令牌标识ID, 采用`mail`作为前缀，区别于图片验证码，可选参数；
    >
    > email：邮件地址，必选参数；
    
    返回值说明：
    
        {ret: 0, msg: "..."}
    
    > - `ret=0` 表示发送成功
    > - `ret=-1` 表示参数验证错误
    > - `ret=-6` 表示发送频率过快或其它消息
    > - `ret=-50` 表示发送异常

- 检查验证码是否合法

        http://<你的域名>/captcha/match?tokenId=<TOKEN_ID>&token=<TOKEN>

    > tokenId：令牌标识ID, 可选参数；
    >
    > token：预验证的令牌值，必选参数；
    
    返回值：

        {ret: 0, matched: true|false}

#### 示例代码：

- 验证码注解`@VCaptcha`的使用
    
        @RequestMapping(value = "/login", method = Type.HttpMethod.POST)
        public IView __doLogin(@VCaptcha(invalid = true)
                               @RequestParam String captcha, // 验证码
                               
                               @VRequried
                               @VMobile
                               @RequestParam String mobile, // 手机号码
                                
                               @VRequried
                               @VCaptcha(tokenId = ICaptcha.Const.TOKEN_SMS)
                               @RequestParam String smscode, // 短信验证码
    
                               @VRequried
                               @RequestParam String passwd, // 登录密码
    
                               @RequestParam(Optional.REDIRECT_URL) String redirectUrl) throws Exception {
            // ...... 省略
            return WebResult.SUCCESS().toJSON();
        }

- 验证码相关方法调用

        // 生成作用域为user.login的验证码
        String _code = Captcha.get().generate("user.login");
        
        // 销毁作用域为user.login的验证码
        Captcha.get().invalidate("user.login");
        
        // 判断是否开启错误记数，开启后将支持跳过参数验证
        Captcha.get().isWrongTimesEnabled();
        
        // 判断作用域为user.login的验证码是否允许忽略
        Captcha.get().isValidationNeedSkip("user.login");
        
        // 重置作用域为user.login的验证码错误计数器
        Captcha.get().resetWrongTimes("user.login");
        
        // 验证作用域为user.login的验证码是否匹配以及验证后是否使其失效
        Captcha.get().validate("user.login", _code, true);

#### 模块配置参数说明

    #-------------------------------------
    # module.captcha 模块初始化参数
    #-------------------------------------
    
    # 验证码模块是否已被禁用(禁用后将忽略所有验证码相关参数验证), 默认值: false
    ymp.configs.module.captcha.disabled=
    
    # 是否开启调试模式(调试模式下控制台将输出生成的验证码, 同时短信验证码也不会被真正发送), 默认值: false
    ymp.configs.module.captcha.dev_mode=
    
    # 验证码服务提供者类, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaProvider
    ymp.configs.module.captcha.provider_class=
    
    # 验证码存储适配器类, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaStorageAdapter
    ymp.configs.module.captcha.storage_adapter_class=
    
    # 身份令牌标识扩展处理器, 默认值: 空
    ymp.configs.module.captcha.token_processor_class=
    
    # 邮件验证码发送服务提供者类, 默认值: 空
    ymp.configs.module.captcha.mail_send_provider_class=
    
    # 邮件验证码模板视图路径, 默认值: captcha_email
    ymp.configs.module.captcha.mail_template_view=
    
    # 验证码邮件主题, 默认值: 空
    ymp.configs.module.captcha.mail_subject=
    
    # 相同令牌标识范围的邮件验证码重复发送的是时间间隔(秒), 默认值: 300秒
    ymp.configs.module.captcha.mail_send_time_interval=
    
    # 手机短信验证码发送服务提供者类, 默认值: 空
    ymp.configs.module.captcha.sms_send_provider_class=
    
    # 手机短信验证码内容模板, 默认值: ${captcha}
    ymp.configs.module.captcha.sms_content_template=
    
    # 相同令牌标识范围的短信验证码重复发送的是时间间隔(秒), 默认值: 120秒
    ymp.configs.module.captcha.sms_send_time_interval=
    
    # 设置在达到指定错误次数上限后开启验证码, 默认值: 0, 表示不开启错误记数特性
    ymp.configs.module.captcha.need_captcha_wrong_times=
    
    # 缓存名称前缀, 默认值: ""
    ymp.configs.module.captcha.cache_name_prefix=
    
    # 验证码最小字符长度, 默认值: 4
    ymp.configs.module.captcha.token_length_min=
    
    # 验证码超时时间, 单位: 秒, 默认: 空, 空或小于等于0均表示不限制
    ymp.configs.module.captcha.token_timeout=
    
    # 高度, 默认: 70px
    ymp.configs.module.captcha.height=
    
    # 宽度, 默认: 200px
    ymp.configs.module.captcha.width=
    
    # 前景色, RGB值, 如: 0,0,0|1,2,3, 多个颜色用'|'分隔, 默认: 随机
    ymp.configs.module.captcha.foregrounds=
    
    # 背景色, RBG值, 默认: 255,255,255
    ymp.configs.module.captcha.background=
    
    # 质量, 可选值: min|default|max, 默认: max
    ymp.configs.module.captcha.quality=
    
    # 压缩比, 0-1之间, 默认: 空
    ymp.configs.module.captcha.compress_ratio=
    
    # 图片格式, 可选值: png|jpeg, 默认: jpeg
    ymp.configs.module.captcha.format=
    
    # 自定义字体, 可选参数, 如: SansSerif,plain|Serif,bold|Monospaced,plain, 多个字体用'|'分隔, 默认: 随机
    ymp.configs.module.captcha.fonts=
    
    # 图片生成效果参数设置: 缩放, 取值范围: [0-1, 0-1], 默认: 1,1
    ymp.configs.module.captcha.effect.scale=
    
    # 图片生成效果参数设置: 波浪, 默认值: true
    ymp.configs.module.captcha.effect.ripple=
    
    # 图片生成效果参数设置: 模糊, 默认值: true
    ymp.configs.module.captcha.effect.blur=
    
    # 图片生成效果参数设置: 轮廓, 默认值: false
    ymp.configs.module.captcha.effect.outline=
    
    # 图片生成效果参数设置: 旋转, 默认值: true
    ymp.configs.module.captcha.effect.ratale=

#### One More Thing

YMP不仅提供便捷的Web及其它Java项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入 官方QQ群480374360，一起交流学习，帮助YMP成长！

了解更多有关YMP框架的内容，请访问官网：http://www.ymate.net/