// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.locale.CurrencyCatalog
import com.moneytrack.settings.domain.model.CurrencyOption
import com.moneytrack.settings.domain.usecase.ObserveAppCurrencyCodeUseCase
import com.moneytrack.settings.domain.usecase.SaveSelectedCurrencyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    observeAppCurrencyCodeUseCase: ObserveAppCurrencyCodeUseCase,
    private val saveSelectedCurrencyCodeUseCase: SaveSelectedCurrencyCodeUseCase,
    currencyCatalog: CurrencyCatalog,
) : ViewModel() {

    private val allCurrencies = currencyCatalog.all()
    private val currentCurrencyCode = observeAppCurrencyCodeUseCase()

    val uiState: StateFlow<CurrencyUiState> = currentCurrencyCode
        .map { selectedCurrencyCode ->
            CurrencyUiState(
                selectedCurrencyCode = selectedCurrencyCode,
                currencies = allCurrencies.prioritizeSelected(selectedCurrencyCode),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
            initialValue = CurrencyUiState(
                selectedCurrencyCode = currentCurrencyCode.value,
                currencies = allCurrencies.prioritizeSelected(currentCurrencyCode.value),
            ),
        )

    fun onCurrencySelected(currencyCode: String) {
        viewModelScope.launch {
            saveSelectedCurrencyCodeUseCase(currencyCode = currencyCode)
        }
    }

    private companion object {
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
    }
}

data class CurrencyUiState(
    val selectedCurrencyCode: String,
    val currencies: List<CurrencyOption>,
)

private fun List<CurrencyOption>.prioritizeSelected(
    selectedCurrencyCode: String,
): List<CurrencyOption> {
    val sortedCurrencies = sortedBy(CurrencyOption::countryName)
    val selectedCurrency = sortedCurrencies.firstOrNull { option ->
        option.code == selectedCurrencyCode
    }
    return buildList {
        if (selectedCurrency != null) {
            add(selectedCurrency)
        }
        addAll(sortedCurrencies.filterNot { option -> option.code == selectedCurrencyCode })
    }
}
