// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import com.moneytrack.security.domain.usecase.ClearAppLockUseCase
import com.moneytrack.security.domain.usecase.CompletePinSetupWithBiometricUseCase
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import com.moneytrack.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SecurityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun uiState_mapsPinEnabledToPinSelection() = runTest {
        val repository = FakeSecurityRepository(initialStatus = PinSetupStatus.PIN_ENABLED)
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        advanceUntilIdle()

        assertEquals(SecurityOption.PIN, viewModel.uiState.value.selectedOption)
        collectJob.cancel()
    }

    @Test
    fun biometricSelectionResult_success_enablesBiometricAndCompletes() = runTest {
        val repository = FakeSecurityRepository(initialStatus = PinSetupStatus.SKIPPED)
        val viewModel = createViewModel(repository)
        val events = mutableListOf<SecurityEvent>()
        val collectJob = launch { viewModel.events.collect(events::add) }

        viewModel.onBiometricSelectionResult(success = true)
        advanceUntilIdle()

        assertEquals(PinSetupStatus.BIOMETRIC_ENABLED, repository.currentStatus.value)
        assertEquals(SecurityEvent.Completed, events.last())
        collectJob.cancel()
    }

    @Test
    fun noneSelection_clearsLockAndCompletes() = runTest {
        val repository = FakeSecurityRepository(initialStatus = PinSetupStatus.PIN_ENABLED)
        val viewModel = createViewModel(repository)
        val events = mutableListOf<SecurityEvent>()
        val collectJob = launch { viewModel.events.collect(events::add) }

        viewModel.onOptionSelected(SecurityOption.NONE)
        advanceUntilIdle()

        assertEquals(PinSetupStatus.SKIPPED, repository.currentStatus.value)
        assertEquals(true, repository.pinCleared)
        assertEquals(SecurityEvent.Completed, events.last())
        collectJob.cancel()
    }

    @Test
    fun selectingPin_emitsOpenPinSetupEvent() = runTest {
        val repository = FakeSecurityRepository(initialStatus = PinSetupStatus.SKIPPED)
        val viewModel = createViewModel(repository)
        val events = mutableListOf<SecurityEvent>()
        val collectJob = launch { viewModel.events.collect(events::add) }

        viewModel.onOptionSelected(SecurityOption.PIN)
        advanceUntilIdle()

        assertEquals(SecurityEvent.OpenPinSetup, events.last())
        collectJob.cancel()
    }

    private fun createViewModel(
        repository: FakeSecurityRepository,
    ): SecurityViewModel {
        return SecurityViewModel(
            getPinSetupStatusUseCase = GetPinSetupStatusUseCase(repository),
            completePinSetupWithBiometricUseCase = CompletePinSetupWithBiometricUseCase(repository),
            clearAppLockUseCase = ClearAppLockUseCase(repository),
        )
    }

    private class FakeSecurityRepository(
        initialStatus: PinSetupStatus,
    ) : SecurityRepository {
        val currentStatus = MutableStateFlow(initialStatus)
        var pinCleared: Boolean = false

        override fun observePinSetupStatus(): Flow<PinSetupStatus> = currentStatus.asStateFlow()

        override suspend fun setPinSetupStatus(status: PinSetupStatus) {
            currentStatus.value = status
        }

        override suspend fun savePinHash(pinHash: String) = Unit

        override suspend fun clearPinHash() {
            pinCleared = true
        }

        override suspend fun getPinHash(): String? = null
    }
}
