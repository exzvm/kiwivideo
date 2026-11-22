# Issue tracker: Local Markdown（本地 Markdown）

本仓库的 issue 和 spec 以 markdown 文件形式存放在 `.scratch/` 下。

## 约定

- 每个 feature 一个目录：`.scratch/<feature-slug>/`
- spec 文件为 `.scratch/<feature-slug>/spec.md`
- 实现类 issue 一个 ticket 一个文件：`.scratch/<feature-slug>/issues/<NN>-<slug>.md`，从 `01` 开始编号，不要写成单个合并的 tickets 文件
- 每个 issue 文件顶部靠近开头处用一行 `Status:` 记录状态
- 评论与对话历史追加到文件末尾的 `## Comments` 标题下

## 当某个 skill 要求「发布到 issue tracker」时

在 `.scratch/<feature-slug>/` 下新建文件（必要时创建目录）。

## 当某个 skill 要求「获取相关 ticket」时

读取对应路径的文件。用户通常会直接给出路径或 issue 编号。

## Wayfinding 操作

供 `/wayfinder` 使用。**map** 是一个文件，每个 ticket 对应一个 **child** 文件。

- **Map**：`.scratch/<effort>/map.md`（Notes / Decisions-so-far / Fog 正文）。
- **Child ticket**：`.scratch/<effort>/issues/NN-<slug>.md`，从 `01` 开始编号，body 中写问题。`Type:` 行记录 ticket 类型（`research`/`prototype`/`grilling`/`task`）；`Status:` 行记录 `claimed`/`resolved`。
- **Blocking**：顶部附近一行 `Blocked by: NN, NN`。当它列出的每个文件均为 `resolved` 时，该 ticket 即解除阻塞。
- **Frontier**：扫描 `.scratch/<effort>/issues/`，找出 open、未阻塞、未认领的文件；编号最小者优先。
- **Claim**：动工前设置 `Status: claimed` 并保存。
- **Resolve**：在 `## Answer` 标题下追加答案，设置 `Status: resolved`，然后把上下文指针（要点 + 链接）追加到 `map.md` 的 Decisions-so-far。
