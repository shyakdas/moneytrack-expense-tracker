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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    observeAppCurrencyCodeUseCase: ObserveAppCurrencyCodeUseCase,
    private val saveSelectedCurrencyCodeUseCase: SaveSelectedCurrencyCodeUseCase,
    currencyCatalog: CurrencyCatalog,
) : ViewModel() {

    private val allCurrencies = currencyCatalog.all()
    private val currentCurrencyCode = observeAppCurrencyCodeUseCase()
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<CurrencyUiState> = combine(
        currentCurrencyCode,
        searchQuery,
    ) { selectedCurrencyCode, query ->
        val filteredCurrencies = allCurrencies.filteredForDisplay(
            selectedCurrencyCode = selectedCurrencyCode,
            query = query,
        )

            CurrencyUiState(
                selectedCurrencyCode = selectedCurrencyCode,
                searchQuery = query,
                currencies = filteredCurrencies,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
            initialValue = CurrencyUiState(
                selectedCurrencyCode = currentCurrencyCode.value,
                searchQuery = searchQuery.value,
                currencies = allCurrencies.prioritizeSelected(currentCurrencyCode.value),
            ),
        )

    fun onSearchQueryChanged(query: String) {
        searchQuery.update { query }
    }

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
    val searchQuery: String,
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

private fun List<CurrencyOption>.filterByCountry(query: String): List<CurrencyOption> {
    if (query.isBlank()) return this
    val normalizedQuery = query.trim()
    return filter { option ->
        option.countryName.contains(normalizedQuery, ignoreCase = true)
    }
}

private fun List<CurrencyOption>.filteredForDisplay(
    selectedCurrencyCode: String,
    query: String,
): List<CurrencyOption> {
    val normalizedQuery = query.trim()
    return if (normalizedQuery.isBlank()) {
        prioritizeSelected(selectedCurrencyCode)
    } else {
        filterByCountry(normalizedQuery).sortedBy(CurrencyOption::countryName)
    }
}
