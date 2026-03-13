// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.usecase.ClearAppLockUseCase
import com.moneytrack.security.domain.usecase.CompletePinSetupWithBiometricUseCase
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SecurityViewModel @Inject constructor(
    getPinSetupStatusUseCase: GetPinSetupStatusUseCase,
    private val completePinSetupWithBiometricUseCase: CompletePinSetupWithBiometricUseCase,
    private val clearAppLockUseCase: ClearAppLockUseCase,
) : ViewModel() {

    private val _events = Channel<SecurityEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val uiState: StateFlow<SecurityUiState> = getPinSetupStatusUseCase()
        .map { pinSetupStatus ->
            SecurityUiState(selectedOption = pinSetupStatus.toSecurityOption())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
            initialValue = SecurityUiState(),
        )

    fun onOptionSelected(option: SecurityOption) {
        when (option) {
            SecurityOption.PIN -> viewModelScope.launch {
                _events.send(SecurityEvent.OpenPinSetup)
            }

            SecurityOption.BIOMETRIC -> viewModelScope.launch {
                _events.send(SecurityEvent.RequestBiometricPrompt)
            }

            SecurityOption.NONE -> disableSecurity()
        }
    }

    fun onBiometricSelectionResult(success: Boolean) {
        if (!success) return
        viewModelScope.launch {
            completePinSetupWithBiometricUseCase()
            _events.send(SecurityEvent.Completed)
        }
    }

    private fun disableSecurity() {
        viewModelScope.launch {
            clearAppLockUseCase()
            _events.send(SecurityEvent.Completed)
        }
    }

    private companion object {
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
    }
}

data class SecurityUiState(
    val selectedOption: SecurityOption = SecurityOption.NONE,
)

enum class SecurityOption {
    PIN,
    BIOMETRIC,
    NONE,
}

sealed interface SecurityEvent {
    data object RequestBiometricPrompt : SecurityEvent
    data object OpenPinSetup : SecurityEvent
    data object Completed : SecurityEvent
}

private fun PinSetupStatus.toSecurityOption(): SecurityOption =
    when (this) {
        PinSetupStatus.PIN_ENABLED -> SecurityOption.PIN
        PinSetupStatus.BIOMETRIC_ENABLED -> SecurityOption.BIOMETRIC
        PinSetupStatus.NOT_STARTED,
        PinSetupStatus.SKIPPED,
            -> SecurityOption.NONE
    }
