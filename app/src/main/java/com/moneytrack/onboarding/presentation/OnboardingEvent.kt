package com.moneytrack.onboarding.presentation

sealed interface OnboardingEvent {
    data object Completed : OnboardingEvent
}
