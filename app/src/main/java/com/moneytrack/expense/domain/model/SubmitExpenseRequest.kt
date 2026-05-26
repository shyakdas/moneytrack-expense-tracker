// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.model

data class SubmitExpenseRequest(
    val amount: Double,
    val description: String?,
    val category: String,
    val occurredAtEpochMillis: Long,
    val attachmentUri: String?,
    val attachmentName: String?,
    val attachmentType: String?,
    val repeatSchedule: RepeatSchedule?,
)

data class ExpenseSubmissionResult(
    val recurringExpenseId: Long? = null,
    val nextRunAtEpochMillis: Long? = null,
)
