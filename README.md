# MoneyTrack – Offline Expense Tracker 💸

[![PR Checks](https://github.com/shyakdas/moneytrack-expense-tracker/actions/workflows/pr-quality-checks.yml/badge.svg)](https://github.com/shyakdas/moneytrack-expense-tracker/actions)
[![Nightly Build](https://github.com/shyakdas/moneytrack-expense-tracker/actions/workflows/nightly.yml/badge.svg)](https://github.com/shyakdas/moneytrack-expense-tracker/actions)
[![Dependabot](https://img.shields.io/badge/dependabot-enabled-brightgreen)](https://github.com/shyakdas/moneytrack-expense-tracker/security/dependabot)

MoneyTrack is a **privacy-first, offline Android expense tracker** built for people who want complete control over their financial data.

No cloud. No ads. No tracking.
Just **secure, local-first money management**.

---

## 🧪 Testing & Quality

- Unit tests
- Snapshot tests (Paparazzi)
- ktlint & detekt
- Coverage via Jacoco

All checks run automatically on every Pull Request.

---

## ⚙️ CI Overview

### Pull Request CI
- Lint & static analysis
- Unit tests
- Snapshot tests
- Coverage generation

### Nightly CI
- Full test suite
- Snapshot validation
- Early failure detection

---

## 🛠️ Run CI Locally

Run the full CI pipeline locally:

```bash
make ci
