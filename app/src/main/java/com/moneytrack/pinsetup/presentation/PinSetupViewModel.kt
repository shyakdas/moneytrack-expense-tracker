package com.moneytrack.pinsetup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
        }
    }

    private fun startPinEntry() {
        _uiState.update {
            it.copy(
                stage = PinSetupStage.CREATE_PIN,
                enteredPin = "",
                firstPin = "",
                showPinMismatch = false,
            )
        }
    }

    private fun completeWithBiometric() {
        viewModelScope.launch {
            completePinSetupWithBiometricUseCase()
            _events.send(PinSetupEvent.Completed)
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
        if (state.stage == PinSetupStage.INTRO || state.enteredPin.length >= PIN_LENGTH) return

        _uiState.update {
            it.copy(
                enteredPin = it.enteredPin + digit.toString(),
                showPinMismatch = false,
            )
        }
    }

    private fun deleteDigit() {
        _uiState.update { state ->
            if (state.enteredPin.isEmpty()) state else state.copy(
                enteredPin = state.enteredPin.dropLast(1),
                showPinMismatch = false,
            )
        }
    }

    private fun submitPin() {
        val state = _uiState.value
        if (state.enteredPin.length != PIN_LENGTH) return

        when (state.stage) {
            PinSetupStage.INTRO -> Unit
            PinSetupStage.CREATE_PIN -> {
                _uiState.update {
                    it.copy(
                        stage = PinSetupStage.CONFIRM_PIN,
                        firstPin = state.enteredPin,
                        enteredPin = "",
                        showPinMismatch = false,
                    )
                }
            }

            PinSetupStage.CONFIRM_PIN -> {
                if (state.enteredPin == state.firstPin) {
                    viewModelScope.launch {
                        completePinSetupWithPinUseCase(state.enteredPin)
                        _events.send(PinSetupEvent.Completed)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            stage = PinSetupStage.CREATE_PIN,
                            firstPin = "",
                            enteredPin = "",
                            showPinMismatch = true,
                        )
                    }
                }
            }
        }
    }
}
