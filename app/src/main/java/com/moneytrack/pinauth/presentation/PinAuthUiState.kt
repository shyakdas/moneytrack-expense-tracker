package com.moneytrack.pinauth.presentation

data class PinAuthUiState(
    val isLoading: Boolean = true,
    val mode: PinAuthMode = PinAuthMode.PIN,
    val enteredPin: String = "",
    val showPinError: Boolean = false,
    val showBiometricError: Boolean = false,
)
