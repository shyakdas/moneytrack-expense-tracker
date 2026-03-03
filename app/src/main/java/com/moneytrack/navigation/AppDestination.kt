package com.moneytrack.navigation

sealed class AppDestination(val route: String) {
    data object Onboarding : AppDestination("onboarding")
    data object PinSetup : AppDestination("pin_setup")
    data object Home : AppDestination("home")
}
