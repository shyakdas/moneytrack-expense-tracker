// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.settings.presentation.SecurityOption
import com.moneytrack.settings.presentation.SecurityScreen
import com.moneytrack.settings.presentation.SecurityUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class SecurityScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun allSecurityOptions_areDisplayed() {
        composeRule.setContent {
            MoneyTrackTheme {
                SecurityScreen(
                    uiState = SecurityUiState(selectedOption = SecurityOption.NONE),
                    onBackClick = {},
                    onOptionSelected = {},
                )
            }
        }

        composeRule.onNodeWithText("PIN").assertIsDisplayed()
        composeRule.onNodeWithText("Biometric").assertIsDisplayed()
        composeRule.onNodeWithText("None").assertIsDisplayed()
    }

    @Test
    fun tappingSecurityOption_callsSelectionCallback() {
        var selectedOption: SecurityOption? = null

        composeRule.setContent {
            MoneyTrackTheme {
                SecurityScreen(
                    uiState = SecurityUiState(selectedOption = SecurityOption.NONE),
                    onBackClick = {},
                    onOptionSelected = { option -> selectedOption = option },
                )
            }
        }

        composeRule.onNodeWithText("Biometric").performClick()

        assertEquals(SecurityOption.BIOMETRIC, selectedOption)
    }
}
