// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.usecase

import com.moneytrack.expense.domain.repository.CategoryRepository
import javax.inject.Inject

class MoveCategoryDownUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(categoryId: Long) {
        categoryRepository.moveCategoryDown(categoryId = categoryId)
    }
}

