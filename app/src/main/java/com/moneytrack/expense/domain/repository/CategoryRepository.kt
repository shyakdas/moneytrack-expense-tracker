// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.repository

import com.moneytrack.expense.domain.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<ExpenseCategory>>
    suspend fun ensureDefaultCategories()
    suspend fun addCategory(name: String)
    suspend fun moveCategoryUp(categoryId: Long)
    suspend fun moveCategoryDown(categoryId: Long)
    suspend fun reorderCategories(categoryIds: List<Long>)
}
