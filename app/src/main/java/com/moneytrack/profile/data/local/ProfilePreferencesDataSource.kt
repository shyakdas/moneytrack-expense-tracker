// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moneytrack.data.local.appDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ProfilePreferencesDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private companion object {
        val DISPLAY_NAME_KEY = stringPreferencesKey("profile_display_name")
    }

    val displayNameFlow: Flow<String> =
        context.appDataStore.data.map { preferences ->
            preferences[DISPLAY_NAME_KEY].orEmpty()
        }

    suspend fun saveDisplayName(displayName: String) {
        context.appDataStore.edit { preferences ->
            preferences[DISPLAY_NAME_KEY] = displayName
        }
    }
}
