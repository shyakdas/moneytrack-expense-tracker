// Copyright (c) 2026 shyakdas

package com.moneytrack.startup

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun startDestination_updatesToOnboarding_whenLocalFlagsAreCleared() = runTest {
        val onboardingFlow = MutableStateFlow(true)
        val pinStatusFlow = MutableStateFlow(PinSetupStatus.PIN_ENABLED)
        val viewModel = createViewModel(
            onboardingCompletedFlow = onboardingFlow,
            pinSetupStatusFlow = pinStatusFlow,
        )
        advanceUntilIdle()

        assertEquals(AppDestination.PinAuth.route, viewModel.uiState.value.startDestination)

        onboardingFlow.value = false
        pinStatusFlow.value = PinSetupStatus.NOT_STARTED
        advanceUntilIdle()

        assertEquals(AppDestination.Onboarding.route, viewModel.uiState.value.startDestination)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun initialState_staysLoading_untilStartupSourcesEmit() = runTest {
        val onboardingFlow = MutableSharedFlow<Boolean>(replay = 0)
        val pinStatusFlow = MutableSharedFlow<PinSetupStatus>(replay = 0)
        val viewModel = createViewModel(
            onboardingCompletedFlow = onboardingFlow,
            pinSetupStatusFlow = pinStatusFlow,
        )

        assertTrue(viewModel.uiState.value.isLoading)

        onboardingFlow.emit(true)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isLoading)

        pinStatusFlow.emit(PinSetupStatus.SKIPPED)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(AppDestination.Home.route, viewModel.uiState.value.startDestination)
    }

    private fun createViewModel(
        onboardingCompleted: Boolean = false,
        pinSetupStatus: PinSetupStatus = PinSetupStatus.NOT_STARTED,
        onboardingCompletedFlow: Flow<Boolean> = MutableStateFlow(onboardingCompleted),
        pinSetupStatusFlow: Flow<PinSetupStatus> = MutableStateFlow(pinSetupStatus),
    ): AppEntryViewModel {
        val onboardingUseCase =
            GetOnboardingCompletedUseCase(
                object : OnboardingRepository {
                    override fun observeCompletion(): Flow<Boolean> = onboardingCompletedFlow
                    override suspend fun setCompleted(completed: Boolean) = Unit
                },
            )
        val pinUseCase =
            GetPinSetupStatusUseCase(
                object : SecurityRepository {
                    override fun observePinSetupStatus(): Flow<PinSetupStatus> = pinSetupStatusFlow
                    override suspend fun setPinSetupStatus(status: PinSetupStatus) = Unit
                    override suspend fun savePinHash(pinHash: String) = Unit
                    override suspend fun clearPinHash() = Unit
                    override suspend fun getPinHash(): String? = null
                },
            )
        return AppEntryViewModel(
            getOnboardingCompletedUseCase = onboardingUseCase,
            getPinSetupStatusUseCase = pinUseCase,
        )
    }
}
