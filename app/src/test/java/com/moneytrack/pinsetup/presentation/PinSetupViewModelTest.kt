// Copyright (c) 2026 shyakdas

package com.moneytrack.pinsetup.presentation

import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import com.moneytrack.security.domain.usecase.CompletePinSetupWithBiometricUseCase
import com.moneytrack.security.domain.usecase.CompletePinSetupWithPinUseCase
import com.moneytrack.security.domain.usecase.SkipPinSetupUseCase
import com.moneytrack.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PinSetupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun submitPin_success_movesToSuccessStage_andEmitsCompletedEvent() = runTest {
        val fakeRepository = FakeSecurityRepository()
        val viewModel = createViewModel(fakeRepository)

        viewModel.onAction(PinSetupAction.SelectPin)
        enterPin(viewModel, "1234")
        viewModel.onAction(PinSetupAction.SubmitPin)

        enterPin(viewModel, "1234")
        val completedEvent = async { viewModel.events.first() }
        viewModel.onAction(PinSetupAction.SubmitPin)

        advanceUntilIdle()
        assertEquals(PinSetupStage.SUCCESS, viewModel.uiState.value.stage)
        assertEquals(PinSetupStatus.PIN_ENABLED, fakeRepository.savedStatus)
        assertTrue(fakeRepository.savedPinHash.isNotBlank())

        advanceTimeBy(1200)
        advanceUntilIdle()
        assertEquals(PinSetupEvent.Completed, completedEvent.await())
    }

    @Test
    fun threeWrongConfirmAttempts_locksOut_andForgotPinRequestsRecoveryAuth() = runTest {
        val viewModel = createViewModel(FakeSecurityRepository())
        val recoveryEvent = async { viewModel.events.first() }

        viewModel.onAction(PinSetupAction.SelectPin)
        repeat(3) {
            enterPin(viewModel, "1234")
            viewModel.onAction(PinSetupAction.SubmitPin)
            enterPin(viewModel, "9999")
            viewModel.onAction(PinSetupAction.SubmitPin)
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(PinSetupStage.CONFIRM_PIN, state.stage)
        assertTrue(state.isLockedOut)
        assertEquals(3, state.failedAttempts)

        viewModel.onAction(PinSetupAction.ForgotPin)
        advanceUntilIdle()
        assertEquals(PinSetupEvent.RequestRecoveryAuth, recoveryEvent.await())
    }

    @Test
    fun recoveryVerificationSuccess_resetsToCreatePinUnlocked() = runTest {
        val viewModel = createViewModel(FakeSecurityRepository())

        viewModel.onAction(PinSetupAction.SelectPin)
        repeat(3) {
            enterPin(viewModel, "1234")
            viewModel.onAction(PinSetupAction.SubmitPin)
            enterPin(viewModel, "9999")
            viewModel.onAction(PinSetupAction.SubmitPin)
        }
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isLockedOut)

        viewModel.onAction(PinSetupAction.RecoveryVerificationResult(success = true))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(PinSetupStage.CREATE_PIN, state.stage)
        assertFalse(state.isLockedOut)
        assertEquals(0, state.failedAttempts)
        assertFalse(state.showRecoveryError)
    }

    @Test
    fun enterFourthDigitInCreatePin_autoMovesToConfirmPin() = runTest {
        val viewModel = createViewModel(FakeSecurityRepository())

        viewModel.onAction(PinSetupAction.SelectPin)
        enterPin(viewModel, "1234")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(PinSetupStage.CONFIRM_PIN, state.stage)
        assertEquals("", state.enteredPin)
        assertEquals("1234", state.firstPin)
    }

    @Test
    fun enterFourthDigitInConfirmPin_autoCompletesFlow() = runTest {
        val fakeRepository = FakeSecurityRepository()
        val viewModel = createViewModel(fakeRepository)
        val completedEvent = async { viewModel.events.first() }

        viewModel.onAction(PinSetupAction.SelectPin)
        enterPin(viewModel, "1234")
        enterPin(viewModel, "1234")
        advanceUntilIdle()

        assertEquals(PinSetupStage.SUCCESS, viewModel.uiState.value.stage)
        assertEquals(PinSetupStatus.PIN_ENABLED, fakeRepository.savedStatus)

        advanceTimeBy(1200)
        advanceUntilIdle()
        assertEquals(PinSetupEvent.Completed, completedEvent.await())
    }

    private fun createViewModel(repository: FakeSecurityRepository): PinSetupViewModel {
        return PinSetupViewModel(
            completePinSetupWithPinUseCase = CompletePinSetupWithPinUseCase(repository),
            completePinSetupWithBiometricUseCase = CompletePinSetupWithBiometricUseCase(repository),
            skipPinSetupUseCase = SkipPinSetupUseCase(repository),
        )
    }

    private fun enterPin(viewModel: PinSetupViewModel, pin: String) {
        pin.forEach { char ->
            viewModel.onAction(PinSetupAction.EnterDigit(char.digitToInt()))
        }
    }

    private class FakeSecurityRepository : SecurityRepository {
        var savedStatus: PinSetupStatus = PinSetupStatus.NOT_STARTED
        var savedPinHash: String = ""

        override fun observePinSetupStatus(): Flow<PinSetupStatus> = flowOf(savedStatus)

        override suspend fun setPinSetupStatus(status: PinSetupStatus) {
            savedStatus = status
        }

        override suspend fun savePinHash(pinHash: String) {
            savedPinHash = pinHash
        }

        override suspend fun clearPinHash() {
            savedPinHash = ""
        }

        override suspend fun getPinHash(): String? = savedPinHash.ifBlank { null }
    }
}
