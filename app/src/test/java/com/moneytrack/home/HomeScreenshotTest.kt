// Copyright (c) 2026 shyakdas

package com.moneytrack.home

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import com.moneytrack.home.presentation.HomeScreen
import com.moneytrack.home.presentation.HomeUiState
import com.moneytrack.home.presentation.HomeTransaction
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
                        accountBalanceText = "$0",
                        hasBudget = false,
                        budgetText = null,
                        hasExpenses = false,
                        expensesText = "$0",
                        transactions = emptyList(),
                        selectedBottomRoute = "home",
                        selectedRange = "Today",
                    ),
                    onBottomRouteSelected = {},
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
                        accountBalanceText = "$9,400",
                        hasBudget = true,
                        budgetText = "$40,000",
                        hasExpenses = true,
                        expensesText = "$1,200",
                        transactions = listOf(
                            HomeTransaction(
                                icon = com.moneytrack.designsystem.R.drawable.expense,
                                title = "Shopping",
                                subtitle = "Buy some grocery",
                                amount = "- $120",
                                time = "10:00 AM",
                                type = TransactionType.EXPENSE,
                            ),
                            HomeTransaction(
                                icon = com.moneytrack.designsystem.R.drawable.expense,
                                title = "Food",
                                subtitle = "Lunch",
                                amount = "- $32",
                                time = "07:30 PM",
                                type = TransactionType.EXPENSE,
                            ),
                        ),
                        selectedBottomRoute = "home",
                        selectedRange = "Today",
                    ),
                    onBottomRouteSelected = {},
                    onTimeRangeSelected = {},
                    onSetBudgetClick = {},
                )
            }
        }
    }
}
