// Copyright (c) 2026 shyakdas

package com.moneytrack.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.profile.presentation.ExportDataActions
import com.moneytrack.profile.presentation.ExportDataScreen
import com.moneytrack.profile.presentation.ExportDataUiState
import com.moneytrack.profile.domain.model.ExportDateRange
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class ExportDataScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun exportScreen_showsDefaultFields() {
        composeRule.setContent {
            MoneyTrackTheme {
                ExportDataScreen(
                    uiState = ExportDataUiState(),
                    actions = ExportDataActions(
                        onBackClick = {},
                        onDataTypeSelected = {},
                        onDateRangeSelected = {},
                        onFormatSelected = {},
                        onExportClick = {},
                    ),
                )
            }
        }

        composeRule.onNodeWithText("What data do you want to export?").assertIsDisplayed()
        composeRule.onNodeWithText("All transactions").assertIsDisplayed()
        composeRule.onNodeWithText("When date range?").assertIsDisplayed()
        composeRule.onNodeWithText("Last 30 days").assertIsDisplayed()
        composeRule.onNodeWithText("What format do you want to export?").assertIsDisplayed()
        composeRule.onNodeWithText("CSV").assertIsDisplayed()
        composeRule.onNodeWithText("Export").assertIsDisplayed()
    }

    @Test
    fun selectingDateRange_callsCallback() {
        var selectedDateRange: ExportDateRange? = null

        composeRule.setContent {
            MoneyTrackTheme {
                ExportDataScreen(
                    uiState = ExportDataUiState(),
                    actions = ExportDataActions(
                        onBackClick = {},
                        onDataTypeSelected = {},
                        onDateRangeSelected = { selectedDateRange = it },
                        onFormatSelected = {},
                        onExportClick = {},
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Last 30 days").performClick()
        composeRule.onNodeWithText("Last 90 days").performClick()

        assertEquals(ExportDateRange.LAST_90_DAYS, selectedDateRange)
    }

    @Test
    fun tappingExport_callsCallback() {
        var exportClicks = 0

        composeRule.setContent {
            MoneyTrackTheme {
                ExportDataScreen(
                    uiState = ExportDataUiState(),
                    actions = ExportDataActions(
                        onBackClick = {},
                        onDataTypeSelected = {},
                        onDateRangeSelected = {},
                        onFormatSelected = {},
                        onExportClick = { exportClicks++ },
                    ),
                )
            }
        }

        composeRule.onNodeWithText("Export").performClick()

        assertEquals(1, exportClicks)
    }
}
