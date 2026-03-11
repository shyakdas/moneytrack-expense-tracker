// Copyright (c) 2026 shyakdas

package com.moneytrack.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
                                amount = "-$120",
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

    @Test
    fun inrAmounts_areDisplayedInHomeSummary() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = true,
                        hasBudget = true,
                        accountBalanceText = "₹2,50,000",
                        budgetText = "₹1,00,000",
                        expensesText = "₹12,500",
                    ),
                    onBottomRouteSelected = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("₹2,50,000").assertIsDisplayed()
        composeRule.onNodeWithText("₹1,00,000").assertIsDisplayed()
        composeRule.onNodeWithText("₹12,500").assertIsDisplayed()
    }

    @Test
    fun usdAmounts_areDisplayedInHomeSummary() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = true,
                        hasBudget = true,
                        accountBalanceText = "$250,000",
                        budgetText = "$100,000",
                        expensesText = "$12,500",
                    ),
                    onBottomRouteSelected = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("$250,000").assertIsDisplayed()
        composeRule.onNodeWithText("$100,000").assertIsDisplayed()
        composeRule.onNodeWithText("$12,500").assertIsDisplayed()
    }

    @Test
    fun budgetLoading_doesNotShowSetBudgetNowCta() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(hasExpenses = false),
                    isBudgetLoaded = false,
                    onBottomRouteSelected = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onAllNodesWithText("Set budget now").assertCountEquals(0)
    }

    private fun baseState(
        hasExpenses: Boolean,
        hasBudget: Boolean = false,
        accountBalanceText: String = "$0",
        budgetText: String? = null,
        expensesText: String = "$0",
        transactions: List<HomeTransaction> = emptyList(),
    ): HomeUiState {
        return HomeUiState(
            accountBalanceText = accountBalanceText,
            hasBudget = hasBudget,
            budgetAmount = null,
            budgetText = budgetText,
            hasExpenses = hasExpenses,
            expensesText = expensesText,
            transactions = transactions,
            selectedBottomRoute = "home",
            selectedRange = "Today",
        )
    }
}
