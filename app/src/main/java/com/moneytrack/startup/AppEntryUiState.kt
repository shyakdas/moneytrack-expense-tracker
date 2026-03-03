package com.moneytrack.startup

import com.moneytrack.navigation.AppDestination

data class AppEntryUiState(
    val isLoading: Boolean = true,
    val startDestination: String = AppDestination.Onboarding.route,
)
