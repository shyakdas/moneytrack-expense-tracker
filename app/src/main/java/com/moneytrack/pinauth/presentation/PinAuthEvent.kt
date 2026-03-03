package com.moneytrack.pinauth.presentation

sealed interface PinAuthEvent {
    data object Authenticated : PinAuthEvent
    data object RequestBiometricPrompt : PinAuthEvent
}
