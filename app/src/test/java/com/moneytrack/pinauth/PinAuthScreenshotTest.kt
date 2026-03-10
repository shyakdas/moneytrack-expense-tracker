// Copyright (c) 2026 shyakdas

package com.moneytrack.pinauth

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.runtime.Composable
import com.moneytrack.pinauth.presentation.PinAuthMode
import com.moneytrack.pinauth.presentation.PinAuthScreen
import com.moneytrack.pinauth.presentation.PinAuthUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class PinAuthScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun pinauth_pin_light() {
        paparazzi.snapshot {
            PinAuthSnapshotContent(
                uiState = PinAuthUiState(
                    isLoading = false,
                    mode = PinAuthMode.PIN,
                    enteredPin = "12",
                ),
                darkTheme = false,
            )
        }
    }

    @Test
    fun pinauth_pin_error_light() {
        paparazzi.snapshot {
            PinAuthSnapshotContent(
                uiState = PinAuthUiState(
                    isLoading = false,
                    mode = PinAuthMode.PIN,
                    showPinError = true,
                ),
                darkTheme = false,
            )
        }
    }

    @Test
    fun pinauth_biometric_light() {
        paparazzi.snapshot {
            PinAuthSnapshotContent(
                uiState = PinAuthUiState(
                    isLoading = false,
                    mode = PinAuthMode.BIOMETRIC,
                ),
                darkTheme = false,
            )
        }
    }

    @Test
    fun pinauth_pin_dark() {
        paparazzi.snapshot {
            PinAuthSnapshotContent(
                uiState = PinAuthUiState(
                    isLoading = false,
                    mode = PinAuthMode.PIN,
                    enteredPin = "12",
                ),
                darkTheme = true,
            )
        }
    }

    @Test
    fun pinauth_pin_error_dark() {
        paparazzi.snapshot {
            PinAuthSnapshotContent(
                uiState = PinAuthUiState(
                    isLoading = false,
                    mode = PinAuthMode.PIN,
                    showPinError = true,
                ),
                darkTheme = true,
            )
        }
    }

    @Test
    fun pinauth_biometric_dark() {
        paparazzi.snapshot {
            PinAuthSnapshotContent(
                uiState = PinAuthUiState(
                    isLoading = false,
                    mode = PinAuthMode.BIOMETRIC,
                ),
                darkTheme = true,
            )
        }
    }
}

@Composable
private fun PinAuthSnapshotContent(
    uiState: PinAuthUiState,
    darkTheme: Boolean,
) {
    MoneyTrackTheme(darkTheme = darkTheme) {
        PinAuthScreen(
            uiState = uiState,
            onAction = {},
        )
    }
}
