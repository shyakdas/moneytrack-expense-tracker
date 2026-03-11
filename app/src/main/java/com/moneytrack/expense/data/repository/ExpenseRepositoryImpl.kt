// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.data.repository

import com.moneytrack.data.local.db.dao.RecurringExpenseDao
import com.moneytrack.data.local.db.dao.TransactionDao
import com.moneytrack.data.local.db.entity.RecurringExpenseEntity
import com.moneytrack.data.local.db.entity.TransactionEntity
import com.moneytrack.expense.domain.model.ExpenseSubmissionResult
import com.moneytrack.expense.domain.model.RecurringExpenseSchedule
import com.moneytrack.expense.domain.model.RepeatFrequency
import com.moneytrack.expense.domain.model.RepeatSchedule
import com.moneytrack.expense.domain.model.SubmitExpenseRequest
import com.moneytrack.expense.domain.repository.ExpenseRepository
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val recurringExpenseDao: RecurringExpenseDao,
) : ExpenseRepository {

    override suspend fun submitExpense(request: SubmitExpenseRequest): ExpenseSubmissionResult {
        val now = System.currentTimeMillis()
        transactionDao.insert(
            request.toTransactionEntity(
                occurredAtEpochMillis = request.occurredAtEpochMillis,
                createdAtEpochMillis = now,
            ),
        )

        return createRecurringExpenseResult(
            request = request,
            createdAtEpochMillis = now,
        )
            ?: ExpenseSubmissionResult()
    }

    override suspend fun processRecurringExpense(recurringExpenseId: Long): RecurringExpenseSchedule? {
        val recurringExpense = recurringExpenseDao.getById(recurringExpenseId) ?: return null
        transactionDao.insert(
            recurringExpense.toTransactionEntity(
                occurredAtEpochMillis = recurringExpense.nextRunAtEpochMillis,
                createdAtEpochMillis = System.currentTimeMillis(),
            ),
        )
        val nextRunAtEpochMillis = calculateNextRunAt(
            fromEpochMillis = recurringExpense.nextRunAtEpochMillis,
            frequency = RepeatFrequency.valueOf(recurringExpense.frequency),
        )
        return if (nextRunAtEpochMillis <= recurringExpense.endAtEpochMillis) {
            recurringExpenseDao.updateNextRunAt(
                id = recurringExpense.id,
                nextRunAtEpochMillis = nextRunAtEpochMillis,
            )
            RecurringExpenseSchedule(
                id = recurringExpense.id,
                nextRunAtEpochMillis = nextRunAtEpochMillis,
            )
        } else {
            recurringExpenseDao.deleteById(recurringExpense.id)
            null
        }
    }

    override suspend fun getActiveRecurringSchedules(): List<RecurringExpenseSchedule> =
        recurringExpenseDao.getAll().map { recurringExpense ->
            RecurringExpenseSchedule(
                id = recurringExpense.id,
                nextRunAtEpochMillis = recurringExpense.nextRunAtEpochMillis,
            )
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

    private suspend fun createRecurringExpenseResult(
        request: SubmitExpenseRequest,
        createdAtEpochMillis: Long,
    ): ExpenseSubmissionResult? {
        val repeatSchedule = request.repeatSchedule ?: return null
        val nextRunAtEpochMillis = calculateNextRunAt(
            fromEpochMillis = request.occurredAtEpochMillis,
            frequency = repeatSchedule.frequency,
        )
        return if (nextRunAtEpochMillis <= repeatSchedule.endAtEpochMillis) {
            val recurringExpenseId = recurringExpenseDao.insert(
                RecurringExpenseEntity(
                    amount = request.amount,
                    note = request.description,
                    category = request.category,
                    frequency = repeatSchedule.frequency.name,
                    endAtEpochMillis = repeatSchedule.endAtEpochMillis,
                    nextRunAtEpochMillis = nextRunAtEpochMillis,
                    createdAtEpochMillis = createdAtEpochMillis,
                ),
            )
            ExpenseSubmissionResult(
                recurringExpenseId = recurringExpenseId,
                nextRunAtEpochMillis = nextRunAtEpochMillis,
            )
        } else {
            null
        }
    }
}

private fun SubmitExpenseRequest.toTransactionEntity(
    occurredAtEpochMillis: Long,
    createdAtEpochMillis: Long,
): TransactionEntity = TransactionEntity(
    title = category,
    note = description?.takeIf { it.isNotBlank() },
    amount = amount,
    type = EXPENSE_TYPE,
    category = category,
    occurredAtEpochMillis = occurredAtEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
)

private fun RecurringExpenseEntity.toTransactionEntity(
    occurredAtEpochMillis: Long,
    createdAtEpochMillis: Long,
): TransactionEntity = TransactionEntity(
    title = category,
    note = note?.takeIf { it.isNotBlank() },
    amount = amount,
    type = EXPENSE_TYPE,
    category = category,
    occurredAtEpochMillis = occurredAtEpochMillis,
    createdAtEpochMillis = createdAtEpochMillis,
)

private const val EXPENSE_TYPE = "expense"
