package com.moneytrack.startup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import com.moneytrack.navigation.AppDestination

class AppEntryViewModelTest {
    @Test
    fun init_setsLoadingFalseAndOnboardingStartDestination() {
        val viewModel = AppEntryViewModel()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(AppDestination.Onboarding.route, state.startDestination)
    }
}
