// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.presentation

import androidx.compose.runtime.Composable

@Composable
fun ExpenseRoute(
    onBackClick: () -> Unit,
) {
    ExpenseScreen(
        onBackClick = onBackClick,
        onContinueClick = {},
    )
}

