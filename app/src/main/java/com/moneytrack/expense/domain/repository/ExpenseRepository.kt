// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.repository

import com.moneytrack.expense.domain.model.ExpenseSubmissionResult
import com.moneytrack.expense.domain.model.RecurringExpenseSchedule
import com.moneytrack.expense.domain.model.SubmitExpenseRequest

interface ExpenseRepository {
    suspend fun submitExpense(request: SubmitExpenseRequest): ExpenseSubmissionResult
    suspend fun processRecurringExpense(recurringExpenseId: Long): RecurringExpenseSchedule?
    suspend fun getActiveRecurringSchedules(): List<RecurringExpenseSchedule>
}
