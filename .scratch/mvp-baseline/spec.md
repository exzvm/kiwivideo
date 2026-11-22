Status: implemented

# Spec：Kiwi Video 当前 MVP 实现基线

> 本文件记录**已经实现**的功能与结构，作为后续迭代的基线。它描述的是「现在是什么」，不是待开发的新功能；因此状态标记为 `implemented` 而非 `ready-for-agent`。

## Problem Statement

用户想在手机上快速浏览一组电影、了解影片信息并直接播放，而不需要注册账号、配置后端或等待内容分发。对这样一个最小可用产品而言，核心诉求只有三点：

- 打开应用就能看到可点选的片单；
- 点进一部影片能看清楚简介、年份、时长、类型；
- 点播放就能完整观看影片，并且网络慢、加载失败、进入后台等常见情况不会让人不知所措。

## Solution

Kiwi Video 是一个单 Activity 的 Jetpack Compose 应用，提供三个页面：

- **首页**：自适应列数的电影海报网格，覆盖加载中、加载失败 / 重试、成功三种状态；
- **详情页**：海报、片名、年份 / 时长 / 类型、简介与「播放」按钮；
- **播放页**：基于 Media3 ExoPlayer 的播放器，支持 HLS 与 MP4，沉浸式全屏，进入后台自动暂停，播放出错时给出提示。

数据来自应用内置的静态片单（8 部 Blender 基金会开源电影）。无网络时海报自动降级为按影片 id 生成的渐变占位图，因此应用在弱网 / 离线环境下仍有可用的浏览与播放体验（视频本身仍需网络）。

## User Stories

1. As a 首次打开应用的观众，I want 直接看到电影海报网格，so that 我不需要任何前置步骤就能开始浏览。
2. As a 观众，I want 海报网格根据屏幕宽度自动调整列数，so that 手机和平板上都不会出现海报过大或过小。
3. As a 观众，I want 每张海报卡片显示片名与年份 / 时长，so that 我不点进去也能大致判断影片。
4. As a 观众，I want 片单加载时看到明确的加载指示，so that 我不会误以为应用卡死。
5. As a 观众，I want 加载失败时看到错误信息与「重试」按钮，so that 网络或数据问题可以自行恢复。
6. As a 观众，I want 点重试后重新加载片单，so that 不必重启应用。
7. As a 观众，I want 海报加载失败时看到按影片生成的渐变占位图并显示片名，so that 弱网下网格仍然可读。
8. As a 观众，I want 点击任意影片进入详情页，so that 我能进一步了解它。
9. As a 观众，I want 详情页展示大图海报、片名、年份、时长与类型，so that 我能快速判断是否想看。
10. As a 观众，I want 详情页展示影片简介，so that 我能了解剧情梗概。
11. As a 观众，I want 从详情页点「返回」回到首页，so that 浏览动线不中断。
12. As a 观众，I want 详情页有明显的「播放」按钮，so that 我能直接开始观看。
13. As a 观众，I want 播放页支持 HLS 与 MP4 片源，so that 不同来源的影片都能播放。
14. As a 观众，I want 播放器提供播放 / 暂停、进度拖动等基础控制，so that 我能按自己的节奏观看。
15. As a 观众，I want 播放页进入沉浸式全屏、隐藏系统栏，so that 观看时画面不受打扰。
16. As a 观众，I want 全屏时可通过滑动临时呼出系统栏，so that 我仍能返回或使用系统操作。
17. As a 观众，I want 播放期间屏幕保持常亮，so that 观看时不会因自动锁屏中断。
18. As a 观众，I want 应用进入后台时自动暂停播放，so that 不会在切走后继续消耗流量与电量。
19. As a 观众，I want 播放页左上角随时可以返回，so that 我能快速退出播放。
20. As a 观众，I want 视频起播或缓冲时看到缓冲指示，so that 我知道正在加载而不是卡住。
21. As a 观众，I want 播放失败时看到错误信息与返回入口，so that 我知道发生了什么并能退出。
22. As a 观众，I want 影片详情加载失败时看到错误与重试，so that 我能重新加载而不是面对空白页。
23. As a 观众，I want 请求的影片不存在时看到明确的「影片不存在」提示，so that 我理解是数据问题而非应用崩溃。
24. As a 观众，I want 启动时片单文件解析失败会看到「片单加载失败」页面，so that 我明白是数据源问题。
25. As a 观众，I want 旋转屏幕或改变窗口大小后页面状态保持，so that 浏览不被打断。
26. As a 观众，I want 应用在深色模式下使用深色配色、浅色模式下使用浅色配色，so that 视觉与系统设置一致。
27. As a 观众，I want 片单底部与详情页看到片源版权说明，so that 我清楚内容来源。
28. As a 观众，I want 无需登录即可播放全部影片，so that 体验没有任何账号门槛。
29. As a 观众，I want 应用在 Android 7.0 及以上设备运行，so that 覆盖较广的设备范围。

