<!-- Copyright (c) 2026 shyakdas -->

# Contributing to MoneyTrack

Thanks for taking the time to contribute.
MoneyTrack is being built as a privacy-first Android app with a strong focus on clean architecture, maintainable UI, and reliable test coverage.

This guide explains how to contribute in a way that fits the existing project structure and CI rules.

## Before You Start

Please read these first:
- [README.md](README.md)
- [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- [SECURITY.md](SECURITY.md)

If you plan to work on a larger feature, open an issue or start a discussion first so we can align on direction before code is written.

## Development Setup

1. Fork the repository
2. Clone your fork
3. Open the project in Android Studio
4. Use the `devDebug` variant for local development
5. Make sure the project builds before starting work

```bash
git clone https://github.com/<your-username>/moneytrack-expense-tracker.git
cd moneytrack-expense-tracker
./gradlew :app:assembleDevDebug
```

## Branch Naming

Create your branch from `main`.

Use clear, descriptive branch names with one of these prefixes:
- `feat/` for new features
- `fix/` for bug fixes
- `chore/` for maintenance work
- `docs/` for documentation updates
- `test/` for test-only changes
- `refactor/` for code cleanup without behavior changes

Examples:
- `feat/transaction-filters`
- `fix/pin-auth-navigation`
- `docs/readme-improvements`
- `test/home-dashboard-coverage`

## Commit Format

This project uses a Conventional Commits style.

Format:

```text
type(scope): short summary
```

Examples:
- `feat(expense): add recurring expense scheduling`
- `fix(home): correct monthly balance calculation`
- `docs(readme): update setup and testing guide`
- `test(transaction): add screenshot coverage for empty state`
- `chore(ci): tighten PR quality checks`

Recommended commit types:
- `feat`
- `fix`
- `refactor`
- `test`
- `docs`
- `chore`

Keep commit messages focused on what changed, not how hard it was to build.

## Pull Request Expectations

Before opening a PR, make sure your branch:
- builds successfully
- passes lint and static analysis
- includes tests for behavior changes
- updates snapshots for UI changes
- updates documentation when needed

PR titles should also follow the Conventional Commits style:

```text
feat(home): add dynamic spend frequency chart
```

## PR Checklist

Use this checklist before requesting review:

- I rebased or merged the latest `main`
- My branch name follows the repository convention
- My PR title follows the commit/PR format
- I kept changes scoped to a single purpose
- I preserved clean architecture boundaries
- I added or updated tests for changed behavior
- I updated screenshot baselines if UI changed
- I ran the required local checks
- I updated docs, strings, or screenshots if needed
- I verified there are no new warnings introduced by my change

## Code Style Rules

### General

- Use Kotlin and Compose best practices
- Keep code readable and explicit
- Prefer small, focused functions
- Avoid unused code, dead resources, and commented-out blocks
- Keep naming clear and domain-oriented

### Architecture

Respect the existing feature-first structure:
- `presentation`: Compose UI, routes, ViewModels, UI state
- `domain`: models and use cases
- `data`: repositories, Room, DataStore, integrations
- `DesignSystem`: shared UI components and tokens

Do not:
- place business logic directly inside composables
- access Room or persistence directly from UI
- mix unrelated feature code into the wrong package

### UI

- Reuse `DesignSystem` components where possible
- Follow the existing `AppTheme` instead of introducing ad hoc styling
- Keep layouts responsive across phone sizes
- Add preview functions for new Compose screens when practical
- Update screenshot tests when shared UI changes affect existing baselines

### Static Analysis

This repository is strict about quality checks.
New warnings should be treated as failures and fixed before merge.

Run:

```bash
./gradlew :app:ktlintCheck :app:detekt :app:lintDevDebug
```

## Testing Expectations

Behavior changes should include the right level of coverage.

### Unit Tests

Add unit tests for:
- ViewModel state changes
- use case behavior
- mapping logic
- date, currency, and formatting rules

Run:

```bash
./gradlew :app:testDevDebugUnitTest
```

### UI Tests

Add UI tests for:
- critical user flows
- important states
- visibility and interaction behavior

Compile check:

```bash
./gradlew :app:compileDevDebugAndroidTestKotlin
```

### Screenshot Tests

If UI visuals change, update or add Paparazzi coverage.

Record:

```bash
./gradlew :app:recordPaparazziDevDebug
```

Verify:

```bash
./gradlew :app:verifyPaparazziDevDebug
```

If the change affects `:DesignSystem`, also run:

```bash
./gradlew :DesignSystem:recordPaparazziDebug
./gradlew :DesignSystem:verifyPaparazziDebug
```

### CI Parity

The main PR workflow also runs:

```bash
./gradlew ktlintCheck detekt :app:lintDevDebug
./gradlew testDebugUnitTest jacocoTestReport
./gradlew verifyPaparazziDebug
```

You do not always need to run every command locally for a small change, but you should run the checks relevant to your update.

## Documentation Expectations

Please update documentation when your change affects:
- setup or build steps
- architecture decisions
- screenshots or visible app behavior
- contributor workflows

## Reporting Bugs

When reporting a bug, include:
- device or emulator details
- build variant
- steps to reproduce
- expected behavior
- actual behavior
- screenshots or logs if relevant

## Security

Do not open public issues for sensitive security problems.
Use [SECURITY.md](SECURITY.md) for responsible disclosure instructions.

## Questions and First Contributions

Good first contributions usually include:
- documentation improvements
- test coverage gaps
- UI polish fixes
- lint or warning cleanup
- small bug fixes with clear reproduction steps

If you are unsure where to start, open an issue and ask.

Thanks again for helping improve MoneyTrack.
