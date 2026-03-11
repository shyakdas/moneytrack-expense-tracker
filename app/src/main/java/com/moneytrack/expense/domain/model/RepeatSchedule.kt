// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.model

data class RepeatSchedule(
    val frequency: RepeatFrequency,
    val endAtEpochMillis: Long,
)