## Implementation Decisions

- **平台与构建**：单模块 Android 应用（`applicationId` 为 `com.dylanxy.kiwivideo`），Kotlin 2.2、Gradle Kotlin DSL、版本目录统一管理依赖。`minSdk = 24`、`targetSdk = 36`、`compileSdk = 37.1`（由 core-ktx 1.19 的硬性要求决定）。Java 兼容级别 11。
- **UI 框架**：Jetpack Compose + Material 3，单 Activity，`enableEdgeToEdge`，Compose BOM 统一版本。主题为自定义浅 / 深色方案（猕猴桃绿主色），`dynamicColor` 默认关闭；主题定义在 Compose 中，XML 主题仅作为启动窗口样式。
- **架构**：MVVM + `StateFlow` + repository 接口。无可用的 DI 框架，ViewModel 通过伴生 `factory` 接收依赖，在导航宿主中构造。
- **数据契约**：`Movie` 为不可变数据类，字段包括 id、片名、年份、时长（分钟）、类型列表、简介、海报地址、视频地址，使用 `kotlinx.serialization` 序列化。
- **数据源**：内置资源文件 `assets/movies.json` 提供 8 部影片；`MovieCatalogJson` 负责解码，配置 `ignoreUnknownKeys = true`，因此片单新增字段不会破坏旧版本。`LocalMovieRepository` 实现 `MovieRepository` 接口（`getMovies` / `getMovie(id)`），在内存列表中查找。
- **启动流程**：应用启动时读取并解析片单，成功则构造 repository 并进入导航；解析或读取失败则渲染独立的「片单加载失败」页面并显示错误信息，不进入主界面。
- **导航**：Navigation Compose 的 type-safe 路由，三个目标：首页、详情（携带 movieId）、播放（携带 movieId）。详情页与播放页都复用 `DetailViewModel`，但使用不同的 ViewModel key 以获得独立实例。
- **状态建模**：首页与详情各自定义 `Loading` / `Success` / `Error` 密封接口状态；ViewModel 在初始化时加载，并把 `CancellationException` 原样抛出、其他异常统一转为 `Error`。
- **首页表现**：`LazyVerticalGrid` 使用 `GridCells.Adaptive(minSize = 140.dp)` 自适应列数，海报按 2:3 展示，卡片按影片 id 作为 key；底部有一行片源版权说明。
- **详情页表现**：海报按 16:9 展示，标题、元信息（年份 · 时长 · 类型）与简介纵向排列，可滚动；「播放」按钮占满宽度。
- **海报加载**：使用 Coil 3 的 `AsyncImage`，网络层由 OkHttp 提供；海报区域先绘制一个由影片 id 哈希决定的渐变背景（5 套调色板之一）并居中叠加片名，图片加载成功后覆盖在其上，因此加载中与加载失败都有可用的占位视觉。
- **播放器**：Media3 ExoPlayer + `PlayerView`（启用控制器与缓冲指示），通过 `MediaItem.fromUri` 支持 HLS 与 MP4。播放器在进入播放页时创建并自动准备、自动播放，离开时释放。生命周期观察者在 `ON_STOP` 时暂停。播放器监听错误事件，出错时在视频上方覆盖错误信息与返回按钮。进入播放页时隐藏系统栏并设置常亮标志，离开时恢复。
- **错误与空态组件**：首页与详情共用 `LoadingState` / `ErrorState`；播放页因黑底场景使用独立的白色文字错误层。详情页数据缺失时使用固定文案「影片不存在」。
- **权限与清单**：仅声明 `INTERNET` 权限；Activity 声明处理常见配置变化（方向、屏幕尺寸等），软键盘模式为 `adjustResize`。
- **依赖清单**：Compose（Material 3、UI、Tooling）、Activity Compose、Lifecycle（runtime / compose / viewmodel-compose）、Navigation Compose、Media3（exoplayer、exoplayer-hls、ui）、Coil 3（compose、okhttp 网络）、kotlinx.serialization。测试依赖为 JUnit4 与 kotlinx-coroutines-test。
- **资源与配置**：片名、视频地址、海报地址等均来自片单；应用名称通过字符串资源提供。release 构建未启用优化。

