// Copyright (c) 2026 shyakdas

package com.moneytrack.onboarding.presentation

import org.junit.Assert.assertSame
import org.junit.Test

class OnboardingActionTest {
    @Test
    fun finishedAction_isSingletonObject() {
        assertSame(OnboardingAction.OnFinishedClick, OnboardingAction.OnFinishedClick)
    }
}
