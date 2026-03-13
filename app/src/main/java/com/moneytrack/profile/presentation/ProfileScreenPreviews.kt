// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.MoneyTrackTheme

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
internal fun ProfileScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        ProfileScreen(
            uiState = ProfileUiState(
                name = "Saver",
                editName = "Saver",
                isEditSheetVisible = false,
                isClearDataSheetVisible = false,
                clearDataCompleted = false,
                isSaveEnabled = true,
            ),
            navigationCallbacks = ProfileNavigationCallbacks(
                onBottomRouteClick = {},
                onAddExpenseClick = {},
            ),
            actionCallbacks = ProfileActionCallbacks(
                onEditClick = {},
                onSettingsClick = {},
                onDismissEditSheet = {},
                onEditNameChanged = {},
                onSaveName = {},
                onClearDataClick = {},
                onDismissClearDataSheet = {},
                onConfirmClearData = {},
            ),
        )
    }
}
