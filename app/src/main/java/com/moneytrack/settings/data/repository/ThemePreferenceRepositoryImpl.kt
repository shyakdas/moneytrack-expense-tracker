// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.data.repository

import com.moneytrack.settings.data.local.ThemePreferencesDataSource
import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.domain.repository.ThemePreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ThemePreferenceRepositoryImpl @Inject constructor(
    private val themePreferencesDataSource: ThemePreferencesDataSource,
) : ThemePreferenceRepository {

    override fun observeAppThemeMode(): Flow<AppThemeMode> =
        themePreferencesDataSource.appThemeModeFlow

    override suspend fun saveAppThemeMode(appThemeMode: AppThemeMode) {
        themePreferencesDataSource.saveAppThemeMode(appThemeMode)
    }
}
