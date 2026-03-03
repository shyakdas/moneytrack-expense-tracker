package com.moneytrack.startup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import com.moneytrack.navigation.AppDestination

class AppEntryUiStateTest {
    @Test
    fun defaultValues_areCorrect() {
        val state = AppEntryUiState()

        assertTrue(state.isLoading)
        assertEquals(AppDestination.Onboarding.route, state.startDestination)
    }
}
