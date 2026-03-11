// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.domain.model

data class ExpenseCategory(
    val id: Long,
    val name: String,
    val colorHex: String,
    val sortOrder: Int,
    val isDefault: Boolean,
)

