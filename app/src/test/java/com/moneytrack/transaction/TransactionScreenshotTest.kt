// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.transaction.presentation.TransactionItemUiState
import com.moneytrack.transaction.presentation.TransactionScreen
import com.moneytrack.transaction.presentation.TransactionSectionUiState
import com.moneytrack.transaction.presentation.TransactionUiState
import org.junit.Rule
import org.junit.Test
import ui.components.card.transaction.TransactionType
import ui.theme.MoneyTrackTheme

class TransactionScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun transaction_empty_light() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                TransactionScreen(
                    uiState = TransactionUiState(sections = emptyList()),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                )
            }
        }
    }

    @Test
    fun transaction_content_dark() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                TransactionScreen(
                    uiState = TransactionUiState(
                        sections = listOf(
                            TransactionSectionUiState(
                                title = "Today",
                                items = listOf(
                                    TransactionItemUiState(
                                        id = 1L,
                                        iconRes = com.moneytrack.designsystem.R.drawable.shopping_bag,
                                        category = "Shopping",
                                        title = "Shopping",
                                        subtitle = "Buy some grocery",
                                        amount = "-₹120",
                                        date = "17 May 2025",
                                        time = "10:00 AM",
                                        type = TransactionType.EXPENSE,
                                    ),
                                    TransactionItemUiState(
                                        id = 2L,
                                        iconRes = com.moneytrack.designsystem.R.drawable.recurring_bill,
                                        category = "Subscription",
                                        title = "Subscription",
                                        subtitle = null,
                                        amount = "-₹80",
                                        date = "17 May 2025",
                                        time = "03:30 PM",
                                        type = TransactionType.EXPENSE,
                                    ),
                                ),
                            ),
                            TransactionSectionUiState(
                                title = "Yesterday",
                                items = listOf(
                                    TransactionItemUiState(
                                        id = 3L,
                                        iconRes = com.moneytrack.designsystem.R.drawable.salary,
                                        category = "Salary",
                                        title = "Salary",
                                        subtitle = "Salary for July",
                                        amount = "₹5,000",
                                        date = "16 May 2025",
                                        time = "04:30 PM",
                                        type = TransactionType.INCOME,
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
    }
}