## Testing Decisions

- **好测试的标准**：只验证外部可观察行为——repository 对外返回的数据、ViewModel 对外暴露的 `uiState`、JSON 解码的输入输出；不断言私有实现细节、不依赖真实网络与真实播放器。
- **被测模块**：`MovieCatalogJson`、`LocalMovieRepository`、`HomeViewModel`、`DetailViewModel`。
- **现有测试**：共 13 个 JVM 单测，全部通过 `./gradlew testDebugUnitTest` 运行：
  - JSON 解码：正常解码、忽略未知字段、畸形 JSON 抛异常；
  - repository：返回全部影片、按 id 命中、未知 id 返回 null；
  - 首页 ViewModel：成功状态、失败状态、失败后重试；
  - 详情 ViewModel：命中影片、影片不存在、repository 失败；
  - 以及一个占位的示例单测。
- **先例与基础设施**：`MainDispatcherRule` 用 `StandardTestDispatcher` 替换主调度器；`FakeMovieRepository` 支持注入数据或失败；`sampleMovie` 提供可覆盖 id 与片名的样例；ViewModel 测试使用 `runTest` + `advanceUntilIdle` 断言状态迁移。
- **未覆盖**：Compose UI、导航、播放器与沉浸式行为没有自动化测试；Instrumentation 侧仅有 Android Studio 生成的环境自检测试。后续新增测试应优先复用上述 dispatcher 规则与 fake repository，避免引入新的测试接缝。

## Out of Scope

当前实现明确**不包含**以下能力（README 中列为 V2 候选）：

- 搜索、分类筛选、排序；
- 登录 / 账号体系；
- 收藏、观看历史、续播、下载 / 离线缓存；
- 播放器倍速、字幕、手势调节、清晰度切换、画中画；
- 后端服务与远程片单接口（UI 与 ViewModel 只依赖 `MovieRepository`，替换实现即可接入，但尚未实现）；
- 推荐、评分、评论、分享；
- 多语言 / 本地化资源；
- 深链接、桌面小组件、通知；
- Compose UI 测试、播放器集成测试与端到端测试。

## Further Notes

- **构建与验证**：`./gradlew testDebugUnitTest` 跑单测，`./gradlew assembleDebug` 生成 APK，`./gradlew lintDebug` 做静态检查。APK 输出在 `app/build/outputs/apk/debug/app-debug.apk`。终端若找不到 Java，可指向 Android Studio 内置 JDK（`JAVA_HOME` 设为 `/Applications/Android Studio.app/Contents/jbr/Contents/Home`）。
- **替换片单**：编辑 `assets/movies.json` 即可增删影片，字段与 `Movie` 数据类一一对应；`ignoreUnknownKeys` 允许逐步加字段。
- **片源与版权**：演示片源为 Blender 基金会开源电影（CC BY）及公开测试流，正式发布前必须替换为自有版权或已授权片源；海报使用 placehold.co 占位图。
- **接入远程数据**：界面与 ViewModel 已面向 `MovieRepository` 编程，新增远程实现并在启动处替换本地实现即可，页面与现有测试无需改动。
- **已知技术约束**：`compileSdk = 37.1` 是为了满足 core-ktx 1.19 的要求；`targetSdk = 36`。播放器依赖 `@UnstableApi` 的 Media3 API 区域。
- **相关计划**：首页搜索与类型筛选已单独立项，见 `.scratch/search-and-filter/spec.md`，不属于本基线范围。
