# Domain Docs（领域文档）

工程类 skill 在探索代码库时应如何消费本仓库的领域文档。

## 探索之前，先读这些

- 仓库根目录的 **`GLOSSARY.md`**，或
- 若存在根目录的 **`GLOSSARY-MAP.md`**：它指向每个上下文的 `GLOSSARY.md`。读取与当前主题相关的每一份。
- **`docs/adr/`**：阅读与你即将改动的区域相关的 ADR。多上下文仓库中，还要查看 `src/<context>/docs/adr/` 下的上下文级决策。

如果上述文件不存在，**静默继续**。不要提示它们缺失，也不要主动建议创建。等术语或决策真正被确认时，由 `/domain-modeling` skill（经由 `/grill-with-docs` 和 `/improve-codebase-architecture` 触达）按需创建。

## 文件结构

单上下文仓库（绝大多数仓库）：

```
/
├── GLOSSARY.md
├── docs/adr/
│   ├── 0001-event-sourced-orders.md
│   └── 0002-postgres-for-write-model.md
└── src/
```

多上下文仓库（根目录存在 `GLOSSARY-MAP.md`）：

```
/
├── GLOSSARY-MAP.md
├── docs/adr/                          ← 系统级决策
└── src/
    ├── ordering/
    │   ├── GLOSSARY.md
    │   └── docs/adr/                  ← 上下文级决策
    └── billing/
        ├── GLOSSARY.md
        └── docs/adr/
```

## 使用 glossary 的词汇

当你的产出中提及某个领域概念时（issue 标题、重构提案、假设、测试名），使用 `GLOSSARY.md` 中定义的术语。不要漂移到 glossary 明确避免的同义词。

如果需要的概念尚未出现在 glossary 中，这是一个信号：要么你在发明项目不使用的语言（重新考虑），要么存在真实缺口（记下来交给 `/domain-modeling`）。

## 标记 ADR 冲突

如果你的产出与某个既有 ADR 冲突，明确暴露出来，而不是悄悄覆盖：

> _与 ADR-0007（event-sourced orders）冲突，但值得重新开启，因为……_
