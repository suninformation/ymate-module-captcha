# YMATE-MODULE-CAPTCHA

[![Maven Central status](https://img.shields.io/maven-central/v/net.ymate.module/ymate-module-captcha.svg)](https://search.maven.org/artifact/net.ymate.module/ymate-module-captcha)
[![LICENSE](https://img.shields.io/github/license/suninformation/ymate-module-captcha.svg)](https://gitee.com/suninformation/ymate-module-captcha/blob/master/LICENSE)


基于 YMP 框架实现的验证码模块，支持图片、邮件和短信三种验证类型，采用注解验证，配置简单、灵活，可自定义扩展，主要特性如下：

- 支持图片、邮件和短信三种验证类型；
- 支持多作用域验证码生成；
- 支持 `debug` 模式，该模式下短信不会被发送；
- 支持根据限定条件判断是否启用验证码，如：请求 `n` 次后需要填写验证码；
- 采用 `@VCaptcha` 注解验证，配置简单、灵活，可自定义扩展；



## Maven包依赖

```xml
<dependency>
    <groupId>net.ymate.module</groupId>
    <artifactId>ymate-module-captcha</artifactId>
    <version>2.0.0</version>
</dependency>
```



## 模块配置参数说明

```properties
#-------------------------------------
# module.captcha 模块初始化参数
#-------------------------------------

# 验证码模块模块是否已启用(禁用后将忽略所有验证码相关参数验证), 默认值: true
ymp.configs.module.captcha.enabled=

# 是否开启调试模式(调试模式下控制台将输出生成的验证码, 同时短信验证码也不会被真正发送), 默认值: false
ymp.configs.module.captcha.dev_mode=

# 开启验证码类型, 取值范围: ALL|DEFAULT|SMS|MAIL, 默认值: ALL
ymp.configs.module.captcha.captcha_types=

# 验证码服务提供者类, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaProvider
ymp.configs.module.captcha.provider_class=

# 自定义验证码生成器类, 默认值: 空
ymp.configs.module.captcha.token_generator_class=

# 验证码存储适配器类, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaStorageAdapter
ymp.configs.module.captcha.storage_adapter_class=

# 作用域标识扩展处理器, 开启错误记数时为必须, 默认值: 空
ymp.configs.module.captcha.scope_processor_class=

# 手机短信或邮件验证码发送服务提供者类, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaSendProvider
ymp.configs.module.captcha.send_provider_class=

# 设置在达到指定错误次数上限后开启验证码, 默认值: 0, 表示不开启错误记数特性
ymp.configs.module.captcha.need_captcha_wrong_times=

# 缓存名称前缀, 默认值: ""
ymp.configs.module.captcha.cache_name_prefix=

# 验证码最小字符长度, 默认值: 4
ymp.configs.module.captcha.token_length_min=

# 验证码超时时间, 单位: 秒, 默认: 空, 空或小于等于0均表示不限制
ymp.configs.module.captcha.token_timeout=

# 默认验证码控制器服务请求映射前缀(不允许'/'开始和结束), 默认值: ""
ymp.configs.module.captcha.service_prefix=

# 是否注册默认验证码控制器, 默认值: false
ymp.configs.module.captcha.service_enabled=

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

# 自定义字体配置分析器, 可选参数, 默认值: net.ymate.module.captcha.impl.DefaultCaptchaFontsParser
# 若希望通过指定文件加载字体, 请使用: net.ymate.module.captcha.impl.FileCaptchaFontsParser
# 或通过实现net.ymate.module.captcha.ICaptchaFontsParser接口自定义加载方式
ymp.configs.module.captcha.fonts_parser_class=

# 自定义字体, 可选参数, 格式: [字体名称:SansSerif],[字体样式:plain|bold|italic], 多个字体用'|'分隔, 默认: 随机
# 当自定义字体配置分析器为通过文件加载时, 配置格式: [字体格式:type1|truetype],[字体样式:plain|bold|italic],<文件路径:${root}/fonts/SansSerif.ttf>, 多个字体用'|'分隔
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
ymp.configs.module.captcha.effect.rotate=
```



## 使用方法说明

在 Web 环境下，当配置 `service_enabled=true` 时，模块将在初始化时注册默认验证码控制器，该控制器提供以下接口能力：



### 获取验证码图片

```shell
http://<你的域名>/captcha?scope=<SCOPE>&type=<TYPE>
```

参数说明：

- scope：作用域标识，用于区分不同客户端及数据存储范围，必选参数；

- type：取值范围:`data|json`，采用Base64编码输出图片，默认为空，可选参数；



### 发送短信验证码

```shell
http://<你的域名>/captcha/sms_code?scope=<SCOPE>&mobile=<MOBILE>&captcha=<CAPTCHA>
```

参数说明：

- scope：作用域标识，用于区分不同客户端及数据存储范围，必选参数；

- mobile：手机号码，必选参数；

- captcha：图片验证码（通过`http://<你的域名>/captcha?scope=<SCOPE>`获取）

返回值说明：

```json
{ret: 0}
```

- `0` 表示发送成功
- `-1` 表示参数验证错误
- `-6` 表示发送频率过快或其它消息
- `-50` 表示发送异常



### 发送邮件验证码

```shell
http://<你的域名>/captcha/mail_code?scope=<SCOPE>&email=<EMAIL>&captcha=<CAPTCHA>
```

参数说明：

- scope：作用域标识，用于区分不同客户端及数据存储范围，必选参数；

- email：邮件地址，必选参数；

- captcha：图片验证码（通过`http://<你的域名>/captcha?scope=<SCOPE>`获取）

返回值说明：

```json
{ret: 0}
```

- `0` 表示发送成功
- `-1` 表示参数验证错误
- `-6` 表示发送频率过快或其它消息
- `-50` 表示发送异常



### 检查验证码是否合法

```shell
http://<你的域名>/captcha/match?scope=<SCOPE>&token=<TOKEN>&target=<TARGET>
```

参数说明：

- scope：作用域标识，用于区分不同客户端及数据存储范围，必选参数；

- token：预验证的令牌值，必选参数；

- target：当验证手机或邮件时必须指定对应的手机号或邮箱地址；

返回值说明：

```json
{ret: 0, matched: true|false}
```



## 示例代码

### 验证码注解 `@VCaptcha` 的使用

```java
@RequestMapping(value = "/login", method = Type.HttpMethod.POST)
public IView login(@VCaptcha(invalid = true)
                   @RequestParam String captcha, // 验证码

                   @VRequired
                   @VMobile
                   @RequestParam String mobile, // 手机号码

                   @VRequired
                   @RequestParam String scope, // 短信验证码作用域标识

                   @VRequired
                   @VCaptcha(type = ICaptcha.Type.MAIL, scopeName = "scope", targetName="mobile")
                   @RequestParam String smscode, // 短信验证码

                   @VRequired
                   @RequestParam String passwd, // 登录密码

                   @RequestParam(Optional.REDIRECT_URL) String redirectUrl) throws Exception {
    // ...... 省略
    return WebResult.success().toJsonView();
}
```



### 验证码相关方法调用

```java
// 生成作用域为user.login.xxx的验证码
String code = Captcha.get().generate("user.login.xxx");

// 销毁作用域为user.login.xxx的验证码
Captcha.get().invalidate("user.login.xxx");

// 判断是否开启错误记数，开启后将支持跳过参数验证
Captcha.get().isWrongTimesEnabled();

// 判断作用域为user.login.xxx的验证码是否允许忽略
Captcha.get().isValidationNeedSkip(ICaptcha.Type.DEFAULT, "user.login.xxx");

// 重置作用域为user.login.xxx的验证码错误计数器
Captcha.get().resetWrongTimes(ICaptcha.Type.DEFAULT, "user.login.xxx");

// 验证作用域为user.login.xxx的验证码是否匹配以及验证后是否使其失效
Captcha.get().validate("user.login.xxx", code, true);
```



## One More Thing

YMP 不仅提供便捷的 Web 及其它 Java 项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入官方 QQ 群：[480374360](https://qm.qq.com/cgi-bin/qm/qr?k=3KSXbRoridGeFxTVA8HZzyhwU_btZQJ2)，一起交流学习，帮助 YMP 成长！

如果喜欢 YMP，希望得到你的支持和鼓励！

![Donation Code](https://ymate.net/img/donation_code.png)

了解更多有关 YMP 框架的内容，请访问官网：[https://ymate.net](https://ymate.net)