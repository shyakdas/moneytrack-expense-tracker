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
import javax.inject.Inject

@HiltViewModel
class AppEntryViewModel @Inject constructor(
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppEntryUiState())
    val uiState: StateFlow<AppEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isOnboardingCompleted = getOnboardingCompletedUseCase().first()
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    startDestination = if (isOnboardingCompleted) {
                        AppDestination.Auth.route
                    } else {
                        AppDestination.Onboarding.route
                    },
                )
            }
        }
    }
}
