// Copyright (c) 2026 shyakdas

package com.moneytrack.home.data.repository

import com.moneytrack.data.local.db.dao.BudgetDao
import com.moneytrack.data.local.db.entity.BUDGET_SINGLETON_ID
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

    override fun observeBudget(): Flow<Budget?> =
        budgetDao.observeBudget().map { entity ->
            entity?.toDomain()
        }

    override suspend fun upsertBudget(
        amount: Double,
        description: String?,
    ) {
        budgetDao.upsertBudget(
            BudgetEntity(
                id = BUDGET_SINGLETON_ID,
                amount = amount,
                description = description,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }
}

private fun BudgetEntity.toDomain(): Budget = Budget(
    amount = amount,
    description = description,
    updatedAtEpochMillis = updatedAtEpochMillis,
)
