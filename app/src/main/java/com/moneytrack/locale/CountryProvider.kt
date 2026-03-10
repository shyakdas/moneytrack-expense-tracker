// Copyright (c) 2026 shyakdas

package com.moneytrack.locale

interface CountryProvider {
    fun getCountryCode(): String
    fun getCurrencySymbol(): String
}
