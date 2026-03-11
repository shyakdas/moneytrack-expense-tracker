// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.data.repository

import com.moneytrack.data.local.db.dao.TransactionDao
import com.moneytrack.data.local.db.entity.TransactionEntity
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.transaction.domain.repository.TransactionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
) : TransactionRepository {

    override fun observeTransactions(): Flow<List<TransactionRecord>> =
        transactionDao.observeTransactions().map { transactions ->
            transactions.map(TransactionEntity::toDomain)
        }
}

private fun TransactionEntity.toDomain(): TransactionRecord = TransactionRecord(
    id = id,
    title = title,
    note = note,
    amount = amount,
    type = if (type.equals(EXPENSE_TYPE, ignoreCase = true)) {
        TransactionRecordType.EXPENSE
    } else {
        TransactionRecordType.INCOME
    },
    category = category,
    occurredAtEpochMillis = occurredAtEpochMillis,
)

private const val EXPENSE_TYPE = "expense"
