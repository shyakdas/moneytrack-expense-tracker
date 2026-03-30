// Copyright (c) 2026 shyakdas

package com.moneytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moneytrack.data.local.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY occurred_at_epoch_millis DESC")
    fun observeTransactions(): Flow<List<TransactionEntity>>

    @Query(
        "SELECT * FROM transactions " +
            "ORDER BY occurred_at_epoch_millis DESC " +
            "LIMIT :limit",
    )
    fun observeRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TransactionEntity?

    @Query(
        "SELECT * FROM transactions " +
            "WHERE occurred_at_epoch_millis >= :fromEpochMillis " +
            "ORDER BY occurred_at_epoch_millis DESC",
    )
    suspend fun getTransactionsFrom(fromEpochMillis: Long): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}
