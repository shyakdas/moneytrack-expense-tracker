// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.domain.usecase.ObserveAppThemeModeUseCase
import com.moneytrack.settings.domain.usecase.SaveAppThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    observeAppThemeModeUseCase: ObserveAppThemeModeUseCase,
    private val saveAppThemeModeUseCase: SaveAppThemeModeUseCase,
) : ViewModel() {

    val uiState: StateFlow<ThemeUiState> = observeAppThemeModeUseCase()
        .map { selectedThemeMode ->
            ThemeUiState(selectedThemeMode = selectedThemeMode)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
            initialValue = ThemeUiState(),
        )

    fun onThemeModeSelected(appThemeMode: AppThemeMode) {
        viewModelScope.launch {
            saveAppThemeModeUseCase(appThemeMode)
        }
    }

    private companion object {
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
    }
}

data class ThemeUiState(
    val selectedThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
)
