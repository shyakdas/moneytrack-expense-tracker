// Copyright (c) 2026 shyakdas

package com.moneytrack.home

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.home.presentation.HomeScreen
import com.moneytrack.home.presentation.HomeUiState
import com.moneytrack.home.presentation.HomeTransaction
import org.junit.Assert.assertEquals
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
    fun noSpendFrequencyData_showsEmptyMessageAndTabs() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = false,
                        hasSpendFrequencyData = false,
                    ),
                    onBottomRouteSelected = {},
                    onSeeAllTransactionsClick = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Add expenses to view spend frequency.").assertIsDisplayed()
        composeRule.onNodeWithText("Today").assertIsDisplayed()
        composeRule.onNodeWithText("Week").assertIsDisplayed()
    }

    @Test
    fun noTransactions_showsRecentTransactionEmptyMessage() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = true,
                        hasSpendFrequencyData = true,
                        transactions = emptyList(),
                    ),
                    onBottomRouteSelected = {},
                    onSeeAllTransactionsClick = {},
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
                        hasSpendFrequencyData = true,
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
                    onSeeAllTransactionsClick = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Shopping").assertIsDisplayed()
        composeRule.onNodeWithText("Groceries").assertIsDisplayed()
    }

    @Test
    fun transactionWithoutSubtitle_hidesSubtitleLine() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = true,
                        hasSpendFrequencyData = true,
                        transactions = listOf(
                            HomeTransaction(
                                icon = com.moneytrack.designsystem.R.drawable.expense,
                                title = "Expense",
                                subtitle = null,
                                amount = "-₹120",
                                time = "10:00 AM",
                                type = TransactionType.EXPENSE,
                            ),
                        ),
                    ),
                    onBottomRouteSelected = {},
                    onSeeAllTransactionsClick = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Expense").assertIsDisplayed()
        composeRule.onAllNodesWithText("No note added").assertCountEquals(0)
    }

    @Test
    fun inrAmounts_areDisplayedInHomeSummary() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = true,
                        hasBudget = true,
                        hasSpendFrequencyData = true,
                        accountBalanceText = "₹2,50,000",
                        budgetText = "₹1,00,000",
                        expensesText = "₹12,500",
                    ),
                    onBottomRouteSelected = {},
                    onSeeAllTransactionsClick = {},
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
    fun seeAll_clickTriggersNavigationCallback() {
        var clickedCount = 0
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(
                        hasExpenses = true,
                        hasSpendFrequencyData = true,
                    ),
                    onBottomRouteSelected = {},
                    onSeeAllTransactionsClick = { clickedCount++ },
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }

        composeRule.onNodeWithText("See All").performClick()

        assertEquals(1, clickedCount)
    }

    @Test
    fun budgetLoading_doesNotShowSetBudgetNowCta() {
        composeRule.setContent {
            MoneyTrackTheme {
                HomeScreen(
                    uiState = baseState(hasExpenses = false),
                    isBudgetLoaded = false,
                    onBottomRouteSelected = {},
                    onSeeAllTransactionsClick = {},
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
        hasSpendFrequencyData: Boolean = false,
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
            spendFrequencyPoints = if (hasSpendFrequencyData) listOf(12f, 24f, 18f, 36f) else emptyList(),
            hasSpendFrequencyData = hasSpendFrequencyData,
            transactions = transactions,
            selectedBottomRoute = "home",
            selectedRange = "Today",
        )
    }
}
