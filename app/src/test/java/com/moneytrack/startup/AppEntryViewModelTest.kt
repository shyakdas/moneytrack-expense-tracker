// Copyright (c) 2026 shyakdas

package com.moneytrack.startup

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import com.moneytrack.navigation.AppDestination
import com.moneytrack.onboarding.domain.repository.OnboardingRepository
import com.moneytrack.onboarding.domain.usecase.GetOnboardingCompletedUseCase
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import com.moneytrack.testutil.MainDispatcherRule

class AppEntryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun init_setsOnboardingDestination_whenOnboardingNotCompleted() = runTest {
        val viewModel = createViewModel(
            onboardingCompleted = false,
            pinSetupStatus = PinSetupStatus.NOT_STARTED,
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(AppDestination.Onboarding.route, state.startDestination)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun init_setsPinSetupDestination_whenOnboardingCompletedAndPinNotStarted() = runTest {
        val viewModel = createViewModel(
            onboardingCompleted = true,
            pinSetupStatus = PinSetupStatus.NOT_STARTED,
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(AppDestination.PinSetup.route, state.startDestination)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun init_setsPinAuthDestination_whenPinSetupAlreadyHandled() = runTest {
        val viewModel = createViewModel(
            onboardingCompleted = true,
            pinSetupStatus = PinSetupStatus.PIN_ENABLED,
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(AppDestination.PinAuth.route, state.startDestination)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun init_setsHomeDestination_whenPinSetupSkipped() = runTest {
        val viewModel = createViewModel(
            onboardingCompleted = true,
            pinSetupStatus = PinSetupStatus.SKIPPED,
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(AppDestination.Home.route, state.startDestination)
    }

    private fun createViewModel(
        onboardingCompleted: Boolean,
        pinSetupStatus: PinSetupStatus,
    ): AppEntryViewModel {
        val onboardingUseCase =
            GetOnboardingCompletedUseCase(
                object : OnboardingRepository {
                    override fun observeCompletion(): Flow<Boolean> = flowOf(onboardingCompleted)
                    override suspend fun setCompleted(completed: Boolean) = Unit
                },
            )
        val pinUseCase =
            GetPinSetupStatusUseCase(
                object : SecurityRepository {
                    override fun observePinSetupStatus(): Flow<PinSetupStatus> = flowOf(pinSetupStatus)
                    override suspend fun setPinSetupStatus(status: PinSetupStatus) = Unit
                    override suspend fun savePinHash(pinHash: String) = Unit
                    override suspend fun getPinHash(): String? = null
                },
            )
        return AppEntryViewModel(
            getOnboardingCompletedUseCase = onboardingUseCase,
            getPinSetupStatusUseCase = pinUseCase,
        )
    }
}
