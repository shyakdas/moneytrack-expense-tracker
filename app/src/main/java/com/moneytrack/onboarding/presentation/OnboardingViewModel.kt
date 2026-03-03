package com.moneytrack.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.moneytrack.onboarding.domain.usecase.SetOnboardingCompletedUseCase
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
) : ViewModel() {

    fun onAction(action: OnboardingAction) {
        viewModelScope.launch {
            when (action) {
                OnboardingAction.OnFinishedClick -> setOnboardingCompletedUseCase()
            }
        }
    }
}
