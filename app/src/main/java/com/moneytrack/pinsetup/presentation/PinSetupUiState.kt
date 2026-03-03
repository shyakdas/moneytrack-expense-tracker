package com.moneytrack.pinsetup.presentation

data class PinSetupUiState(
    val stage: PinSetupStage = PinSetupStage.INTRO,
    val enteredPin: String = "",
    val firstPin: String = "",
    val showPinMismatch: Boolean = false,
)
