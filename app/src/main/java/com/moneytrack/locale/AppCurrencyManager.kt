// Copyright (c) 2026 shyakdas

package com.moneytrack.locale

import com.moneytrack.settings.domain.usecase.SaveSelectedCurrencyCodeUseCase
import com.moneytrack.settings.domain.usecase.ObserveSelectedCurrencyCodeUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class AppCurrencyManager internal constructor(
    observeSelectedCurrencyCodeUseCase: ObserveSelectedCurrencyCodeUseCase,
    private val saveSelectedCurrencyCodeUseCase: SaveSelectedCurrencyCodeUseCase,
    countryProvider: CountryProvider,
    currencyCatalog: CurrencyCatalog,
    scope: CoroutineScope,
) {

    private val defaultCurrencyCode = currencyCatalog.defaultCurrencyCode(countryProvider.getCountryCode())
    private val _currencyCode = MutableStateFlow(defaultCurrencyCode)
    val currencyCode: StateFlow<String> = _currencyCode.asStateFlow()

    @Inject
    constructor(
        observeSelectedCurrencyCodeUseCase: ObserveSelectedCurrencyCodeUseCase,
        saveSelectedCurrencyCodeUseCase: SaveSelectedCurrencyCodeUseCase,
        countryProvider: CountryProvider,
        currencyCatalog: CurrencyCatalog,
    ) : this(
        observeSelectedCurrencyCodeUseCase = observeSelectedCurrencyCodeUseCase,
        saveSelectedCurrencyCodeUseCase = saveSelectedCurrencyCodeUseCase,
        countryProvider = countryProvider,
        currencyCatalog = currencyCatalog,
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    )

    init {
        scope.launch {
            observeSelectedCurrencyCodeUseCase().collect { selectedCurrencyCode ->
                val resolvedCurrencyCode = selectedCurrencyCode
                    ?.takeIf { currencyCode -> currencyCatalog.find(currencyCode) != null }
                    ?: defaultCurrencyCode
                if (selectedCurrencyCode != null && selectedCurrencyCode != resolvedCurrencyCode) {
                    saveSelectedCurrencyCodeUseCase(resolvedCurrencyCode)
                }
                _currencyCode.value = resolvedCurrencyCode
            }
        }
    }

    fun currentCurrencyCode(): String = currencyCode.value
}
