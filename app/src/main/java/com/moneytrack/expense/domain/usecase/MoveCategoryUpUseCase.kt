// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.usecase

import com.moneytrack.expense.domain.repository.CategoryRepository
import javax.inject.Inject

class MoveCategoryUpUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(categoryId: Long) {
        categoryRepository.moveCategoryUp(categoryId = categoryId)
    }
}

