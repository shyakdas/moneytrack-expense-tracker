package com.moneytrack.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppDestinationTest {
    @Test
    fun onboardingRoute_isCorrect() {
        assertEquals("onboarding", AppDestination.Onboarding.route)
    }
}
