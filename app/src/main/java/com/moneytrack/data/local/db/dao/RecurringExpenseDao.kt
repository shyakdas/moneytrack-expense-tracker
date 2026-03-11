// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moneytrack.data.local.db.entity.RecurringExpenseEntity

@Dao
interface RecurringExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurringExpense: RecurringExpenseEntity): Long

    @Query("SELECT * FROM recurring_expenses WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): RecurringExpenseEntity?

    @Query("SELECT * FROM recurring_expenses ORDER BY next_run_at_epoch_millis ASC")
    suspend fun getAll(): List<RecurringExpenseEntity>

    @Query(
        "UPDATE recurring_expenses " +
            "SET next_run_at_epoch_millis = :nextRunAtEpochMillis " +
            "WHERE id = :id",
    )
    suspend fun updateNextRunAt(
        id: Long,
        nextRunAtEpochMillis: Long,
    )

    @Query("DELETE FROM recurring_expenses WHERE id = :id")
    suspend fun deleteById(id: Long)
}
