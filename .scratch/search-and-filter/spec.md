Status: ready-for-agent

# Spec：首页搜索与类型筛选

## Problem Statement

用户打开 Kiwi Video 的首页，只能在一屏海报网格里逐行浏览。当前片单虽只有 8 部，但没有任何按片名查找或按类型收窄的手段：想找一部具体影片、或只想看「科幻」「喜剧」类的影片时，只能肉眼扫完全部海报，片单增长后会更难。

## Solution

在首页顶部提供两个并排的浏览入口：

- 一个搜索框，按片名即时过滤网格；
- 一行类型筛选 chip（含「全部」），点选后只显示该类型的影片。

搜索词与类型可以组合使用。没有匹配结果时，网格区域显示明确的空状态，并提供一键「清除筛选」。整个过滤在已加载的片单上即时完成，不发起额外加载，也不影响原有的加载中 / 加载失败 / 重试行为。

## User Stories

1. As a 观众，I want 按片名搜索，so that 我能快速定位想看的那部影片。
2. As a 观众，I want 输入部分片名就能匹配，so that 我不必准确记住完整标题。
3. As a 观众，I want 搜索时忽略英文大小写，so that 我小写输入「sintel」也能找到「Sintel」。
4. As a 观众，I want 输入为空或只有空格时显示全部影片，so that 清空搜索框后能立刻回到完整片单。
5. As a 观众，I want 一键清空搜索词，so that 不必手动删除字符。
6. As a 观众，I want 看到类型筛选 chip，so that 我知道有哪些类型可筛。
7. As a 观众，I want 类型 chip 来自实际片单，so that 不会出现筛不出任何影片的「空类型」。
8. As a 观众，I want 点「全部」恢复不按类型筛选，so that 能快速退出类型筛选。
9. As a 观众，I want 点某个类型后只看到该类型的影片，so that 能按兴趣收窄选择。
10. As a 观众，I want 选中的类型 chip 有明显选中态，so that 我知道当前筛选条件。
11. As a 观众，I want 搜索词与类型组合生效（同时满足），so that 我能进一步缩小结果。
12. As a 观众，I want 组合筛选下无结果时看到「没有匹配的影片」提示，so that 不会误以为列表加载失败。
13. As a 观众，I want 无结果时能一键清除所有筛选，so that 能立刻回到完整片单。
14. As a 观众，I want 筛选结果仍可点进详情，so that 找到影片后能直接查看和播放。
15. As a 观众，I want 从详情页返回首页时保留我之前的搜索词和类型，so that 不用重新筛选。
16. As a 观众，I want 屏幕旋转等配置变化后筛选条件不丢失，so that 浏览不被打断。
17. As a 观众，I want 加载失败后点重试时保留已选的筛选条件，so that 重新加载后仍是同一视图。
18. As a 观众，I want 搜索和筛选即时生效、无额外转圈，so that 浏览体验流畅。
19. As a 观众，I want 片单为空或全部被筛掉时看到清楚的空状态，so that 我理解当前没有可显示的影片。
20. As a 观众，I want 搜索框有「搜索影片」之类的提示文字，so that 我一眼知道它是做什么的。
21. As an accessibility 用户，I want 筛选 chip 和搜索框可被读屏识别，so that 我也能用搜索与筛选。
22. As an 观众，I want 类型 chip 信息横向可滚动而不挤坏布局，so that 类型较多时首页仍整洁。
23. As an 观众，I want 片单底部原有的片源说明在新筛选下仍合理显示，so that 版权提示不丢失。

## Implementation Decisions

