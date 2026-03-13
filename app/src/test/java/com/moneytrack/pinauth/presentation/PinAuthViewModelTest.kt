// Copyright (c) 2026 shyakdas

package com.moneytrack.pinauth.presentation

import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.repository.SecurityRepository
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import com.moneytrack.security.domain.usecase.VerifyPinUseCase
import com.moneytrack.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PinAuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun init_withPinEnabled_setsPinMode() = runTest {
        val viewModel = createViewModel(
            repository = FakeSecurityRepository(
                status = PinSetupStatus.PIN_ENABLED,
                savedPin = "1234",
            ),
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(PinAuthMode.PIN, state.mode)
    }

    @Test
    fun submitCorrectPin_emitsAuthenticatedEvent() = runTest {
        val viewModel = createViewModel(
            repository = FakeSecurityRepository(
                status = PinSetupStatus.PIN_ENABLED,
                savedPin = "1234",
            ),
        )
        val eventDeferred = async { viewModel.events.first() }
        advanceUntilIdle()

        "1234".forEach { char ->
            viewModel.onAction(PinAuthAction.EnterDigit(char.digitToInt()))
        }
        viewModel.onAction(PinAuthAction.SubmitPin)
        advanceUntilIdle()

        assertEquals(PinAuthEvent.Authenticated, eventDeferred.await())
    }

    @Test
    fun submitWrongPin_showsErrorAndClearsInput() = runTest {
        val viewModel = createViewModel(
            repository = FakeSecurityRepository(
                status = PinSetupStatus.PIN_ENABLED,
                savedPin = "1234",
            ),
        )
        advanceUntilIdle()

        "9999".forEach { char ->
            viewModel.onAction(PinAuthAction.EnterDigit(char.digitToInt()))
        }
        viewModel.onAction(PinAuthAction.SubmitPin)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showPinError)
        assertEquals("", state.enteredPin)
    }

    @Test
    fun init_withBiometricEnabled_requestsBiometricPrompt() = runTest {
        val viewModel = createViewModel(
            repository = FakeSecurityRepository(
                status = PinSetupStatus.BIOMETRIC_ENABLED,
                savedPin = "1234",
            ),
        )
        val eventDeferred = async { viewModel.events.first() }

        advanceUntilIdle()

        assertEquals(PinAuthEvent.RequestBiometricPrompt, eventDeferred.await())
        assertEquals(PinAuthMode.BIOMETRIC, viewModel.uiState.value.mode)
    }

    @Test
    fun enteringFourthDigitCorrectPin_autoAuthenticatesWithoutSubmit() = runTest {
        val viewModel = createViewModel(
            repository = FakeSecurityRepository(
                status = PinSetupStatus.PIN_ENABLED,
                savedPin = "1234",
            ),
        )
        val eventDeferred = async { viewModel.events.first() }
        advanceUntilIdle()

        "1234".forEach { char ->
            viewModel.onAction(PinAuthAction.EnterDigit(char.digitToInt()))
        }
        advanceUntilIdle()

        assertEquals(PinAuthEvent.Authenticated, eventDeferred.await())
    }

    @Test
    fun enteringFourthDigitWrongPin_autoShowsErrorWithoutSubmit() = runTest {
        val viewModel = createViewModel(
            repository = FakeSecurityRepository(
                status = PinSetupStatus.PIN_ENABLED,
                savedPin = "1234",
            ),
        )
        advanceUntilIdle()

        "9999".forEach { char ->
            viewModel.onAction(PinAuthAction.EnterDigit(char.digitToInt()))
        }
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showPinError)
        assertEquals("", viewModel.uiState.value.enteredPin)
    }

    private fun createViewModel(repository: FakeSecurityRepository): PinAuthViewModel {
        return PinAuthViewModel(
            getPinSetupStatusUseCase = GetPinSetupStatusUseCase(repository),
            verifyPinUseCase = VerifyPinUseCase(repository),
        )
    }

    private class FakeSecurityRepository(
        private val status: PinSetupStatus,
        savedPin: String,
    ) : SecurityRepository {
        private val savedPinHash = sha256(savedPin)

        override fun observePinSetupStatus(): Flow<PinSetupStatus> = flowOf(status)

        override suspend fun setPinSetupStatus(status: PinSetupStatus) = Unit

        override suspend fun savePinHash(pinHash: String) = Unit

        override suspend fun clearPinHash() = Unit

        override suspend fun getPinHash(): String = savedPinHash

        private fun sha256(value: String): String {
            val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
            return bytes.joinToString(separator = "") { byte ->
                "%02x".format(byte)
            }
        }
    }
}
