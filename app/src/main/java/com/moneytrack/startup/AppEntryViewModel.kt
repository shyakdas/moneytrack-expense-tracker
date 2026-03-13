// Copyright (c) 2026 shyakdas

package com.moneytrack.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.navigation.AppDestination
import com.moneytrack.onboarding.domain.usecase.GetOnboardingCompletedUseCase
import com.moneytrack.security.domain.model.PinSetupStatus
import com.moneytrack.security.domain.usecase.GetPinSetupStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppEntryViewModel @Inject constructor(
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val getPinSetupStatusUseCase: GetPinSetupStatusUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AppEntryUiState(
            isLoading = true,
            startDestination = AppDestination.Onboarding.route,
        ),
    )
    val uiState: StateFlow<AppEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getOnboardingCompletedUseCase(),
                getPinSetupStatusUseCase(),
            ) { onboardingCompleted, pinSetupStatus ->
                when {
                    !onboardingCompleted -> AppDestination.Onboarding.route
                    pinSetupStatus == PinSetupStatus.NOT_STARTED -> AppDestination.PinSetup.route
                    pinSetupStatus == PinSetupStatus.SKIPPED -> AppDestination.Home.route
                    else -> AppDestination.PinAuth.route
                }
            }.collect { destination ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        startDestination = destination,
                    )
                }
            }
        }
    }
}
