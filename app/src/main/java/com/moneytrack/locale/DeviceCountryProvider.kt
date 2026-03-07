// Copyright (c) 2026 shyakdas

package com.moneytrack.locale

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Currency
import java.util.Locale

@Singleton
class DeviceCountryProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun getCountryCode(): String {
        val networkCountry = telephonyCountryCode { it.networkCountryIso }
        if (networkCountry.isNotEmpty()) return networkCountry

        val simCountry = telephonyCountryCode { it.simCountryIso }
        if (simCountry.isNotEmpty()) return simCountry

        val localeCountry = Locale.getDefault().country.orEmpty()
        if (localeCountry.isNotEmpty()) return localeCountry.uppercase(Locale.US)

        return DEFAULT_COUNTRY_CODE
    }

    fun getCurrencySymbol(): String {
        val countryCode = getCountryCode()
        val locale = Locale.Builder()
            .setLanguage("en")
            .setRegion(countryCode)
            .build()
        return runCatching {
            Currency.getInstance(locale).getSymbol(locale)
        }.getOrDefault(DEFAULT_CURRENCY_SYMBOL)
    }

    private fun telephonyCountryCode(
        selector: (TelephonyManager) -> String?,
    ): String {
        val telephonyManager = context.getSystemService(TelephonyManager::class.java) ?: return ""
        val raw = runCatching { selector(telephonyManager).orEmpty() }.getOrDefault("")
        return raw.uppercase(Locale.US)
    }

    private companion object {
        const val DEFAULT_COUNTRY_CODE = "US"
        const val DEFAULT_CURRENCY_SYMBOL = "$"
    }
}
