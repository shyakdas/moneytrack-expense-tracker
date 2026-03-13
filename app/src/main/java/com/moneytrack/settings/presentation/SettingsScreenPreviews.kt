// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.MoneyTrackTheme

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
internal fun SettingsScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        SettingsScreen(
            uiState = SettingsUiState(
                currencyCode = "INR",
                language = "English",
                themeMode = SettingsThemeMode.SYSTEM,
                securityType = SettingsSecurityType.BIOMETRIC,
                notificationsPerDay = 3,
            ),
            onBackClick = {},
        )
    }
}
