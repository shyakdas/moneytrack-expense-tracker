// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moneytrack.data.local.db.entity.CurrencyPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyPreferenceDao {

    @Query("SELECT currency_code FROM currency_preferences WHERE id = 1 LIMIT 1")
    fun observeSelectedCurrencyCode(): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCurrencyPreference(preference: CurrencyPreferenceEntity)
}
