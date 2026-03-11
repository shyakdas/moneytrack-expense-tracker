// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.transaction.presentation.TransactionItemUiState
import com.moneytrack.transaction.presentation.TransactionScreen
import com.moneytrack.transaction.presentation.TransactionSectionUiState
import com.moneytrack.transaction.presentation.TransactionUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.components.card.transaction.TransactionType
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class TransactionScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyState_isDisplayed() {
        composeRule.setContent {
            MoneyTrackTheme {
                TransactionScreen(
                    uiState = TransactionUiState(sections = emptyList()),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                )
            }
        }

        composeRule.onNodeWithText("No transactions yet").assertIsDisplayed()
        composeRule.onNodeWithText("Your saved expenses will appear here.").assertIsDisplayed()
        composeRule.onNodeWithText("See your financial report").assertIsDisplayed()
    }

    @Test
    fun populatedState_showsSectionsAndRows() {
        composeRule.setContent {
            MoneyTrackTheme {
                TransactionScreen(
                    uiState = populatedState(),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Today").assertIsDisplayed()
        composeRule.onNodeWithText("Yesterday").assertIsDisplayed()
        composeRule.onNodeWithText("Shopping").assertIsDisplayed()
        composeRule.onNodeWithText("Salary").assertIsDisplayed()
    }

    @Test
    fun missingSubtitle_isNotShown() {
        composeRule.setContent {
            MoneyTrackTheme {
                TransactionScreen(
                    uiState = TransactionUiState(
                        sections = listOf(
                            TransactionSectionUiState(
                                title = "Today",
                                items = listOf(
                                    TransactionItemUiState(
                                        id = 1L,
                                        iconRes = com.moneytrack.designsystem.R.drawable.expense,
                                        title = "Expense",
                                        subtitle = null,
                                        amount = "-₹120",
                                        time = "10:00 AM",
                                        type = TransactionType.EXPENSE,
                                    ),
                                ),
                            ),
                        ),
                    ),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Expense").assertIsDisplayed()
        composeRule.onNodeWithText("10:00 AM").assertIsDisplayed()
        composeRule.onAllNodesWithText("No note added").assertCountEquals(0)
    }

    @Test
    fun homeBottomNav_clickCallsCallback() {
        var selectedRoute: String? = null
        composeRule.setContent {
            MoneyTrackTheme {
                TransactionScreen(
                    uiState = populatedState(),
                    onBottomRouteClick = { route -> selectedRoute = route },
                    onAddExpenseClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Home").performClick()

        assertEquals("home", selectedRoute)
    }

    private fun populatedState(): TransactionUiState = TransactionUiState(
        sections = listOf(
            TransactionSectionUiState(
                title = "Today",
                items = listOf(
                    TransactionItemUiState(
                        id = 1L,
                        iconRes = com.moneytrack.designsystem.R.drawable.shopping_bag,
                        title = "Shopping",
                        subtitle = "Buy some grocery",
                        amount = "-₹120",
                        time = "10:00 AM",
                        type = TransactionType.EXPENSE,
                    ),
                ),
            ),
            TransactionSectionUiState(
                title = "Yesterday",
                items = listOf(
                    TransactionItemUiState(
                        id = 2L,
                        iconRes = com.moneytrack.designsystem.R.drawable.salary,
                        title = "Salary",
                        subtitle = "Salary for July",
                        amount = "₹5,000",
                        time = "04:30 PM",
                        type = TransactionType.INCOME,
                    ),
                ),
            ),
        ),
    )
}
