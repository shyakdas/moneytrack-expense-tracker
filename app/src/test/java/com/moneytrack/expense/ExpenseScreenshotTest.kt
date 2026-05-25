// Copyright (c) 2026 shyakdas

package com.moneytrack.expense

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.runtime.Composable
import com.moneytrack.expense.domain.model.ExpenseCategory
import com.moneytrack.expense.domain.model.RepeatFrequency
import com.moneytrack.expense.presentation.ExpenseAttachmentType
import com.moneytrack.expense.presentation.ExpenseAttachmentUiState
import com.moneytrack.expense.presentation.ExpenseContent
import com.moneytrack.expense.presentation.ExpenseRepeatUiState
import com.moneytrack.expense.presentation.ExpenseUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class ExpenseScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun expense_empty_light() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                ExpenseSnapshotContent(
                    uiState = ExpenseUiState(
                        amountText = "$0",
                        isSubmitEnabled = false,
                    ),
                )
            }
        }
    }

    @Test
    fun expense_configured_dark() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                ExpenseSnapshotContent(
                    uiState = ExpenseUiState(
                        amountInput = "5200",
                        amountText = "$5,200",
                        isSubmitEnabled = true,
                        description = "Monthly plan",
                        attachment = ExpenseAttachmentUiState(
                            uriString = "content://receipt",
                            name = "invoice.pdf",
                            type = ExpenseAttachmentType.DOCUMENT,
                        ),
                        repeatSchedule = ExpenseRepeatUiState(
                            frequency = RepeatFrequency.MONTHLY,
                            endAtEpochMillis = 1_767_225_599_999L,
                        ),
                        selectedCategory = ExpenseCategory(
                            id = 1L,
                            name = "Subscription",
                            colorHex = "#7F3DFF",
                            sortOrder = 0,
                            isDefault = true,
                        ),
                    ),
                )
            }
        }
    }
}

@Composable
private fun ExpenseSnapshotContent(
    uiState: ExpenseUiState,
) {
    ExpenseContent(
        amountInput = uiState.amountInput,
        amountText = uiState.amountText,
        description = uiState.description,
        isSubmitEnabled = uiState.isSubmitEnabled,
        attachment = uiState.attachment,
        repeatSchedule = uiState.repeatSchedule,
        selectedCategory = uiState.selectedCategory,
        onBackClick = {},
        onContinueClick = {},
        onAmountChanged = {},
        onDescriptionChanged = {},
        onAttachmentClick = {},
        onAttachmentRemoved = {},
        onRepeatClick = {},
        onRepeatEnabledChange = {},
        onCategoryFieldClick = {},
        occurredAtEpochMillis = uiState.occurredAtEpochMillis,
        onOccurredAtChanged = {},
    )
}
