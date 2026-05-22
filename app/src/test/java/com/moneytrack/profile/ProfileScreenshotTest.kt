// Copyright (c) 2026 shyakdas

package com.moneytrack.profile

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.profile.presentation.ProfileActionCallbacks
import com.moneytrack.profile.presentation.ProfileNavigationCallbacks
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
                    navigationCallbacks = previewNavigationCallbacks(),
                    actionCallbacks = previewActionCallbacks(),
                )
            }
        }
    }

    @Test
    fun profile_editName_light() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                ProfileScreen(
                    uiState = baseState().copy(
                        editName = "Saver",
                        isEditSheetVisible = true,
                        isSaveEnabled = true,
                    ),
                    navigationCallbacks = previewNavigationCallbacks(),
                    actionCallbacks = previewActionCallbacks(),
                )
            }
        }
    }

    @Test
    fun profile_clearData_dark() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                ProfileScreen(
                    uiState = baseState().copy(isClearDataSheetVisible = true),
                    navigationCallbacks = previewNavigationCallbacks(),
                    actionCallbacks = previewActionCallbacks(),
                )
            }
        }
    }

    private fun baseState(): ProfileUiState = ProfileUiState(
        name = "Saver",
        editName = "",
        isEditSheetVisible = false,
        isClearDataSheetVisible = false,
        clearDataCompleted = false,
        isSaveEnabled = false,
    )

    private fun previewNavigationCallbacks(): ProfileNavigationCallbacks = ProfileNavigationCallbacks(
        onBottomRouteClick = {},
        onAddExpenseClick = {},
    )

    private fun previewActionCallbacks(): ProfileActionCallbacks = ProfileActionCallbacks(
        onEditClick = {},
        onSettingsClick = {},
        onExportClick = {},
        onDismissEditSheet = {},
        onEditNameChanged = {},
        onSaveName = {},
        onClearDataClick = {},
        onDismissClearDataSheet = {},
        onConfirmClearData = {},
    )
}
