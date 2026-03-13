// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.settings.domain.model.CurrencyOption
import ui.theme.MoneyTrackTheme

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
internal fun SettingsScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        SettingsScreen(
            uiState = SettingsUiState(
                currencySymbol = "₹",
                language = "English",
                themeMode = SettingsThemeMode.SYSTEM,
                securityType = SettingsSecurityType.BIOMETRIC,
                notificationsPerDay = 3,
            ),
            onBackClick = {},
            onCurrencyClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
internal fun CurrencyScreenPreview() {
    MoneyTrackTheme(darkTheme = false) {
        CurrencyScreen(
            uiState = CurrencyUiState(
                selectedCurrencyCode = "INR",
                currencies = listOf(
                    CurrencyOption(countryName = "India", code = "INR", symbol = "₹"),
                    CurrencyOption(countryName = "United States", code = "USD", symbol = "$"),
                    CurrencyOption(countryName = "Germany", code = "EUR", symbol = "€"),
                ),
            ),
            onBackClick = {},
            onCurrencySelected = {},
        )
    }
}
