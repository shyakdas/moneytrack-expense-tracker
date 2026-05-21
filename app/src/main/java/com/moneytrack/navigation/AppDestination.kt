// Copyright (c) 2026 shyakdas

package com.moneytrack.navigation

sealed class AppDestination(val route: String) {
    data object Onboarding : AppDestination("onboarding")
    data object PinSetup : AppDestination("pin_setup")
    data object PinAuth : AppDestination("pin_auth")
    data object Home : AppDestination("home")
    data object Transaction : AppDestination("transaction")
    data object Profile : AppDestination("profile")
    data object Settings : AppDestination("settings")
    data object Currency : AppDestination("currency")
    data object Theme : AppDestination("theme")
    data object Security : AppDestination("security")
    data object Notification : AppDestination("notification")
    data object About : AppDestination("about")
    data object ExportData : AppDestination("export_data")
    data object SecurityPinSetup : AppDestination("security_pin_setup")
    data object Expense : AppDestination("expense")
}
