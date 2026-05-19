# Bilibili Android Client

[English](README.md) | [中文](README_CN.md)

A native Android third-party Bilibili client built with Kotlin and Jetpack Compose, inspired by [JKVideo](https://github.com/tiajinsha/JKVideo).

## Features

- **Home Feed** — Hot videos list + live room grid, pull-to-refresh and infinite scroll
- **Video Player** — DASH streaming via Media3 ExoPlayer, multi-quality switching
- **Danmaku** — 5-lane rolling + top/bottom fixed bullet comments, real-time Canvas rendering
- **Live Streaming** — HLS playback with room info display
- **QR Code Login** — Bilibili QR generation + polling confirmation, persistent cookie auth
- **Search** — Debounced suggestions + results (videos, users, live rooms)
- **Creator Channel** — User profile + video grid with pagination
- **Downloads** — Download management UI (download engine in development)
- **Settings** — Dark mode, danmaku opacity/speed/font, default quality

## Tech Stack

| Category | Choice |
|---|---|
| Language | Kotlin 100% |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Networking | Retrofit + OkHttp + Kotlinx Serialization |
| Video | Media3 ExoPlayer (DASH + HLS) |
| Danmaku | Custom Canvas rendering |
| Image | Coil |
| Local Storage | Room + DataStore |
| Navigation | Navigation Compose |
| Build | Gradle 9.4.1 + AGP 9.0.1 + Kotlin 2.3.20 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 |

## Build

```bash
# Build debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

**Requirements:**
- JDK 21
- Android SDK

## Project Structure

```
app/src/main/java/com/bilibili/client/
├── core/                  # Cross-cutting
│   ├── network/           # OkHttp interceptors (Auth, WBI sign), Retrofit config
│   ├── player/            # ExoPlayer wrapper (BiliPlayer)
│   ├── danmaku/           # Danmaku engine (parser, renderer, overlay)
│   └── theme/             # Material 3 theme (Bilibili pink)
├── data/                  # Data layer
│   ├── api/               # Retrofit API interfaces
│   ├── model/             # DTOs + mapping extensions
│   ├── repository/        # Repository implementations
│   └── local/             # Room database + DataStore
├── domain/                # Domain layer
│   ├── model/             # Domain models (Video, LiveRoom, User, DanmakuItem)
│   └── repository/        # Repository interfaces
├── ui/                    # Presentation layer
│   ├── navigation/        # Navigation graph (8 routes)
│   ├── home/              # Home screen (hot + live)
│   ├── video/             # Video detail (player + danmaku + comments)
│   ├── live/              # Live room (HLS player)
│   ├── search/            # Search (suggestions + results)
│   ├── creator/           # Creator channel
│   ├── download/          # Download management
│   ├── login/             # QR code login
│   ├── settings/          # Settings
│   └── components/        # Shared components (Loading/Error/Empty/VideoPlayer)
└── di/                    # Hilt DI modules
```

## Data Flow

```
Screen (Compose) ←→ ViewModel (StateFlow) ←→ Repository ←→ BiliApi (Retrofit)
                                                    ↕
                                              Room / DataStore
```

## Bilibili API

Most endpoints return `Response<BiliResponse<T>>` where `BiliResponse` carries the API-level status code. Cookie authentication is handled automatically by `AuthInterceptor`: it captures `Set-Cookie` headers from login responses, persists them to DataStore, and injects them into subsequent requests.

The danmaku endpoint (`x/v2/dm/web/seg.so`) returns raw XML/Protobuf data without JSON serialization.

## License

For educational and personal use only.
