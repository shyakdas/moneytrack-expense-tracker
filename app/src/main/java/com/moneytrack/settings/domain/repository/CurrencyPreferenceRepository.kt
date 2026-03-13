// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.domain.repository

import kotlinx.coroutines.flow.Flow

interface CurrencyPreferenceRepository {
    fun observeSelectedCurrencyCode(): Flow<String?>
    suspend fun saveSelectedCurrencyCode(currencyCode: String)
}
