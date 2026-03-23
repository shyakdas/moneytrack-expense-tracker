// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.settings.presentation.NotificationScreen
import com.moneytrack.settings.presentation.NotificationUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class NotificationScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun allNotificationOptions_areDisplayed() {
        composeRule.setContent {
            MoneyTrackTheme {
                NotificationScreen(
                    uiState = NotificationUiState(selectedNotificationsPerDay = 3),
                    onBackClick = {},
                    onNotificationCountSelected = {},
                )
            }
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()
        composeRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun tappingNotificationOption_callsSelectionCallback() {
        var selectedNotificationsPerDay: Int? = null

        composeRule.setContent {
            MoneyTrackTheme {
                NotificationScreen(
                    uiState = NotificationUiState(selectedNotificationsPerDay = 3),
                    onBackClick = {},
                    onNotificationCountSelected = { selectedNotificationsPerDay = it },
                )
            }
        }

        composeRule.onNodeWithText("5").performClick()

        assertEquals(5, selectedNotificationsPerDay)
    }
}
