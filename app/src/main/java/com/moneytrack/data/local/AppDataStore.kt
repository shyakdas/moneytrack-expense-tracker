// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val PREFERENCES_NAME = "moneytrack_preferences"

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)
