// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.settings.presentation.AboutScreen
import com.moneytrack.settings.presentation.AboutUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class AboutScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun aboutContent_isDisplayed() {
        composeRule.setContent {
            MoneyTrackTheme {
                AboutScreen(
                    uiState = AboutUiState(versionName = "1.0.0-dev"),
                    onBackClick = {},
                )
            }
        }

        composeRule.onNodeWithText("MoneyTrack").assertIsDisplayed()
        composeRule.onNodeWithText("Version").assertIsDisplayed()
        composeRule.onNodeWithText("Privacy").assertIsDisplayed()
        composeRule.onNodeWithText("Data on your device").assertIsDisplayed()
        composeRule.onNodeWithText("Helpful reminders").assertIsDisplayed()
    }
}
