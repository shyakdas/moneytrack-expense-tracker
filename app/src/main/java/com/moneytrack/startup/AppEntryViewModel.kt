package com.moneytrack.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.moneytrack.navigation.AppDestination
import com.moneytrack.onboarding.domain.usecase.GetOnboardingCompletedUseCase
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import javax.inject.Inject

@HiltViewModel
class AppEntryViewModel @Inject constructor(
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val getPinSetupStatusUseCase: GetPinSetupStatusUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppEntryUiState())
    val uiState: StateFlow<AppEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val onboardingCompleted = getOnboardingCompletedUseCase().first()
            val pinSetupStatus = getPinSetupStatusUseCase().first()
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    startDestination = when {
                        !onboardingCompleted -> AppDestination.Onboarding.route
                        pinSetupStatus == PinSetupStatus.NOT_STARTED -> AppDestination.PinSetup.route
                        pinSetupStatus == PinSetupStatus.SKIPPED -> AppDestination.Home.route
                        else -> AppDestination.PinAuth.route
                    },
                )
            }
        }
    }
}
