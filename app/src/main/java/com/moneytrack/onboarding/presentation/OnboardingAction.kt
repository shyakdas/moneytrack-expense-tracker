// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding.presentation

sealed interface OnboardingAction {
    data object OnFinishedClick : OnboardingAction
}