- **过滤位置**：全部搜索 / 筛选逻辑放在 `HomeViewModel`，对已加载的完整片单做客户端过滤。不改 `MovieRepository` 接口，不新增依赖。
- **单一状态出口**：`HomeUiState` 仍是唯一状态源；`HomeUiState.Success` 扩展为同时携带过滤后的影片列表、当前搜索词、当前选中类型（`null` 表示「全部」）以及可选类型列表。`Loading` / `Error` 语义不变。
- **View 事件**：暴露 `onQueryChange(String)`、`onGenreSelected(String?)`、`clearFilters()`。筛选状态由 ViewModel 持有，Compose 只做无状态渲染与回调上抛。
- **片名匹配规则**：对搜索词做 `trim`；空串视为不过滤。否则 `title.contains(query, ignoreCase = true)`。
- **类型匹配规则**：选中类型非空时，要求 `movie.genres` 包含该类型。搜索词与类型为「与」关系，同时生效才显示。
- **可选类型来源**：从完整片单所有影片的 `genres` 取并集、去重并稳定排序；基于未过滤的片单计算，避免选中某类型后其它 chip 消失。「全部」由 UI 作为额外选项呈现，不进入类型集合。
- **无结果**：`Success` 中列表为空即无匹配；由 UI 渲染空状态文案与「清除筛选」操作，不新增 `Empty` 状态类型。
- **重试语义**：`loadMovies()` 重新拉取片单后，按当前搜索词与类型重新应用过滤，不重置筛选条件。
- **不引入防抖**：片单在本地，列表规模小，输入即时过滤即可。
- **UI 结构**：搜索框与类型 chip 行放在首页顶部区域（随内容布局），chip 行横向可滚动；海报网格保持现有自适应列与按 id 的 key。空状态放在网格区域。
- **状态保留**：筛选状态由 `HomeViewModel` 持有，从详情页返回或配置变化时自然保留。
- **不测 Compose 内部**：不在本 spec 引入 Compose UI 测试。

## Testing Decisions

- **好测试的标准**：只验证外部可观察行为——通过 ViewModel 的公开事件驱动，断言 `uiState` 的输出；不触碰私有实现、不校验内部数据结构的组织方式。
- **被测模块**：`HomeViewModel`（唯一 seam）。
- **测试文件**：扩展 `HomeViewModelTest`，沿用 `FakeMovieRepository` 与 `MainDispatcherRule`。
- **需覆盖的行为**：
  1. 非空搜索词按片名子串、忽略大小写过滤；
  2. 空白搜索词视为不过滤，返回全部；
  3. 选择某类型后只返回含该类型的影片；
  4. 搜索词 + 类型组合时取交集；
  5. 可选类型列表来自完整片单、去重且稳定；
  6. 无匹配时 `Success` 的影片列表为空；
  7. `clearFilters()` 同时重置搜索词与类型并返回全部；
  8. 加载失败 `Error` 后重试，筛选条件保留且重新应用；
  9. 原有「加载成功 / 失败 / 重试」用例在新状态下仍通过。
- **测试前置**：`sampleMovie` 辅助函数目前把 `genres` 写死，需要让测试能构造不同 `genres` 与标题的样例，以便断言过滤结果。
- **先例**：`HomeViewModelTest` 现有的 `runTest` + `advanceUntilIdle` + `assertEquals` 模式；`DetailViewModelTest` 的同类写法可参考。

## Out of Scope

- 服务端 / 远程 API 搜索（当前片单是本地静态 JSON）。
- 多类型同时筛选、类型取反或排除。
- 按年份、时长、简介搜索，排序，模糊匹配，拼音 / 首字母搜索，相关性排序。
- 搜索建议、最近搜索、搜索历史。
- 筛选条件的持久化（跨进程重启保留）。
- 详情页、播放页内的搜索或筛选入口。
- 自定义动画、转场等超出 Material 默认表现的效果。
- Compose UI 测试与 Instrumentation 测试。

## Further Notes

- 类型值是 `movies.json` 中的中文字符串（如「动画」「科幻」「喜剧」），筛选与展示直接沿用这些原文。
- 片名当前均为英文（如 Sintel、Big Buck Bunny），规则不区分语言，中文片名同样适用。
- README 中「接入远程 API」的路径仍成立：将来若接入远程搜索，只需替换仓库实现，本 spec 的 ViewModel 过滤逻辑可作为本地兜底或随之调整，属于后续工作。
- 遵循项目 MVP 原则：只实现搜索与单类型筛选，不提前抽象 SearchFilter 组件或 Repository 级查询接口。
