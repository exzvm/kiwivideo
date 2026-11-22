# AGENTS.md

## 项目概览

- **语言**：Kotlin
- **平台 / IDE**：Android（Android Studio）
- **构建系统**：Gradle（Kotlin DSL）

## 核心原则

1. **MVP 优先（最小可行性准则）**：
   - 严格聚焦最小可用产品，先构建最简单、最干净的可运行版本。
   - 避免过度设计、过早抽象，或添加未被要求的功能。
2. **测试优先（写代码前先自测）**：
   - 遵循严格的 TDD（测试驱动开发），在编写实现代码之前先写会失败的单元测试。
   - 只有当所有已有测试与新写的测试都通过时，任务才算完成。

## 常用命令

- **构建 Debug APK**：`./gradlew assembleDebug`
- **运行单元测试**：`./gradlew testDebugUnitTest`
- **Lint 与格式检查**：`./gradlew lintDebug`（若已配置，也可用 `./gradlew ktlintCheck`）

## 代码风格与架构

- 遵循官方 [Kotlin 编码规范](https://kotlinlang.org)。
- 保持函数短小、职责单一、易于单元测试。
- 优先使用依赖注入 / 构造函数参数，而不是硬编码单例，以便组件在测试中可被 mock。

## 边界（操作边界）

- **始终要做**：
  - 在实现业务逻辑之前先写单元测试（JUnit / MockK）。
  - 任何代码修改后运行 `./gradlew testDebugUnitTest`，确认没有破坏已有功能。
- **先询问**：
  - 在 `build.gradle.kts` 中添加新的第三方依赖。
  - 重构既有架构或修改 Gradle 配置。
- **绝不**：
  - 为了让构建通过而删除或注释掉失败的测试。
  - 硬编码 API key、密钥或其他敏感凭据。
  - 在核心 MVP 流程可用之前，构建复杂的 UI 动画或边缘情况处理。

## Git 提交规范

提交信息对齐 [Conventional Commits](https://www.conventionalcommits.org/)，采用「三块结构 + 标题三字段」：

```
[type](<scope>): <subject>

[可选正文 Body]

[可选页脚 Footer(s)]
```

- **三大块**：标题 Header（必填）、正文 Body（可选）、页脚 Footer（可选），块与块之间空一行。
- **标题三字段**：`type`（必填）、`scope`（可选）、`subject`（必填）。没有 scope 时写作 `<type>: <subject>`。
- **关于「四部分」**：那只是把标题里的 type / scope / subject 与正文、页脚分开数的流行叫法，规范本身仍是上述三块；`commitlint`、`semantic-release` 等工具解析的也是三块结构。
- **type（必填，仅限以下五种）**：
  - `feat`：新增功能
  - `fix`：修复缺陷
  - `docs`：文档变更
  - `test`：测试相关
  - `chore`：构建、依赖、配置、脚本、重构、格式化等维护性变更
- **scope（可选）**：说明影响范围，常用值：`home`、`detail`、`player`、`data`、`docs`、`build`。
- **subject**：用中文简述改动，采用祈使语气，不超过 50 个字符，结尾不加句号。
- **body（可选）**：说明「为什么」以及必要的影响；每行建议不超过 72 个字符。
- **页脚（可选）**：
  - 破坏性变更：在 type/scope 后加 `!`，并在页脚写 `BREAKING CHANGE: <说明>`。
  - 关联 issue 或本地 ticket：使用 `Refs: #123` 或 `Refs: <ticket 编号/路径>`；完成后用 `Closes: ...`。
- **Agent 提交**：保留署名页脚 `Co-Authored-By: Claude Code <noreply@anthropic.com>`；人类提交不强制。
- 一个提交只做一件逻辑上的事，避免混合无关改动。

示例：

```
feat(home): 增加片名搜索
fix(player): 修复切后台后未暂停
docs: 补充片单字段说明
test(data): 覆盖未知影片 id
chore: 升级 Compose BOM
```

## Agent skills

### Issue tracker

Issue 和 spec 以 markdown 文件形式存放在 `.scratch/<feature>/` 下。详见 `docs/agents/issue-tracker.md`。

### Domain docs

单上下文（single-context）：仓库根目录一份 `GLOSSARY.md` 与 `docs/adr/`。详见 `docs/agents/domain.md`。
