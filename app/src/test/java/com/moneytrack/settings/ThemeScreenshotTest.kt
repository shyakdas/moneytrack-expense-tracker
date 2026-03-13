// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.settings.domain.model.AppThemeMode
import com.moneytrack.settings.presentation.ThemeScreen
import com.moneytrack.settings.presentation.ThemeUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class ThemeScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun theme_light_systemSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                ThemeScreen(
                    uiState = ThemeUiState(selectedThemeMode = AppThemeMode.SYSTEM),
                    onBackClick = {},
                    onThemeModeSelected = {},
                )
            }
        }
    }

    @Test
    fun theme_dark_darkSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                ThemeScreen(
                    uiState = ThemeUiState(selectedThemeMode = AppThemeMode.DARK),
                    onBackClick = {},
                    onThemeModeSelected = {},
                )
            }
        }
    }
}
