// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.usecase

import com.moneytrack.expense.domain.model.ExpenseCategory
import com.moneytrack.expense.domain.repository.CategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(): Flow<List<ExpenseCategory>> = categoryRepository.observeCategories()
}

