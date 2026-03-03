package com.moneytrack.pinsetup.presentation

sealed interface PinSetupEvent {
    data object Completed : PinSetupEvent
}
