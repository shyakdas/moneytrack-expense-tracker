// Copyright (c) 2026 shyakdas

package com.moneytrack.home.domain.repository

import com.moneytrack.home.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeBudget(month: Int, year: Int): Flow<Budget?>
    suspend fun upsertBudget(
        month: Int,
        year: Int,
        amount: Double,
        description: String?,
    )
}
