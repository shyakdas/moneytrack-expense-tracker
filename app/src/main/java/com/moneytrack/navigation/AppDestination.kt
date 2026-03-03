package com.moneytrack.navigation

sealed class AppDestination(val route: String) {
    data object Onboarding : AppDestination("onboarding")
    data object Auth : AppDestination("auth")
}
