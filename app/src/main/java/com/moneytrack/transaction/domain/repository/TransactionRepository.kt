// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.domain.repository

import com.moneytrack.transaction.domain.model.TransactionRecord
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(): Flow<List<TransactionRecord>>
    fun observeRecentTransactions(limit: Int): Flow<List<TransactionRecord>>
    suspend fun getTransactionsFrom(fromEpochMillis: Long): List<TransactionRecord>
}
