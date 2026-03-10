// Copyright (c) 2026 shyakdas

package com.moneytrack.pinsetup.presentation

sealed interface PinSetupAction {
    data object SelectPin : PinSetupAction
    data object SelectBiometric : PinSetupAction
    data object Skip : PinSetupAction
    data class EnterDigit(val digit: Int) : PinSetupAction
    data object DeleteDigit : PinSetupAction
    data object SubmitPin : PinSetupAction
    data object ForgotPin : PinSetupAction
    data class RecoveryVerificationResult(val success: Boolean) : PinSetupAction
}
