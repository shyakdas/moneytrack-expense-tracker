// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.presentation.ThemeScreen
import com.moneytrack.settings.presentation.ThemeUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class ThemeScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun allThemeOptions_areDisplayed() {
        composeRule.setContent {
            MoneyTrackTheme {
                ThemeScreen(
                    uiState = ThemeUiState(selectedThemeMode = AppThemeMode.SYSTEM),
                    onBackClick = {},
                    onThemeModeSelected = {},
                )
            }
        }

        composeRule.onNodeWithText("Dark mode").assertIsDisplayed()
        composeRule.onNodeWithText("Light mode").assertIsDisplayed()
        composeRule.onNodeWithText("System").assertIsDisplayed()
    }

    @Test
    fun tappingThemeOption_callsSelectionCallback() {
        var selectedThemeMode: AppThemeMode? = null

        composeRule.setContent {
            MoneyTrackTheme {
                ThemeScreen(
                    uiState = ThemeUiState(selectedThemeMode = AppThemeMode.SYSTEM),
                    onBackClick = {},
                    onThemeModeSelected = { appThemeMode ->
                        selectedThemeMode = appThemeMode
                    },
                )
            }
        }

        composeRule.onNodeWithText("Dark mode").performClick()

        assertEquals(AppThemeMode.DARK, selectedThemeMode)
    }
}
