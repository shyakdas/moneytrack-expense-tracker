// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import ui.components.card.transaction.TransactionType

data class HomeTransaction(
    val icon: Int,
    val title: String,
    val subtitle: String?,
    val amount: String,
    val time: String,
    val type: TransactionType,
)

data class HomeUiState(
    val accountBalanceText: String,
    val hasBudget: Boolean,
    val budgetAmount: Double?,
    val budgetText: String?,
    val hasExpenses: Boolean,
    val expensesText: String,
    val spendFrequencyPoints: List<Float>,
    val hasSpendFrequencyData: Boolean,
    val transactions: List<HomeTransaction>,
    val selectedBottomRoute: String,
    val selectedRange: String,
    val selectedMonth: HomeMonthOption,
    val monthOptions: List<HomeMonthOption>,
)
