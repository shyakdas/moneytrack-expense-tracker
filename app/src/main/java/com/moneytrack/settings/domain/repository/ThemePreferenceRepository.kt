// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.domain.repository

import com.moneytrack.settings.domain.model.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemePreferenceRepository {
    fun observeAppThemeMode(): Flow<AppThemeMode>
    suspend fun saveAppThemeMode(appThemeMode: AppThemeMode)
}
