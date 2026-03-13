// Copyright (c) 2026 shyakdas

package com.moneytrack.security.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.moneytrack.data.local.appDataStore
import com.moneytrack.security.domain.model.PinSetupStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityPreferencesDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private companion object {
        val PIN_SETUP_STATUS_KEY = stringPreferencesKey("pin_setup_status")
        val PIN_HASH_KEY = stringPreferencesKey("pin_hash")
    }

    val pinSetupStatusFlow: Flow<PinSetupStatus> =
        context.appDataStore.data.map { preferences ->
            preferences[PIN_SETUP_STATUS_KEY].toPinSetupStatus()
        }

    suspend fun setPinSetupStatus(status: PinSetupStatus) {
        context.appDataStore.edit { preferences ->
            preferences[PIN_SETUP_STATUS_KEY] = status.name
        }
    }

    suspend fun savePinHash(pinHash: String) {
        context.appDataStore.edit { preferences ->
            preferences[PIN_HASH_KEY] = pinHash
        }
    }

    suspend fun clearPinHash() {
        context.appDataStore.edit { preferences ->
            preferences.remove(PIN_HASH_KEY)
        }
    }

    suspend fun getPinHash(): String? {
        val preferences: Preferences = context.appDataStore.data.first()
        return preferences[PIN_HASH_KEY]
    }
}

private fun String?.toPinSetupStatus(): PinSetupStatus =
    runCatching { this?.let(PinSetupStatus::valueOf) ?: PinSetupStatus.NOT_STARTED }
        .getOrDefault(PinSetupStatus.NOT_STARTED)
