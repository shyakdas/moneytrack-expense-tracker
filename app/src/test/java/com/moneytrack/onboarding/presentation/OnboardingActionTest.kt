// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding.presentation

import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingActionTest {
    @Test
    fun finishedAction_isSingletonObject() {
        assertTrue(OnboardingAction.OnFinishedClick is OnboardingAction)
    }
}
