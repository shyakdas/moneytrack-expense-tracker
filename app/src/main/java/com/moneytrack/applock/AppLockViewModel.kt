package com.moneytrack.applock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val RELOCK_TIMEOUT_MS = 5 * 60 * 1000L

@HiltViewModel
class AppLockViewModel @Inject constructor(
    private val getPinSetupStatusUseCase: GetPinSetupStatusUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppLockUiState())
    val uiState: StateFlow<AppLockUiState> = _uiState.asStateFlow()

    private var lastBackgroundAtMillis: Long? = null

    fun onAppBackgrounded(isChangingConfigurations: Boolean) {
        if (isChangingConfigurations) return
        lastBackgroundAtMillis = System.currentTimeMillis()
    }

    fun onAppForegrounded() {
        val backgroundAt = lastBackgroundAtMillis ?: return
        lastBackgroundAtMillis = null
        val elapsed = System.currentTimeMillis() - backgroundAt
        if (elapsed < RELOCK_TIMEOUT_MS) return

        viewModelScope.launch {
            if (isAppLockEnabled()) {
                _uiState.update { state -> state.copy(forcePinAuth = true) }
            }
        }
    }

    fun onForcePinAuthHandled() {
        _uiState.update { state -> state.copy(forcePinAuth = false) }
    }

    fun onPinAuthenticated() {
        onForcePinAuthHandled()
    }

    private suspend fun isAppLockEnabled(): Boolean =
        when (getPinSetupStatusUseCase().first()) {
            PinSetupStatus.PIN_ENABLED,
            PinSetupStatus.BIOMETRIC_ENABLED,
                -> true

            else -> false
        }
}
