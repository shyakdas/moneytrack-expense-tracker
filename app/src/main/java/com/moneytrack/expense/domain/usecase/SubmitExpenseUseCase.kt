// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.usecase

import com.moneytrack.expense.domain.model.ExpenseSubmissionResult
import com.moneytrack.expense.domain.model.SubmitExpenseRequest
import com.moneytrack.expense.domain.repository.ExpenseRepository
import javax.inject.Inject

class SubmitExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
) {
    suspend operator fun invoke(
        request: SubmitExpenseRequest,
    ): ExpenseSubmissionResult = expenseRepository.submitExpense(request)
}
