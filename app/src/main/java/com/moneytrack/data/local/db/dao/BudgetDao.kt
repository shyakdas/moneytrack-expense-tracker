// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moneytrack.data.local.db.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM monthly_budget WHERE month = :month AND year = :year LIMIT 1")
    fun observeBudget(month: Int, year: Int): Flow<BudgetEntity?>

    @Query("SELECT id FROM monthly_budget WHERE month = :month AND year = :year LIMIT 1")
    suspend fun getBudgetId(month: Int, year: Int): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: BudgetEntity)
}
