// Copyright (c) 2026 shyakdas

package com.moneytrack.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.home.presentation.HomeScreen
import com.moneytrack.home.presentation.HomeUiState
import com.moneytrack.home.presentation.HomeTransaction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.components.card.transaction.TransactionType
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class HomeScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun noExpenses_showsSpendFrequencyEmptyMessage() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(hasExpenses = false),
                    onBottomRouteSelected = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Add expenses to view spend frequency.").assertIsDisplayed()
    }

    @Test
    fun noTransactions_showsRecentTransactionEmptyMessage() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = true,
                        transactions = emptyList(),
                    ),
                    onBottomRouteSelected = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Add an expense to see recent transactions.").assertIsDisplayed()
    }

    @Test
    fun withTransactions_showsTransactionRows() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = true,
                        transactions = listOf(
                            HomeTransaction(
                                icon = com.moneytrack.designsystem.R.drawable.expense,
                                title = "Shopping",
                                subtitle = "Groceries",
                                amount = "- $120",
                                time = "10:00 AM",
                                type = TransactionType.EXPENSE,
                            ),
                        ),
                    ),
                    onBottomRouteSelected = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Shopping").assertIsDisplayed()
    }

    private fun baseState(
        hasExpenses: Boolean,
        transactions: List<HomeTransaction> = emptyList(),
    ): HomeUiState {
        return HomeUiState(
            accountBalanceText = "$0",
            hasBudget = false,
            budgetAmount = null,
            budgetText = null,
            hasExpenses = hasExpenses,
            expensesText = "$0",
            transactions = transactions,
            selectedBottomRoute = "home",
            selectedRange = "Today",
        )
    }
}
