// Copyright (c) 2026 shyakdas

package com.moneytrack.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.profile.presentation.ProfileScreen
import com.moneytrack.profile.presentation.ProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class ProfileScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun defaultState_showsNameAndActions() {
        composeRule.setContent {
            MoneyTrackTheme {
                ProfileScreen(
                    uiState = baseState(),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                    onEditClick = {},
                    onDismissEditSheet = {},
                    onEditNameChanged = {},
                    onSaveName = {},
                    onClearDataClick = {},
                    onDismissClearDataSheet = {},
                    onConfirmClearData = {},
                )
            }
        }

        composeRule.onNodeWithText("Saver").assertIsDisplayed()
        composeRule.onNodeWithText("Settings").assertIsDisplayed()
        composeRule.onNodeWithText("Export Data").assertIsDisplayed()
        composeRule.onNodeWithText("Clear Data").assertIsDisplayed()
    }

    @Test
    fun editButton_clickCallsCallback() {
        var editClicks = 0
        composeRule.setContent {
            MoneyTrackTheme {
                ProfileScreen(
                    uiState = baseState(),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                    onEditClick = { editClicks++ },
                    onDismissEditSheet = {},
                    onEditNameChanged = {},
                    onSaveName = {},
                    onClearDataClick = {},
                    onDismissClearDataSheet = {},
                    onConfirmClearData = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Edit profile").performClick()

        assertEquals(1, editClicks)
    }

    @Test
    fun editSheet_showsFieldsAndSaveButton() {
        composeRule.setContent {
            MoneyTrackTheme {
                ProfileScreen(
                    uiState = baseState(
                        isEditSheetVisible = true,
                        editName = "Nova",
                        isSaveEnabled = true,
                    ),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                    onEditClick = {},
                    onDismissEditSheet = {},
                    onEditNameChanged = {},
                    onSaveName = {},
                    onClearDataClick = {},
                    onDismissClearDataSheet = {},
                    onConfirmClearData = {},
                )
            }
        }

        composeRule.onNodeWithText("Choose your name").assertIsDisplayed()
        composeRule.onNodeWithText("Pick the name you want to see on your MoneyTrack profile.").assertIsDisplayed()
        composeRule.onNodeWithText("Nova").assertIsDisplayed()
        composeRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun clearDataRow_clickCallsCallback() {
        var clearDataClicks = 0
        composeRule.setContent {
            MoneyTrackTheme {
                ProfileScreen(
                    uiState = baseState(),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                    onEditClick = {},
                    onDismissEditSheet = {},
                    onEditNameChanged = {},
                    onSaveName = {},
                    onClearDataClick = { clearDataClicks++ },
                    onDismissClearDataSheet = {},
                    onConfirmClearData = {},
                )
            }
        }

        composeRule.onNodeWithText("Clear Data").performClick()

        assertEquals(1, clearDataClicks)
    }

    @Test
    fun clearDataSheet_buttonsTriggerCallbacks() {
        var dismissClicks = 0
        var confirmClicks = 0
        composeRule.setContent {
            MoneyTrackTheme {
                ProfileScreen(
                    uiState = baseState(isClearDataSheetVisible = true),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                    onEditClick = {},
                    onDismissEditSheet = {},
                    onEditNameChanged = {},
                    onSaveName = {},
                    onClearDataClick = {},
                    onDismissClearDataSheet = { dismissClicks++ },
                    onConfirmClearData = { confirmClicks++ },
                )
            }
        }

        composeRule.onNodeWithText("Clear all local data?").assertIsDisplayed()
        composeRule.onNodeWithText("No").performClick()
        composeRule.onNodeWithText("Yes").performClick()

        assertEquals(1, dismissClicks)
        assertEquals(1, confirmClicks)
    }

    private fun baseState(
        name: String = "Saver",
        editName: String = "",
        isEditSheetVisible: Boolean = false,
        isClearDataSheetVisible: Boolean = false,
        clearDataCompleted: Boolean = false,
        isSaveEnabled: Boolean = false,
    ): ProfileUiState = ProfileUiState(
        name = name,
        editName = editName,
        isEditSheetVisible = isEditSheetVisible,
        isClearDataSheetVisible = isClearDataSheetVisible,
        clearDataCompleted = clearDataCompleted,
        isSaveEnabled = isSaveEnabled,
    )
}
