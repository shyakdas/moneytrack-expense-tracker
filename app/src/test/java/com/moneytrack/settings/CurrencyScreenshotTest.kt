// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.settings.domain.model.CurrencyOption
import com.moneytrack.settings.presentation.CurrencyScreen
import com.moneytrack.settings.presentation.CurrencyUiState
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class CurrencyScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun currency_light_default() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                CurrencyScreen(
                    uiState = baseState(),
                    onBackClick = {},
                    onSearchQueryChanged = {},
                    onCurrencySelected = {},
                )
            }
        }
    }

    @Test
    fun currency_dark_searchResults() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                CurrencyScreen(
                    uiState = baseState(
                        selectedCurrencyCode = "GBP",
                        searchQuery = "Uni",
                        currencies = listOf(
                            CurrencyOption(countryName = "United Kingdom", code = "GBP", symbol = "£"),
                            CurrencyOption(countryName = "United States", code = "USD", symbol = "$"),
                        ),
                    ),
                    onBackClick = {},
                    onSearchQueryChanged = {},
                    onCurrencySelected = {},
                )
            }
        }
    }

    private fun baseState(
        selectedCurrencyCode: String = "INR",
        searchQuery: String = "",
        currencies: List<CurrencyOption> = listOf(
            CurrencyOption(countryName = "India", code = "INR", symbol = "₹"),
            CurrencyOption(countryName = "United Kingdom", code = "GBP", symbol = "£"),
            CurrencyOption(countryName = "United States", code = "USD", symbol = "$"),
            CurrencyOption(countryName = "Germany", code = "EUR", symbol = "€"),
        ),
    ): CurrencyUiState = CurrencyUiState(
        selectedCurrencyCode = selectedCurrencyCode,
        searchQuery = searchQuery,
        currencies = currencies,
    )
}
