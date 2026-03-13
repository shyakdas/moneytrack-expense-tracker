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
        assertEquals("transaction", AppDestination.Transaction.route)
        assertEquals("profile", AppDestination.Profile.route)
        assertEquals("settings", AppDestination.Settings.route)
        assertEquals("currency", AppDestination.Currency.route)
        assertEquals("theme", AppDestination.Theme.route)
        assertEquals("security", AppDestination.Security.route)
        assertEquals("security_pin_setup", AppDestination.SecurityPinSetup.route)
        assertEquals("expense", AppDestination.Expense.route)
    }
}
