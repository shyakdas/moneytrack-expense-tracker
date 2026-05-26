// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.data.repository

import com.moneytrack.data.local.db.dao.TransactionDao
import com.moneytrack.data.local.db.dao.RecurringExpenseDao
import com.moneytrack.data.local.db.entity.RecurringExpenseEntity
import com.moneytrack.data.local.db.entity.TransactionEntity
import com.moneytrack.expense.domain.model.RepeatFrequency
import com.moneytrack.expense.domain.model.RepeatSchedule
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.transaction.domain.repository.TransactionRepository
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val recurringExpenseDao: RecurringExpenseDao,
) : TransactionRepository {

    override fun observeTransactions(): Flow<List<TransactionRecord>> =
        transactionDao.observeTransactions().map { transactions ->
            transactions.map(TransactionEntity::toDomain)
        }

    override fun observeRecentTransactions(limit: Int): Flow<List<TransactionRecord>> =
        transactionDao.observeRecentTransactions(limit = limit).map { transactions ->
            transactions.map(TransactionEntity::toDomain)
        }

    override suspend fun getTransactionsFrom(fromEpochMillis: Long): List<TransactionRecord> =
        transactionDao.getTransactionsFrom(fromEpochMillis = fromEpochMillis)
            .map(TransactionEntity::toDomain)

    override suspend fun getTransactionById(id: Long): TransactionRecord? =
        transactionDao.getById(id)?.toDomain()

    override suspend fun getRepeatScheduleForTransaction(id: Long): RepeatSchedule? {
        val recurringExpense = recurringExpenseDao.getBySourceTransactionId(id) ?: return null
        return RepeatSchedule(
            frequency = RepeatFrequency.valueOf(recurringExpense.frequency),
            endAtEpochMillis = recurringExpense.endAtEpochMillis,
        )
    }

    override suspend fun updateExpenseTransaction(
        id: Long,
        amount: Double,
        note: String?,
        category: String,
        attachmentUri: String?,
        attachmentName: String?,
        attachmentType: String?,
        occurredAtEpochMillis: Long,
        repeatSchedule: RepeatSchedule?,
    ) {
        transactionDao.updateExpenseById(
            id = id,
            title = category,
            note = note?.takeIf(String::isNotBlank),
            amount = amount,
            category = category,
            attachmentUri = attachmentUri?.takeIf(String::isNotBlank),
            attachmentName = attachmentName?.takeIf(String::isNotBlank),
            attachmentType = attachmentType?.takeIf(String::isNotBlank),
            occurredAtEpochMillis = occurredAtEpochMillis,
        )

        recurringExpenseDao.deleteBySourceTransactionId(sourceTransactionId = id)
        if (repeatSchedule != null) {
            val nextRunAtEpochMillis = calculateNextRunAt(
                fromEpochMillis = occurredAtEpochMillis,
                frequency = repeatSchedule.frequency,
            )
            if (nextRunAtEpochMillis <= repeatSchedule.endAtEpochMillis) {
                recurringExpenseDao.insert(
                    RecurringExpenseEntity(
                        amount = amount,
                        note = note?.takeIf(String::isNotBlank),
                        category = category,
                        frequency = repeatSchedule.frequency.name,
                        endAtEpochMillis = repeatSchedule.endAtEpochMillis,
                        nextRunAtEpochMillis = nextRunAtEpochMillis,
                        createdAtEpochMillis = System.currentTimeMillis(),
                        sourceTransactionId = id,
                    ),
                )
            }
        }
    }

    override suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteById(id)
    }
}

private fun calculateNextRunAt(
    fromEpochMillis: Long,
    frequency: RepeatFrequency,
): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = fromEpochMillis
    }
    when (frequency) {
        RepeatFrequency.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
        RepeatFrequency.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
        RepeatFrequency.MONTHLY -> calendar.add(Calendar.MONTH, 1)
        RepeatFrequency.YEARLY -> calendar.add(Calendar.YEAR, 1)
    }
    return calendar.timeInMillis
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
    attachmentUri = attachmentUri,
    attachmentName = attachmentName,
    attachmentType = attachmentType,
    occurredAtEpochMillis = occurredAtEpochMillis,
)

private const val EXPENSE_TYPE = "expense"
