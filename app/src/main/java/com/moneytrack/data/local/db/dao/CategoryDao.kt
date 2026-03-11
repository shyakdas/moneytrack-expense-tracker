// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moneytrack.data.local.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY sort_order ASC, id ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun countCategories(): Int

    @Query("SELECT MAX(sort_order) FROM categories")
    suspend fun maxSortOrder(): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories ORDER BY sort_order ASC, id ASC")
    suspend fun getOrderedCategories(): List<CategoryEntity>

    @Query("UPDATE categories SET sort_order = :sortOrder WHERE id = :categoryId")
    suspend fun updateSortOrder(
        categoryId: Long,
        sortOrder: Int,
    )
}

