// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_preferences")
data class CurrencyPreferenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = CURRENCY_PREFERENCE_SINGLETON_ID,
    @ColumnInfo(name = "currency_code")
    val currencyCode: String,
)

const val CURRENCY_PREFERENCE_SINGLETON_ID = 1
