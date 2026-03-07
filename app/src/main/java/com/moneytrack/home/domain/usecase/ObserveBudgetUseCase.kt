package com.moneytrack.home.domain.usecase

import com.moneytrack.home.domain.model.Budget
import com.moneytrack.home.domain.repository.BudgetRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
) {
    operator fun invoke(): Flow<Budget?> = budgetRepository.observeBudget()
}
