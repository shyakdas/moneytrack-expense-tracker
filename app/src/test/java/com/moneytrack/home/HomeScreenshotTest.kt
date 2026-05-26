// Copyright (c) 2026 shyakdas

package com.moneytrack.home

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.home.presentation.HomeScreen
import com.moneytrack.home.presentation.currentHomeMonthOption
import com.moneytrack.home.presentation.homeMonthOptions
import com.moneytrack.home.presentation.homeYearOptions
import com.moneytrack.home.presentation.HomeTransaction
import com.moneytrack.home.presentation.HomeUiState
import org.junit.Rule
import org.junit.Test
import ui.components.card.transaction.TransactionType
import ui.theme.MoneyTrackTheme

class HomeScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun home_empty_light() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                HomeScreen(
                    uiState = HomeUiState(
                        accountBalanceText = "₹0",
                        hasBudget = false,
                        budgetAmount = null,
                        budgetText = null,
                        hasExpenses = false,
                        expensesAmount = 0.0,
                        expensesText = "₹0",
                        spendFrequencyPoints = emptyList(),
                        hasSpendFrequencyData = false,
                        transactions = emptyList(),
                        selectedBottomRoute = "home",
                        selectedRange = "Today",
                        selectedMonth = currentHomeMonthOption(),
                        monthOptions = homeMonthOptions(),
                        yearOptions = homeYearOptions(),
                    ),
                    onBottomRouteSelected = {},
                    onSeeAllTransactionsClick = {},
                    onExpensesClick = {},
                    onTransactionCardClick = {},
                    onDeleteTransaction = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }
    }

    @Test
    fun home_content_dark() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                HomeScreen(
                    uiState = HomeUiState(
                        accountBalanceText = "₹38,800",
                        hasBudget = true,
                        budgetAmount = 40000.0,
                        budgetText = "₹40,000",
                        hasExpenses = true,
                        expensesAmount = 1200.0,
                        expensesText = "₹1,200",
                        spendFrequencyPoints = listOf(120f, 280f, 200f, 360f, 240f, 420f, 180f),
                        hasSpendFrequencyData = true,
                        transactions = listOf(
                            HomeTransaction(
                                id = 1L,
                                icon = com.moneytrack.designsystem.R.drawable.shopping_bag,
                                category = "Shopping",
                                title = "Shopping",
                                subtitle = "Buy some grocery",
                                amount = "-₹120",
                                date = "17 May 2025",
                                time = "10:00 AM",
                                type = TransactionType.EXPENSE,
                            ),
                            HomeTransaction(
                                id = 2L,
                                icon = com.moneytrack.designsystem.R.drawable.restaurant,
                                category = "Food",
                                title = "Food",
                                subtitle = null,
                                amount = "-₹32",
                                date = "17 May 2025",
                                time = "07:30 PM",
                                type = TransactionType.EXPENSE,
                            ),
                        ),
                        selectedBottomRoute = "home",
                        selectedRange = "Week",
                        selectedMonth = currentHomeMonthOption(),
                        monthOptions = homeMonthOptions(),
                        yearOptions = homeYearOptions(),
                    ),
                    onBottomRouteSelected = {},
                    onSeeAllTransactionsClick = {},
                    onExpensesClick = {},
                    onTransactionCardClick = {},
                    onDeleteTransaction = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }
    }
}
