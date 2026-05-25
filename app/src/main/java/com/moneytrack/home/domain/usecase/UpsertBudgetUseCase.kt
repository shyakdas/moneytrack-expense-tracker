// Copyright (c) 2026 shyakdas

package com.moneytrack.home.domain.usecase

import com.moneytrack.home.domain.repository.BudgetRepository
import javax.inject.Inject

class UpsertBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
) {
    suspend operator fun invoke(
        month: Int,
        year: Int,
        amount: Double,
        description: String?,
    ) {
        budgetRepository.upsertBudget(
            month = month,
            year = year,
            amount = amount,
            description = description,
        )
    }
}
