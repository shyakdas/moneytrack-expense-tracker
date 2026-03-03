package com.moneytrack.pinsetup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.moneytrack.security.domain.usecase.CompletePinSetupWithBiometricUseCase
import com.moneytrack.security.domain.usecase.CompletePinSetupWithPinUseCase
import com.moneytrack.security.domain.usecase.SkipPinSetupUseCase
import javax.inject.Inject

private const val PIN_LENGTH = 4
private const val MAX_CONFIRM_ATTEMPTS = 3
private const val SUCCESS_DELAY_MS = 1200L

@HiltViewModel
class PinSetupViewModel @Inject constructor(
    private val completePinSetupWithPinUseCase: CompletePinSetupWithPinUseCase,
    private val completePinSetupWithBiometricUseCase: CompletePinSetupWithBiometricUseCase,
    private val skipPinSetupUseCase: SkipPinSetupUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinSetupUiState())
    val uiState: StateFlow<PinSetupUiState> = _uiState.asStateFlow()

    private val _events = Channel<PinSetupEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: PinSetupAction) {
        when (action) {
            PinSetupAction.SelectPin -> startPinEntry()
            PinSetupAction.SelectBiometric -> completeWithBiometric()
            PinSetupAction.Skip -> skipSetup()
            PinSetupAction.DeleteDigit -> deleteDigit()
            is PinSetupAction.EnterDigit -> enterDigit(action.digit)
            PinSetupAction.SubmitPin -> submitPin()
            PinSetupAction.ForgotPin -> requestRecoveryVerification()
            is PinSetupAction.RecoveryVerificationResult -> handleRecoveryVerification(action.success)
        }
    }

    private fun startPinEntry() {
        _uiState.update {
            it.copy(
                stage = PinSetupStage.CREATE_PIN,
                enteredPin = "",
                firstPin = "",
                showPinMismatch = false,
                failedAttempts = 0,
                isLockedOut = false,
                showRecoveryError = false,
            )
        }
    }

    private fun completeWithBiometric() {
        viewModelScope.launch {
            completePinSetupWithBiometricUseCase()
            showSuccessThenComplete()
        }
    }

    private fun skipSetup() {
        viewModelScope.launch {
            skipPinSetupUseCase()
            _events.send(PinSetupEvent.Completed)
        }
    }

    private fun enterDigit(digit: Int) {
        val state = _uiState.value
        if (
            state.stage == PinSetupStage.INTRO ||
            state.enteredPin.length >= PIN_LENGTH ||
            state.isLockedOut
        ) {
            return
        }

        _uiState.update {
            it.copy(
                enteredPin = it.enteredPin + digit.toString(),
                showPinMismatch = false,
                showRecoveryError = false,
            )
        }
    }

    private fun deleteDigit() {
        _uiState.update { state ->
            if (state.enteredPin.isEmpty() || state.isLockedOut) {
                state
            } else {
                state.copy(
                enteredPin = state.enteredPin.dropLast(1),
                showPinMismatch = false,
                showRecoveryError = false,
                )
            }
        }
    }

    private fun submitPin() {
        val state = _uiState.value
        if (state.enteredPin.length != PIN_LENGTH || state.isLockedOut) return

        when (state.stage) {
            PinSetupStage.INTRO -> Unit
            PinSetupStage.CREATE_PIN -> {
                _uiState.update {
                    it.copy(
                        stage = PinSetupStage.CONFIRM_PIN,
                        firstPin = state.enteredPin,
                        enteredPin = "",
                        showPinMismatch = false,
                        showRecoveryError = false,
                    )
                }
            }

            PinSetupStage.CONFIRM_PIN -> {
                if (state.enteredPin == state.firstPin) {
                    viewModelScope.launch {
                        completePinSetupWithPinUseCase(state.enteredPin)
                        showSuccessThenComplete()
                    }
                } else {
                    val failedAttempts = state.failedAttempts + 1
                    val isLockedOut = failedAttempts >= MAX_CONFIRM_ATTEMPTS
                    _uiState.update {
                        it.copy(
                            stage = if (isLockedOut) PinSetupStage.CONFIRM_PIN else PinSetupStage.CREATE_PIN,
                            firstPin = if (isLockedOut) state.firstPin else "",
                            enteredPin = "",
                            showPinMismatch = true,
                            failedAttempts = failedAttempts,
                            isLockedOut = isLockedOut,
                            showRecoveryError = false,
                        )
                    }
                }
            }
            PinSetupStage.SUCCESS -> Unit
        }
    }

    private fun requestRecoveryVerification() {
        val state = _uiState.value
        if (!state.isLockedOut) return
        viewModelScope.launch {
            _events.send(PinSetupEvent.RequestRecoveryAuth)
        }
    }

    private fun handleRecoveryVerification(success: Boolean) {
        if (success) {
            startPinEntry()
            return
        }
        _uiState.update {
            it.copy(showRecoveryError = true)
        }
    }

    private suspend fun showSuccessThenComplete() {
        _uiState.update { state ->
            state.copy(
                stage = PinSetupStage.SUCCESS,
                enteredPin = "",
                showPinMismatch = false,
                failedAttempts = 0,
                isLockedOut = false,
                showRecoveryError = false,
            )
        }
        delay(SUCCESS_DELAY_MS)
        _events.send(PinSetupEvent.Completed)
    }
}
