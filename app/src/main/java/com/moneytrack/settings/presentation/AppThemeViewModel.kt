// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.domain.usecase.ObserveAppThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    observeAppThemeModeUseCase: ObserveAppThemeModeUseCase,
) : ViewModel() {

    val appThemeMode: StateFlow<AppThemeMode> = observeAppThemeModeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
            initialValue = AppThemeMode.SYSTEM,
        )

    private companion object {
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
    }
}
