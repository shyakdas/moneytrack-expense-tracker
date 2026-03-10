// Copyright (c) 2026 shyakdas

package com.moneytrack.pinauth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import com.moneytrack.security.domain.usecase.VerifyPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PIN_LENGTH = 4

@HiltViewModel
class PinAuthViewModel @Inject constructor(
    private val getPinSetupStatusUseCase: GetPinSetupStatusUseCase,
    private val verifyPinUseCase: VerifyPinUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinAuthUiState())
    val uiState: StateFlow<PinAuthUiState> = _uiState.asStateFlow()

    private val _events = Channel<PinAuthEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            when (getPinSetupStatusUseCase().first()) {
                PinSetupStatus.PIN_ENABLED -> {
                    _uiState.update { state ->
                        state.copy(isLoading = false, mode = PinAuthMode.PIN)
                    }
                }

                PinSetupStatus.BIOMETRIC_ENABLED -> {
                    _uiState.update { state ->
                        state.copy(isLoading = false, mode = PinAuthMode.BIOMETRIC)
                    }
                    _events.send(PinAuthEvent.RequestBiometricPrompt)
                }

                else -> {
                    _events.send(PinAuthEvent.Authenticated)
                }
            }
        }
    }

    fun onAction(action: PinAuthAction) {
        when (action) {
            is PinAuthAction.EnterDigit -> enterDigit(action.digit)
            PinAuthAction.DeleteDigit -> deleteDigit()
            PinAuthAction.SubmitPin -> submitPin()
            PinAuthAction.RequestBiometricAuth -> requestBiometricAuth()
            is PinAuthAction.BiometricAuthResult -> handleBiometricResult(action.success)
        }
    }

    private fun enterDigit(digit: Int) {
        val state = _uiState.value
        if (state.mode != PinAuthMode.PIN || state.enteredPin.length >= PIN_LENGTH) return
        _uiState.update {
            it.copy(
                enteredPin = it.enteredPin + digit.toString(),
                showPinError = false,
            )
        }

        if (_uiState.value.enteredPin.length == PIN_LENGTH) {
            submitPin()
        }
    }

    private fun deleteDigit() {
        _uiState.update { state ->
            if (state.mode != PinAuthMode.PIN || state.enteredPin.isEmpty()) {
                state
            } else {
                state.copy(
                    enteredPin = state.enteredPin.dropLast(1),
                    showPinError = false,
                )
            }
        }
    }

    private fun submitPin() {
        val state = _uiState.value
        if (state.mode != PinAuthMode.PIN || state.enteredPin.length != PIN_LENGTH) return

        viewModelScope.launch {
            val isValid = verifyPinUseCase(state.enteredPin)
            if (isValid) {
                _events.send(PinAuthEvent.Authenticated)
            } else {
                _uiState.update {
                    it.copy(
                        enteredPin = "",
                        showPinError = true,
                    )
                }
            }
        }
    }

    private fun requestBiometricAuth() {
        if (_uiState.value.mode != PinAuthMode.BIOMETRIC) return
        viewModelScope.launch {
            _events.send(PinAuthEvent.RequestBiometricPrompt)
        }
    }

    private fun handleBiometricResult(success: Boolean) {
        if (success) {
            viewModelScope.launch { _events.send(PinAuthEvent.Authenticated) }
        } else {
            _uiState.update { it.copy(showBiometricError = true) }
        }
    }
}
