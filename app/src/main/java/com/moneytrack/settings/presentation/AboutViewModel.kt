// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.lifecycle.ViewModel
import com.moneytrack.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(
        AboutUiState(versionName = BuildConfig.VERSION_NAME),
    )
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()
}

data class AboutUiState(
    val versionName: String,
)
