# 哔哩哔哩 Android 客户端

基于 Kotlin + Jetpack Compose 的原生 Android 第三方 Bilibili 客户端，参考 [JKVideo](https://github.com/tiajinsha/JKVideo) 的功能设计。

## 功能

- **首页推荐** — 热门视频列表 + 直播房间网格，支持下拉刷新和无限滚动
- **视频播放** — DASH 流媒体播放（Media3 ExoPlayer），支持多清晰度切换
- **弹幕系统** — 5 轨道滚动弹幕 + 顶部/底部固定弹幕，Canvas 实时渲染
- **直播** — HLS 流播放，直播间信息展示
- **扫码登录** — Bilibili QR 码生成 + 轮询确认，Cookie 持久化
- **搜索** — 搜索建议（防抖）+ 视频/用户/直播搜索结果
- **UP 主空间** — 用户信息 + 视频列表（分页）
- **离线下载** — 下载管理界面（下载引擎开发中）
- **设置** — 深色模式、弹幕透明度/速度/字号、默认画质

## 技术栈

| 类别 | 选型 |
|---|---|
| 语言 | Kotlin 100% |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Clean Architecture |
| DI | Hilt |
| 网络 | Retrofit + OkHttp + Kotlinx Serialization |
| 视频 | Media3 ExoPlayer (DASH + HLS) |
| 弹幕 | 自定义 Canvas 渲染 |
| 图片 | Coil |
| 本地存储 | Room + DataStore |
| 导航 | Navigation Compose |
| 构建 | Gradle 9.4.1 + AGP 9.0.1 + Kotlin 2.3.20 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 |

## 构建

```bash
# 编译 Debug APK
./gradlew assembleDebug

# APK 输出路径: app/build/outputs/apk/debug/app-debug.apk
```

**环境要求：**
- JDK 21 (`C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot`)
- Android SDK (`D:\Android\SDK`)
- `gradlew.bat` 已硬编码 JAVA_HOME 路径

## 项目结构

```
app/src/main/java/com/bilibili/client/
├── core/                  # 基础设施
│   ├── network/           # OkHttp 拦截器（Auth/WBI签名）、Retrofit 配置
│   ├── player/            # ExoPlayer 封装（BiliPlayer）
│   ├── danmaku/           # 弹幕引擎（解析/渲染/覆层）
│   └── theme/             # Material 3 主题（Bilibili 粉色系）
├── data/                  # 数据层
│   ├── api/               # Retrofit API 接口
│   ├── model/             # DTO + 映射扩展函数
│   ├── repository/        # Repository 实现
│   └── local/             # Room 数据库 + DataStore
├── domain/                # 领域层
│   ├── model/             # 领域模型（Video, LiveRoom, User, DanmakuItem）
│   └── repository/        # Repository 接口
├── ui/                    # 表现层
│   ├── navigation/        # 导航图（8 个路由）
│   ├── home/              # 首页（热门 + 直播）
│   ├── video/             # 视频详情（播放器 + 弹幕 + 评论）
│   ├── live/              # 直播间（HLS 播放）
│   ├── search/            # 搜索（建议 + 结果）
│   ├── creator/           # UP 主空间
│   ├── download/          # 下载管理
│   ├── login/             # 扫码登录
│   ├── settings/          # 设置
│   └── components/        # 通用组件（Loading/Error/Empty/VideoPlayer）
└── di/                    # Hilt 依赖注入模块
```

## 数据流

```
Screen (Compose) ←→ ViewModel (StateFlow) ←→ Repository ←→ BiliApi (Retrofit)
                                                    ↕
                                              Room / DataStore
```

## Bilibili API

大部分接口返回 `Response<BiliResponse<T>>` 格式，其中 `BiliResponse` 包含 API 层面状态码。Cookie 认证由 `AuthInterceptor` 自动管理：从登录响应捕获 `Set-Cookie` 头，持久化到 DataStore，并在后续请求中自动注入。

弹幕接口（`x/v2/dm/web/seg.so`）直接返回 XML/Protobuf 数据，不经过 JSON 序列化。

## 许可

仅供学习交流使用。
