package com.moneytrack.pinauth.presentation

sealed interface PinAuthAction {
    data class EnterDigit(val digit: Int) : PinAuthAction
    data object DeleteDigit : PinAuthAction
    data object SubmitPin : PinAuthAction
    data object RequestBiometricAuth : PinAuthAction
    data class BiometricAuthResult(val success: Boolean) : PinAuthAction
}
