// Copyright (c) 2026 shyakdas

package com.moneytrack.locale

import com.moneytrack.settings.domain.model.CurrencyOption
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyCatalog @Inject constructor() {

    private val currencies: List<CurrencyOption> by lazy {
        Locale.getAvailableLocales()
            .mapNotNull { locale ->
                val countryName = locale.countryName()
                if (countryName.isBlank()) {
                    null
                } else {
                    val currency = runCatching { Currency.getInstance(locale) }.getOrNull()
                    currency?.let {
                        CurrencyOption(
                            countryName = countryName,
                            code = it.currencyCode,
                            symbol = it.symbolForDisplay(),
                        )
                    }
                }
            }
            .filter { option -> option.code !in unsupportedCurrencyCodes() }
            .distinctBy(CurrencyOption::code)
            .sortedBy(CurrencyOption::countryName)
    }

    fun all(): List<CurrencyOption> = currencies

    fun find(currencyCode: String): CurrencyOption? =
        currencies.firstOrNull { option -> option.code == currencyCode }

    fun defaultCurrencyCode(countryCode: String): String = runCatching {
        Currency.getInstance(
            Locale.Builder()
                .setLanguage(DEFAULT_LANGUAGE)
                .setRegion(countryCode)
                .build(),
        ).currencyCode
    }.getOrDefault(DEFAULT_CURRENCY_CODE)

    private fun Locale.countryName(): String {
        val rawName = getDisplayCountry(Locale.ENGLISH)
        return rawName.replaceFirstChar { char ->
            if (char.isLowerCase()) {
                char.titlecase(Locale.ENGLISH)
            } else {
                char.toString()
            }
        }.trim()
    }

    private fun Currency.symbolForDisplay(): String {
        val symbol = runCatching {
            getSymbol(Locale.ENGLISH)
        }.getOrDefault(currencyCode)
        return symbol.takeUnless { it.isBlank() || it == currencyCode } ?: currencyCode
    }

    private fun unsupportedCurrencyCodes(): Set<String> = setOf(
        "XTS",
        "XXX",
        "XUA",
        "XBA",
        "XBB",
        "XBC",
        "XBD",
        "XAU",
        "XAG",
        "XPT",
        "XPD",
        "XDR",
        "XSU",
        "XFU",
        "XFO",
    )

    private companion object {
        private const val DEFAULT_CURRENCY_CODE = "USD"
        private const val DEFAULT_LANGUAGE = "en"
    }
}
