// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.settings.presentation.SecurityOption
import com.moneytrack.settings.presentation.SecurityScreen
import com.moneytrack.settings.presentation.SecurityUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class SecurityScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun security_light_noneSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                SecurityScreen(
                    uiState = SecurityUiState(selectedOption = SecurityOption.NONE),
                    onBackClick = {},
                    onOptionSelected = {},
                )
            }
        }
    }

    @Test
    fun security_dark_biometricSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                SecurityScreen(
                    uiState = SecurityUiState(selectedOption = SecurityOption.BIOMETRIC),
                    onBackClick = {},
                    onOptionSelected = {},
                )
            }
        }
    }
}
