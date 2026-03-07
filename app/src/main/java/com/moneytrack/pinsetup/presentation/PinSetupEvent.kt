// Copyright (c) 2026 shyakdas

package com.moneytrack.pinsetup.presentation

sealed interface PinSetupEvent {
    data object Completed : PinSetupEvent
    data object RequestRecoveryAuth : PinSetupEvent
}
