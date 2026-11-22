# AGENTS.md

## Project Overview
- **Language**: Kotlin
- **Platform / IDE**: Android (Android Studio)
- **Build System**: Gradle (Kotlin DSL)

## Core Principles
1. **MVP First (最小可行性准则)**:
   - Focus strictly on the Minimum Viable Product. Build the simplest, cleanest working version first.
   - Avoid over-engineering, premature abstraction, or adding unrequested features.
2. **Test-First Workflow (写代码前先自测)**:
   - Follow strict TDD (Test-Driven Development). Write failing unit tests *before* writing implementation code.
   - Never consider a task complete until all existing and newly written tests pass.

## Commands
- **Build Debug APK**: `./gradlew assembleDebug`
- **Run Unit Tests**: `./gradlew testDebugUnitTest`
- **Lint & Format Check**: `./gradlew lintDebug` (or `./gradlew ktlintCheck` if configured)

## Code Style & Architecture
- Follow official [Kotlin Coding Conventions](https://kotlinlang.org).
- Keep functions small, single-purpose, and easy to unit test.
- Prefer dependency injection / constructor parameters over hardcoded singletons so components remain mockable for tests.

## Boundaries (操作边界)
- **Always do**:
  - Write unit tests (JUnit / MockK) before implementing business logic.
  - Run `./gradlew testDebugUnitTest` after any code modification to verify nothing is broken.
- **Ask first**:
  - Adding new third-party dependencies to `build.gradle.kts`.
  - Refactoring existing architecture or modifying Gradle configurations.
- **Never do**:
  - Delete or comment out failing tests to make the build pass.
  - Hardcode API keys, secrets, or sensitive credentials.
  - Build complex UI animations or edge-case handlers before the core MVP flow works.

## Agent skills

### Issue tracker

Issue 和 spec 以 markdown 文件形式存放在 `.scratch/<feature>/` 下。详见 `docs/agents/issue-tracker.md`。

### Domain docs

单上下文（single-context）：仓库根目录一份 `GLOSSARY.md` 与 `docs/adr/`。详见 `docs/agents/domain.md`。
