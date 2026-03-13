// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moneytrack.data.local.appDataStore
import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.domain.model.toAppThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ThemePreferencesDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val appThemeModeFlow: Flow<AppThemeMode> =
        context.appDataStore.data.map { preferences ->
            preferences[APP_THEME_MODE_KEY].toAppThemeMode()
        }

    suspend fun saveAppThemeMode(appThemeMode: AppThemeMode) {
        context.appDataStore.edit { preferences ->
            preferences[APP_THEME_MODE_KEY] = appThemeMode.name
        }
    }

    private companion object {
        val APP_THEME_MODE_KEY = stringPreferencesKey("app_theme_mode")
    }
}
