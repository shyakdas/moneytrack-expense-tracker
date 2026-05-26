// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.domain.repository

import com.moneytrack.transaction.domain.model.TransactionRecord
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(): Flow<List<TransactionRecord>>
    fun observeRecentTransactions(limit: Int): Flow<List<TransactionRecord>>
    suspend fun getTransactionsFrom(fromEpochMillis: Long): List<TransactionRecord>
    suspend fun getTransactionById(id: Long): TransactionRecord? = null
    suspend fun updateExpenseTransaction(
        id: Long,
        amount: Double,
        note: String?,
        category: String,
        occurredAtEpochMillis: Long,
    ) = Unit
    suspend fun deleteTransaction(id: Long) = Unit
}
