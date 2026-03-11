// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.usecase

import com.moneytrack.expense.domain.model.RecurringExpenseSchedule
import com.moneytrack.expense.domain.repository.ExpenseRepository
import javax.inject.Inject

class ProcessRecurringExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
) {
    suspend operator fun invoke(
        recurringExpenseId: Long,
    ): RecurringExpenseSchedule? = expenseRepository.processRecurringExpense(recurringExpenseId)
}
