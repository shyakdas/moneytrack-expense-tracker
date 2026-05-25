// Copyright (c) 2026 shyakdas

package com.moneytrack.home.data.repository

import com.moneytrack.data.local.db.dao.BudgetDao
import com.moneytrack.data.local.db.entity.BudgetEntity
import com.moneytrack.home.domain.model.Budget
import com.moneytrack.home.domain.repository.BudgetRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
) : BudgetRepository {

    override fun observeBudget(month: Int, year: Int): Flow<Budget?> =
        budgetDao.observeBudget(month = month, year = year).map { entity ->
            entity?.toDomain()
        }

    override suspend fun upsertBudget(
        month: Int,
        year: Int,
        amount: Double,
        description: String?,
    ) {
        val existingBudgetId = budgetDao.getBudgetId(month = month, year = year)
        budgetDao.upsertBudget(
            BudgetEntity(
                id = existingBudgetId ?: 0,
                month = month,
                year = year,
                amount = amount,
                description = description,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }
}

private fun BudgetEntity.toDomain(): Budget = Budget(
    month = month,
    year = year,
    amount = amount,
    description = description,
    updatedAtEpochMillis = updatedAtEpochMillis,
)
