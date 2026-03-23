// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.settings.presentation.AboutScreen
import com.moneytrack.settings.presentation.AboutUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class AboutScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun about_light_default() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                AboutScreen(
                    uiState = AboutUiState(versionName = "1.0.0-dev"),
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun about_dark_default() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                AboutScreen(
                    uiState = AboutUiState(versionName = "1.0.0-dev"),
                    onBackClick = {},
                )
            }
        }
    }
}
