package com.moneytrack.pinsetup.presentation

data class PinSetupUiState(
    val stage: PinSetupStage = PinSetupStage.INTRO,
    val enteredPin: String = "",
    val firstPin: String = "",
    val showPinMismatch: Boolean = false,
    val failedAttempts: Int = 0,
    val isLockedOut: Boolean = false,
    val showRecoveryError: Boolean = false,
)
