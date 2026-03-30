// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.profile.domain.model.ExportDateRange
import ui.theme.MoneyTrackTheme

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ExportDataScreenLightPreview() {
    MoneyTrackTheme(darkTheme = false) {
        ExportDataScreen(
            uiState = ExportDataUiState(
                selectedDateRange = ExportDateRange.LAST_30_DAYS,
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

@Suppress("UnusedPrivateMember")
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ExportDataScreenDarkPreview() {
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
