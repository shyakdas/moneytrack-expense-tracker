// Copyright (c) 2026 shyakdas

package com.moneytrack.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.profile.presentation.ProfileActionCallbacks
import com.moneytrack.profile.presentation.ProfileNavigationCallbacks
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
                    navigationCallbacks = baseNavigationCallbacks(),
                    actionCallbacks = baseActionCallbacks(),
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
                    navigationCallbacks = baseNavigationCallbacks(),
                    actionCallbacks = baseActionCallbacks(
                        onEditClick = { editClicks++ },
                    ),
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
                    uiState = baseState().copy(
                        isEditSheetVisible = true,
                        editName = "Nova",
                        isSaveEnabled = true,
                    ),
                    navigationCallbacks = baseNavigationCallbacks(),
                    actionCallbacks = baseActionCallbacks(),
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
                    navigationCallbacks = baseNavigationCallbacks(),
                    actionCallbacks = baseActionCallbacks(
                        onClearDataClick = { clearDataClicks++ },
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Clear Data").performClick()

        assertEquals(1, clearDataClicks)
    }

    @Test
    fun exportRow_clickCallsCallback() {
        var exportClicks = 0
        composeRule.setContent {
            MoneyTrackTheme {
                ProfileScreen(
                    uiState = baseState(),
                    navigationCallbacks = baseNavigationCallbacks(),
                    actionCallbacks = baseActionCallbacks(
                        onExportClick = { exportClicks++ },
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Export Data").performClick()

        assertEquals(1, exportClicks)
    }

    @Test
    fun clearDataSheet_buttonsTriggerCallbacks() {
        var dismissClicks = 0
        var confirmClicks = 0
        composeRule.setContent {
            MoneyTrackTheme {
                ProfileScreen(
                    uiState = baseState().copy(isClearDataSheetVisible = true),
                    navigationCallbacks = baseNavigationCallbacks(),
                    actionCallbacks = baseActionCallbacks(
                        onDismissClearDataSheet = { dismissClicks++ },
                        onConfirmClearData = { confirmClicks++ },
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Clear all local data?").assertIsDisplayed()
        composeRule.onNodeWithText("No").performClick()
        composeRule.onNodeWithText("Yes").performClick()

        assertEquals(1, dismissClicks)
        assertEquals(1, confirmClicks)
    }

    private fun baseState(): ProfileUiState = ProfileUiState(
        name = "Saver",
        editName = "",
        isEditSheetVisible = false,
        isClearDataSheetVisible = false,
        clearDataCompleted = false,
        isSaveEnabled = false,
    )

    private fun baseNavigationCallbacks(): ProfileNavigationCallbacks = ProfileNavigationCallbacks(
        onBottomRouteClick = {},
        onAddExpenseClick = {},
    )

    private fun baseActionCallbacks(
        onEditClick: () -> Unit = {},
        onExportClick: () -> Unit = {},
        onDismissEditSheet: () -> Unit = {},
        onEditNameChanged: (String) -> Unit = {},
        onSaveName: () -> Unit = {},
        onClearDataClick: () -> Unit = {},
        onDismissClearDataSheet: () -> Unit = {},
        onConfirmClearData: () -> Unit = {},
    ): ProfileActionCallbacks = ProfileActionCallbacks(
        onEditClick = onEditClick,
        onSettingsClick = {},
        onExportClick = onExportClick,
        onDismissEditSheet = onDismissEditSheet,
        onEditNameChanged = onEditNameChanged,
        onSaveName = onSaveName,
        onClearDataClick = onClearDataClick,
        onDismissClearDataSheet = onDismissClearDataSheet,
        onConfirmClearData = onConfirmClearData,
    )
}
