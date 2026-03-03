package com.moneytrack.pinsetup

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.runtime.Composable
import com.moneytrack.pinsetup.presentation.PinSetupScreen
import com.moneytrack.pinsetup.presentation.PinSetupStage
import com.moneytrack.pinsetup.presentation.PinSetupUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class PinSetupScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun pinsetup_intro_light() {
        paparazzi.snapshot {
            PinSetupSnapshotContent(
                uiState = PinSetupUiState(stage = PinSetupStage.INTRO),
                darkTheme = false,
            )
        }
    }

    @Test
    fun pinsetup_create_pin_light() {
        paparazzi.snapshot {
            PinSetupSnapshotContent(
                uiState = PinSetupUiState(
                    stage = PinSetupStage.CREATE_PIN,
                    enteredPin = "12",
                ),
                darkTheme = false,
            )
        }
    }

    @Test
    fun pinsetup_locked_light() {
        paparazzi.snapshot {
            PinSetupSnapshotContent(
                uiState = PinSetupUiState(
                    stage = PinSetupStage.CONFIRM_PIN,
                    showPinMismatch = true,
                    failedAttempts = 3,
                    isLockedOut = true,
                ),
                darkTheme = false,
            )
        }
    }

    @Test
    fun pinsetup_success_light() {
        paparazzi.snapshot {
            PinSetupSnapshotContent(
                uiState = PinSetupUiState(stage = PinSetupStage.SUCCESS),
                darkTheme = false,
            )
        }
    }

    @Test
    fun pinsetup_intro_dark() {
        paparazzi.snapshot {
            PinSetupSnapshotContent(
                uiState = PinSetupUiState(stage = PinSetupStage.INTRO),
                darkTheme = true,
            )
        }
    }

    @Test
    fun pinsetup_create_pin_dark() {
        paparazzi.snapshot {
            PinSetupSnapshotContent(
                uiState = PinSetupUiState(
                    stage = PinSetupStage.CREATE_PIN,
                    enteredPin = "12",
                ),
                darkTheme = true,
            )
        }
    }

    @Test
    fun pinsetup_locked_dark() {
        paparazzi.snapshot {
            PinSetupSnapshotContent(
                uiState = PinSetupUiState(
                    stage = PinSetupStage.CONFIRM_PIN,
                    showPinMismatch = true,
                    failedAttempts = 3,
                    isLockedOut = true,
                ),
                darkTheme = true,
            )
        }
    }

    @Test
    fun pinsetup_success_dark() {
        paparazzi.snapshot {
            PinSetupSnapshotContent(
                uiState = PinSetupUiState(stage = PinSetupStage.SUCCESS),
                darkTheme = true,
            )
        }
    }
}

@Composable
private fun PinSetupSnapshotContent(
    uiState: PinSetupUiState,
    darkTheme: Boolean,
) {
    MoneyTrackTheme(darkTheme = darkTheme) {
        PinSetupScreen(
            uiState = uiState,
            onAction = {},
        )
    }
}
