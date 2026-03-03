package com.moneytrack.startup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.moneytrack.navigation.AppDestination
import javax.inject.Inject

@HiltViewModel
class AppEntryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AppEntryUiState())
    val uiState: StateFlow<AppEntryUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                startDestination = AppDestination.Onboarding.route,
            )
        }
    }
}
