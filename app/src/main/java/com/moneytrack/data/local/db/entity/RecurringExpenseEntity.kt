// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_expenses")
data class RecurringExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "note")
    val note: String? = null,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "frequency")
    val frequency: String,
    @ColumnInfo(name = "end_at_epoch_millis")
    val endAtEpochMillis: Long,
    @ColumnInfo(name = "next_run_at_epoch_millis")
    val nextRunAtEpochMillis: Long,
    @ColumnInfo(name = "created_at_epoch_millis")
    val createdAtEpochMillis: Long,
)
