# MoneyTrack – Offline Expense Tracker 💸

MoneyTrack is a **secure, offline-first expense tracker** that helps users manage their income, expenses, and budgets while keeping **all financial data stored locally on the device**.

No cloud. No ads. No tracking.  
Just simple, private money management.

---

## ✨ Why MoneyTrack?

Most finance apps store your data on servers you don’t control.  
MoneyTrack is built with a different philosophy:

> **Your money. Your device. Fully offline.**

---

## 🚀 Features

- 📱 **Offline-first** – works without internet
- 🔐 **Encrypted local storage** for financial data
- 💰 Track **income & expenses**
- 🗂️ Category-based transaction management
- 📊 Monthly & yearly spending insights
- 🔒 App lock (PIN / Biometrics)
- 🌓 Light & Dark mode
- 📤 Manual export (CSV / PDF) *(planned)*

---

## 🔐 Privacy & Security

Privacy is a core feature, not an afterthought.

- ✅ No account signup required
- ✅ No cloud sync by default
- ✅ No ads or trackers
- ✅ No third-party analytics SDKs
- ✅ All data stored **locally & encrypted**

MoneyTrack never sends your financial data anywhere.

---

## 🏗️ Tech Stack

> *(Update as the project evolves)*

- **Platform**: Android
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **Database**: SQLite (Encrypted)
- **Security**:
    - Android Keystore

---

## 📂 Project Structure

```text
MoneyTrack/
├── data/
│   ├── model/
│   ├── local/
│   └── repository/
├── ui/
│   ├── screens/
│   ├── components/
│   └── viewmodel/
├── utils/
├── security/
└── app/
