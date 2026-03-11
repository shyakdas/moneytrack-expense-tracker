// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.model

data class RecurringExpenseSchedule(
    val id: Long,
    val nextRunAtEpochMillis: Long,
)
