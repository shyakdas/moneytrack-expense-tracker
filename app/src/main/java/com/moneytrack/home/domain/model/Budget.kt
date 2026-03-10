// Copyright (c) 2026 shyakdas

package com.moneytrack.home.domain.model

data class Budget(
    val amount: Double,
    val description: String?,
    val updatedAtEpochMillis: Long,
)
