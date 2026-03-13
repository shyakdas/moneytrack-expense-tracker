// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.data.repository

import com.moneytrack.data.local.db.dao.CurrencyPreferenceDao
import com.moneytrack.data.local.db.entity.CurrencyPreferenceEntity
import com.moneytrack.settings.domain.repository.CurrencyPreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CurrencyPreferenceRepositoryImpl @Inject constructor(
    private val currencyPreferenceDao: CurrencyPreferenceDao,
) : CurrencyPreferenceRepository {

    override fun observeSelectedCurrencyCode(): Flow<String?> =
        currencyPreferenceDao.observeSelectedCurrencyCode()

    override suspend fun saveSelectedCurrencyCode(currencyCode: String) {
        currencyPreferenceDao.upsertCurrencyPreference(
            preference = CurrencyPreferenceEntity(currencyCode = currencyCode),
        )
    }
}
