// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.settings.presentation.NotificationScreen
import com.moneytrack.settings.presentation.NotificationUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class NotificationScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun notification_light_threeSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                NotificationScreen(
                    uiState = NotificationUiState(selectedNotificationsPerDay = 3),
                    onBackClick = {},
                    onNotificationCountSelected = {},
                )
            }
        }
    }

    @Test
    fun notification_dark_fiveSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                NotificationScreen(
                    uiState = NotificationUiState(selectedNotificationsPerDay = 5),
                    onBackClick = {},
                    onNotificationCountSelected = {},
                )
            }
        }
    }
}
