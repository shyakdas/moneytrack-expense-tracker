package com.moneytrack.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_budget")
data class BudgetEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = BUDGET_SINGLETON_ID,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "updated_at_epoch_millis")
    val updatedAtEpochMillis: Long,
)

const val BUDGET_SINGLETON_ID = 1
