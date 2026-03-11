// Copyright (c) 2026 shyakdas

package com.moneytrack.navigation

sealed class AppDestination(val route: String) {
    data object Onboarding : AppDestination("onboarding")
    data object PinSetup : AppDestination("pin_setup")
    data object PinAuth : AppDestination("pin_auth")
    data object Home : AppDestination("home")
    data object Expense : AppDestination("expense")
}
