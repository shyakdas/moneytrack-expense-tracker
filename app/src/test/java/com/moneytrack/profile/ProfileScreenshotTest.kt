// Copyright (c) 2026 shyakdas

package com.moneytrack.profile

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.profile.presentation.ProfileScreen
import com.moneytrack.profile.presentation.ProfileUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class ProfileScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun profile_default_light() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                ProfileScreen(
                    uiState = baseState(),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                    onEditClick = {},
                    onDismissEditSheet = {},
                    onEditNameChanged = {},
                    onSaveName = {},
                    onClearDataClick = {},
                    onDismissClearDataSheet = {},
                    onConfirmClearData = {},
                )
            }
        }
    }

    @Test
    fun profile_clearData_dark() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                ProfileScreen(
                    uiState = baseState(isClearDataSheetVisible = true),
                    onBottomRouteClick = {},
                    onAddExpenseClick = {},
                    onEditClick = {},
                    onDismissEditSheet = {},
                    onEditNameChanged = {},
                    onSaveName = {},
                    onClearDataClick = {},
                    onDismissClearDataSheet = {},
                    onConfirmClearData = {},
                )
            }
        }
    }

    private fun baseState(
        name: String = "Saver",
        editName: String = "",
        isEditSheetVisible: Boolean = false,
        isClearDataSheetVisible: Boolean = false,
        clearDataCompleted: Boolean = false,
        isSaveEnabled: Boolean = false,
    ): ProfileUiState = ProfileUiState(
        name = name,
        editName = editName,
        isEditSheetVisible = isEditSheetVisible,
        isClearDataSheetVisible = isClearDataSheetVisible,
        clearDataCompleted = clearDataCompleted,
        isSaveEnabled = isSaveEnabled,
    )
}
