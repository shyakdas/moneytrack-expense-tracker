// Copyright (c) 2026 shyakdas

package com.moneytrack.locale

import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceCountryProviderTest {

    @Test
    fun resolveCountryCode_prefersTelephonyCountryOverLocaleRegion() {
        val countryCode = resolveCountryCode(
            configCountry = "GB",
            localeCountry = "GB",
            networkCountry = "IN",
            simCountry = "IN",
        )

        assertEquals("IN", countryCode)
    }

    @Test
    fun resolveCountryCode_fallsBackToConfigurationCountry_whenTelephonyUnavailable() {
        val countryCode = resolveCountryCode(
            configCountry = "IN",
            localeCountry = "GB",
            networkCountry = "",
            simCountry = "",
        )

        assertEquals("IN", countryCode)
    }

    @Test
    fun resolveCountryCode_ignoresInvalidValues_andUsesDefaultAsLastFallback() {
        val countryCode = resolveCountryCode(
            configCountry = "001",
            localeCountry = "1",
            networkCountry = "",
            simCountry = "@@",
        )

        assertEquals(DEFAULT_COUNTRY_CODE, countryCode)
    }
}
