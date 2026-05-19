# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Gradle uses the custom gradlew.bat (Windows) which hardcodes JAVA_HOME
# to C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot

# Debug build APK output: app/build/outputs/apk/debug/app-debug.apk
```

## Architecture

**Stack:** Kotlin 2.3.20 + Jetpack Compose (Material 3) + AGP 9.0.1 + Gradle 9.4.1

**Pattern:** MVVM + Clean Architecture with Hilt DI. Four layers:

```
ui/  →  domain/  →  data/  →  core/
(Screen + VM)  (model + repository interface)  (repository impl + DTO + API + Room)  (cross-cutting)
```

### Key Layers

- **`core/network/`** — OkHttp interceptors. `AuthInterceptor` captures & injects Bilibili cookies (SESSDATA/bili_jct) from DataStore. `WbiSignInterceptor` is a stub (WBI signing not yet wired). `NetworkModule` provides Retrofit singleton pointing at `https://api.bilibili.com/`.

- **`core/player/`** — `BiliPlayer` wraps Media3 ExoPlayer as a Hilt singleton. Exposes `StateFlow<PlayerState>` and `getExoPlayer()` for `PlayerView` binding. Supports DASH and progressive playback.

- **`core/danmaku/`** — `DanmakuEngine` (singleton, `@Inject`) manages active danmaku items synced to player position. `DanmakuRenderer` draws text on Android Canvas. `DanmakuOverlay` is the Compose composable that renders bullet comments at ~60fps. `DanmakuParser` handles Bilibili XML danmaku format (protobuf parsing is TODO).

- **`data/model/BiliModels.kt`** — All API DTOs (`@Serializable` with `@SerialName`) plus `toDomainVideo()` extension functions. `BiliResponse<T>` wraps all API responses (`code`, `message`, `data`).

- **`data/repository/`** — Repository implementations call `BiliApi` (Retrofit), unwrap `Response<BiliResponse<T>>` via `.body()`, map DTOs → domain models.

- **`domain/repository/`** — Pure Kotlin interfaces. Return `Result<T>` for fallible operations.

- **`data/local/SettingsStore.kt`** — DataStore wrapper for preferences (dark mode, danmaku config, session cookies `sessdata`/`bili_jct`).

- **`di/`** — Three Hilt modules: `AppModule` (SettingsStore), `DatabaseModule` (Room), `RepositoryModule` (binds all 5 repository interfaces to impls).

### Navigation

Single-activity (`MainActivity.kt`) with Navigation Compose. Routes defined as sealed class in `ui/navigation/Routes.kt`. `NavGraph.kt` wires all 8 destinations. `VideoViewModel` and `LiveViewModel` use `SavedStateHandle` to read nav arguments.

### HomeViewModel → HomeScreen

`HomeViewModel` injects `VideoRepository`, `LiveRepository`, `AuthRepository`. Calls `getHotVideos()` and `getLiveRooms()` in parallel on init. `HomeScreen` has `PrimaryTabRow` (热门/直播 tabs) and a 3-tab bottom nav (Home/Search/Downloads).

## Bilibili API Details

- **AuthInterceptor** auto-captures `Set-Cookie` headers from HTTP responses containing `SESSDATA=` or `bili_jct=`, persists to DataStore, and injects cookies into outgoing requests.
- The QR login flow: `getLoginQrCode()` → display image → `pollQrLogin()` every 2s → on confirm, `getCurrentUser()`.
- Danmaku endpoint (`x/v2/dm/web/seg.so`) returns the data directly as `ResponseBody` (not wrapped in `BiliResponse`), containing XML or protobuf.
- Most other endpoints return `Response<BiliResponse<T>>` — the Retrofit `Response` wrapper provides HTTP status/headers; `BiliResponse` provides the Bilibili API status code.

## Environment Notes

- **Windows 10**, JDK 21 at `C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot`
- Android SDK at `D:\Android\SDK`
- Android CLI v0.7.15433128 — `android sdk`, `android run`, `android emulator`, `android docs`
- `gradlew.bat` hardcodes JAVA_HOME to work around system JAVA_HOME pointing at Android Studio's JBR
- `gradle/wrapper/gradle-wrapper.properties` points to `gradle-9.4.1-bin.zip`

## Android Skills

This project has Android development skills installed in `skills/`. Use `Skill` tool when relevant. Key skills:

- **`agp-9-upgrade`** — AGP 9 migration (already applied; use for future AGP upgrades)
- **`android-cli`** — SDK management, emulator, APK deployment, layout inspection
- **`edge-to-edge`** — Edge-to-edge display configuration
- **`navigation-3`** — Navigation Compose type-safe patterns
- **`testing-setup`** — Test infrastructure setup
- **`migrate-xml-views-to-jetpack-compose`** — View-to-Compose migration
- **`perfetto-trace-analysis`** — Performance trace analysis
- **`r8-analyzer`** — R8/ProGuard analysis

Full list: `android skills list` or check `skills/` directory.
