// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.data.repository

import com.moneytrack.data.local.db.dao.CategoryDao
import com.moneytrack.data.local.db.entity.CategoryEntity
import com.moneytrack.expense.domain.model.ExpenseCategory
import com.moneytrack.expense.domain.repository.CategoryRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
) : CategoryRepository {

    override fun observeCategories(): Flow<List<ExpenseCategory>> =
        categoryDao.observeCategories().map { entities ->
            entities.map { entity -> entity.toDomain() }
        }

    override suspend fun ensureDefaultCategories() {
        if (categoryDao.countCategories() > 0) return
        val now = System.currentTimeMillis()
        categoryDao.insertAll(
            DEFAULT_CATEGORIES.mapIndexed { index, category ->
                CategoryEntity(
                    name = category.name,
                    colorHex = category.colorHex,
                    sortOrder = index,
                    isDefault = true,
                    createdAtEpochMillis = now,
                )
            },
        )
    }

    override suspend fun addCategory(name: String) {
        val normalizedName = name.trim()
        if (normalizedName.isEmpty()) return
        val nextSortOrder = (categoryDao.maxSortOrder() ?: -1) + 1
        categoryDao.insert(
            CategoryEntity(
                name = normalizedName,
                colorHex = CUSTOM_CATEGORY_COLOR_HEX,
                sortOrder = nextSortOrder,
                isDefault = false,
                createdAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun moveCategoryUp(categoryId: Long) {
        val orderedCategories = categoryDao.getOrderedCategories()
        val currentIndex = orderedCategories.indexOfFirst { category ->
            category.id == categoryId
        }
        if (currentIndex <= 0) return

        val current = orderedCategories[currentIndex]
        val previous = orderedCategories[currentIndex - 1]
        categoryDao.updateSortOrder(
            categoryId = current.id,
            sortOrder = previous.sortOrder,
        )
        categoryDao.updateSortOrder(
            categoryId = previous.id,
            sortOrder = current.sortOrder,
        )
    }

    override suspend fun moveCategoryDown(categoryId: Long) {
        val orderedCategories = categoryDao.getOrderedCategories()
        val currentIndex = orderedCategories.indexOfFirst { category ->
            category.id == categoryId
        }
        if (currentIndex == -1 || currentIndex >= orderedCategories.lastIndex) return

        val current = orderedCategories[currentIndex]
        val next = orderedCategories[currentIndex + 1]
        categoryDao.updateSortOrder(
            categoryId = current.id,
            sortOrder = next.sortOrder,
        )
        categoryDao.updateSortOrder(
            categoryId = next.id,
            sortOrder = current.sortOrder,
        )
    }

    override suspend fun reorderCategories(categoryIds: List<Long>) {
        categoryIds.forEachIndexed { index, categoryId ->
            categoryDao.updateSortOrder(
                categoryId = categoryId,
                sortOrder = index,
            )
        }
    }

    private companion object {
        const val CUSTOM_CATEGORY_COLOR_HEX = "#7F3DFF"

        val DEFAULT_CATEGORIES = listOf(
            DefaultCategory(name = "Food", colorHex = "#FD3C4A"),
            DefaultCategory(name = "Transport", colorHex = "#0077FF"),
            DefaultCategory(name = "Shopping", colorHex = "#FCAC12"),
            DefaultCategory(name = "Bills", colorHex = "#FD5662"),
            DefaultCategory(name = "Rent", colorHex = "#8F57FF"),
            DefaultCategory(name = "Health", colorHex = "#00A86B"),
            DefaultCategory(name = "Entertainment", colorHex = "#57A5FF"),
            DefaultCategory(name = "Education", colorHex = "#FCBB3C"),
            DefaultCategory(name = "Subscription", colorHex = "#2AB784"),
            DefaultCategory(name = "Others", colorHex = "#91919F"),
        )
    }
}

private data class DefaultCategory(
    val name: String,
    val colorHex: String,
)

private fun CategoryEntity.toDomain(): ExpenseCategory = ExpenseCategory(
    id = id,
    name = name,
    colorHex = colorHex,
    sortOrder = sortOrder,
    isDefault = isDefault,
)
