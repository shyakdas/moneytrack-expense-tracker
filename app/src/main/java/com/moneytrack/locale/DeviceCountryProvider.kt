// Copyright (c) 2026 shyakdas

package com.moneytrack.locale

import android.content.Context
import android.telephony.TelephonyManager
import androidx.core.os.ConfigurationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceCountryProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : CountryProvider {

    override fun getCountryCode(): String {
        val localeCountry = Locale.getDefault().country.orEmpty().uppercase(Locale.US)
        val configCountry = configurationCountryCode()
        val resolved = listOf(
            telephonyCountryCode { it.networkCountryIso },
            telephonyCountryCode { it.simCountryIso },
            configCountry,
            localeCountry,
        ).firstOrNull { it.isNotEmpty() }
        return resolved ?: DEFAULT_COUNTRY_CODE
    }

    override fun getCurrencySymbol(): String {
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

    private fun configurationCountryCode(): String {
        val locale = ConfigurationCompat.getLocales(context.resources.configuration)[0] ?: return ""
        return locale.country.orEmpty().uppercase(Locale.US)
    }

    private companion object {
        const val DEFAULT_COUNTRY_CODE = "US"
        const val DEFAULT_CURRENCY_SYMBOL = "$"
    }
}
