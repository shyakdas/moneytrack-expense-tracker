// Copyright (c) 2026 shyakdas

package com.moneytrack.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moneytrack.settings.domain.model.CurrencyOption
import com.moneytrack.settings.presentation.CurrencyScreen
import com.moneytrack.settings.presentation.CurrencyUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ui.theme.MoneyTrackTheme

@RunWith(AndroidJUnit4::class)
class CurrencyScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun searchBarHint_isDisplayed() {
        composeRule.setContent {
            MoneyTrackTheme {
                CurrencyScreen(
                    uiState = baseState(),
                    onBackClick = {},
                    onSearchQueryChanged = {},
                    onCurrencySelected = {},
                )
            }
        }

        composeRule.onNodeWithText("Search by country name").assertIsDisplayed()
    }

    @Test
    fun typingInSearch_filtersByCountryName() {
        val allCurrencies = sampleCurrencies()
        composeRule.setContent {
            var uiState by mutableStateOf(baseState(currencies = allCurrencies))

            MoneyTrackTheme {
                CurrencyScreen(
                    uiState = uiState,
                    onBackClick = {},
                    onSearchQueryChanged = { query ->
                        uiState = uiState.copy(
                            searchQuery = query,
                            currencies = if (query.isBlank()) {
                                allCurrencies
                            } else {
                                allCurrencies.filter {
                                    it.countryName.contains(query, ignoreCase = true)
                                }
                            },
                        )
                    },
                    onCurrencySelected = {},
                )
            }
        }

        composeRule.onNode(hasSetTextAction()).performTextInput("India")

        composeRule.onNodeWithText("India").assertIsDisplayed()
        assertEquals(0, composeRule.onAllNodesWithText("United Kingdom").fetchSemanticsNodes().size)
        assertEquals(0, composeRule.onAllNodesWithText("United States").fetchSemanticsNodes().size)
    }

    @Test
    fun tappingCurrency_callsSelectionCallback() {
        var selectedCurrencyCode: String? = null
        composeRule.setContent {
            MoneyTrackTheme {
                CurrencyScreen(
                    uiState = baseState(),
                    onBackClick = {},
                    onSearchQueryChanged = {},
                    onCurrencySelected = { code -> selectedCurrencyCode = code },
                )
            }
        }

        composeRule.onNodeWithText("United Kingdom").performClick()

        assertEquals("GBP", selectedCurrencyCode)
    }

    @Test
    fun selectedCurrency_isShownOnceInTheList() {
        composeRule.setContent {
            MoneyTrackTheme {
                CurrencyScreen(
                    uiState = baseState(
                        selectedCurrencyCode = "INR",
                    ),
                    onBackClick = {},
                    onSearchQueryChanged = {},
                    onCurrencySelected = {},
                )
            }
        }

        assertEquals(1, composeRule.onAllNodesWithText("India").fetchSemanticsNodes().size)
    }

    private fun baseState(
        selectedCurrencyCode: String = "INR",
        currencies: List<CurrencyOption> = sampleCurrencies(),
    ): CurrencyUiState = CurrencyUiState(
        selectedCurrencyCode = selectedCurrencyCode,
        searchQuery = "",
        currencies = currencies,
    )

    private fun sampleCurrencies(): List<CurrencyOption> = listOf(
        CurrencyOption(countryName = "India", code = "INR", symbol = "₹"),
        CurrencyOption(countryName = "United Kingdom", code = "GBP", symbol = "£"),
        CurrencyOption(countryName = "United States", code = "USD", symbol = "$"),
    )
}
