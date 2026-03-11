// Copyright (c) 2026 shyakdas

package com.moneytrack.expense

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.expense.domain.model.ExpenseCategory
import com.moneytrack.expense.domain.model.RepeatFrequency
import com.moneytrack.expense.presentation.ExpenseAttachmentType
import com.moneytrack.expense.presentation.ExpenseAttachmentUiState
import com.moneytrack.expense.presentation.ExpenseRepeatUiState
import com.moneytrack.expense.presentation.ExpenseScreen
import com.moneytrack.expense.presentation.ExpenseUiState
import java.text.SimpleDateFormat
import java.util.Locale
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class ExpenseScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyExpenseState_showsOptionalFields_andDisablesContinue() {
        composeRule.setContent {
            MoneyTrackTheme {
                ExpenseScreen(
                    uiState = ExpenseUiState(
                        amountText = "$0",
                        isSubmitEnabled = false,
                    ),
                    onBackClick = {},
                    onContinueClick = {},
                    onAmountChanged = {},
                    onDescriptionChanged = {},
                    onAttachmentSelected = { _, _, _ -> },
                    onAttachmentRemoved = {},
                    onRepeatConfigured = { _, _ -> },
                    onRepeatRemoved = {},
                    onCategorySelected = {},
                )
            }
        }

        composeRule.onNodeWithText("Category").assertIsDisplayed()
        composeRule.onNodeWithText("Description").assertIsDisplayed()
        composeRule.onNodeWithText("Add attachment").assertIsDisplayed()
        composeRule.onNodeWithText("Repeat").assertIsDisplayed()
        composeRule.onNodeWithText("Continue").assertIsNotEnabled()
    }

    @Test
    fun amountReady_continueIsEnabled_andInvokesCallback() {
        var continueClicked = false

        composeRule.setContent {
            MoneyTrackTheme {
                ExpenseScreen(
                    uiState = ExpenseUiState(
                        amountInput = "2400",
                        amountText = "$2,400",
                        isSubmitEnabled = true,
                    ),
                    onBackClick = {},
                    onContinueClick = { continueClicked = true },
                    onAmountChanged = {},
                    onDescriptionChanged = {},
                    onAttachmentSelected = { _, _, _ -> },
                    onAttachmentRemoved = {},
                    onRepeatConfigured = { _, _ -> },
                    onRepeatRemoved = {},
                    onCategorySelected = {},
                )
            }
        }

        composeRule.onNodeWithText("Continue").assertIsEnabled().performClick()
        assertTrue(continueClicked)
    }

    @Test
    fun configuredExpenseState_showsCategoryAttachmentAndRepeatSummary() {
        val endAtEpochMillis = 1_767_225_599_999L
        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(endAtEpochMillis)

        composeRule.setContent {
            MoneyTrackTheme {
                ExpenseScreen(
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
                            endAtEpochMillis = endAtEpochMillis,
                        ),
                        selectedCategory = ExpenseCategory(
                            id = 1L,
                            name = "Subscription",
                            colorHex = "#7F3DFF",
                            sortOrder = 0,
                            isDefault = true,
                        ),
                    ),
                    onBackClick = {},
                    onContinueClick = {},
                    onAmountChanged = {},
                    onDescriptionChanged = {},
                    onAttachmentSelected = { _, _, _ -> },
                    onAttachmentRemoved = {},
                    onRepeatConfigured = { _, _ -> },
                    onRepeatRemoved = {},
                    onCategorySelected = {},
                )
            }
        }

        composeRule.onNodeWithText("Subscription").assertIsDisplayed()
        composeRule.onNodeWithText("invoice.pdf").assertIsDisplayed()
        composeRule.onNodeWithText("Monthly").assertIsDisplayed()
        composeRule.onNodeWithText(formattedDate).assertIsDisplayed()
        composeRule.onNodeWithText("Edit").assertIsDisplayed()
    }
}
