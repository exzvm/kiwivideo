# Kiwi Video

一个最小可用的 Android 电影浏览与播放 App（MVP）。

## 当前功能

- **首页**：电影海报网格（自适应列数），加载中 / 加载失败 / 重试状态
- **搜索 / 筛选**：按片名即时搜索、按类型筛选，两者可组合；无匹配时显示提示并可一键清除筛选
- **详情页**：海报、简介、年份 / 时长 / 类型，播放入口
- **播放页**：Media3 ExoPlayer，支持 HLS 与 MP4，全屏沉浸模式、播放/暂停/进度、进入后台自动暂停、播放错误提示
- **数据**：内置 `assets/movies.json`（8 部 Blender 开源电影），无网络时海报自动降级为渐变占位图

## 技术栈

| 层 | 选型 |
|---|---|
| UI | Kotlin 2.2、Jetpack Compose、Material 3、单 Activity |
| 导航 | Navigation Compose（type-safe routes） |
| 架构 | MVVM + StateFlow + Repository |
| 播放 | Media3 ExoPlayer + `media3-exoplayer-hls` |
| 图片 / 数据 | Coil 3、kotlinx.serialization |
| 测试 | JUnit4、kotlinx-coroutines-test |

## 目录结构

```
app/src/main/
├── assets/movies.json                 # 内置片单
└── java/com/dylanxy/kiwivideo/
    ├── MainActivity.kt                # 入口：加载片单 + 主题
    ├── data/
    │   ├── MovieRepository.kt         # 数据源接口
    │   ├── MovieCatalogJson.kt        # JSON 解析
    │   ├── model/Movie.kt
    │   └── local/LocalMovieRepository.kt
    └── ui/
        ├── home/                      # 首页 + ViewModel
        ├── detail/                    # 详情页 + 共用 ViewModel
        ├── player/                    # ExoPlayer 播放页
        ├── navigation/                # 路由与 NavHost
        ├── components/                # 海报、加载/错误态
        └── theme/
```

## 构建与测试

```bash
./gradlew testDebugUnitTest   # 24 个 JVM 单测
./gradlew assembleDebug       # 生成 APK
./gradlew lintDebug
```

APK 输出：`app/build/outputs/apk/debug/app-debug.apk`

> 如果终端提示找不到 Java，请指向 Android Studio 内置 JDK：
> ```bash
> export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
> ```

## 片单与片源

片单在 `app/src/main/assets/movies.json`，字段：

```json
{
  "id": "sintel",
  "title": "Sintel",
  "year": 2010,
  "durationMinutes": 15,
  "genres": ["动画", "奇幻"],
  "synopsis": "……",
  "posterUrl": "https://…",
  "videoUrl": "https://….m3u8"
}
```

- 演示片源为 [Blender 基金会](https://www.blender.org/) 的开源电影（CC BY），视频流来自其公开下载站及公开测试流。**正式发布前请替换为自有版权或已授权的片源。**
- 海报使用 placehold.co 占位图；无网络时显示由影片 id 生成的渐变占位图。

## 接入远程 API

UI 与 ViewModel 只依赖 `MovieRepository` 接口：

1. 新建 `RemoteMovieRepository : MovieRepository`（Retrofit / Ktor）
2. 在 `MainActivity` 中把 `LocalMovieRepository` 替换为远程实现
3. 页面与测试无需改动

## 已知限制（V2 候选）

- 无登录、收藏、观看历史、下载
- 播放器暂无倍速、字幕、手势调节、清晰度切换
- 片单是静态文件，暂无后端
- `compileSdk = 37.1`（依赖 `core-ktx 1.19` 的硬性要求），`targetSdk = 36`
