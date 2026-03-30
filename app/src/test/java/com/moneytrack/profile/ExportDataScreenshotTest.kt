// Copyright (c) 2026 shyakdas

package com.moneytrack.profile

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.profile.domain.model.ExportDateRange
import com.moneytrack.profile.presentation.ExportDataActions
import com.moneytrack.profile.presentation.ExportDataScreen
import com.moneytrack.profile.presentation.ExportDataUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class ExportDataScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun exportData_light_default() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
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
    }

    @Test
    fun exportData_dark_last90Days() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                ExportDataScreen(
                    uiState = ExportDataUiState(
                        selectedDateRange = ExportDateRange.LAST_90_DAYS,
                    ),
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
    }
}
