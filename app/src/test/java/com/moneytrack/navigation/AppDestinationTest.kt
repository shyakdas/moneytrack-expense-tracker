// Copyright (c) 2026 shyakdas

package com.moneytrack.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppDestinationTest {
    @Test
    fun routes_areCorrect() {
        assertEquals("onboarding", AppDestination.Onboarding.route)
        assertEquals("pin_setup", AppDestination.PinSetup.route)
        assertEquals("pin_auth", AppDestination.PinAuth.route)
        assertEquals("home", AppDestination.Home.route)
        assertEquals("expense", AppDestination.Expense.route)
    }
}
