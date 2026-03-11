// Copyright (c) 2026 shyakdas

package com.moneytrack.locale

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class CurrencyFormatter @Inject constructor(
    private val countryProvider: CountryProvider,
) {

    fun format(value: Double): String {
        val countryCode = countryProvider.getCountryCode()
        val currencySymbol = countryProvider.getCurrencySymbol()
        val numberFormatter = NumberFormat.getIntegerInstance(countryLocale(countryCode))
        val absoluteValue = abs(value).toLong()
        val formattedNumber = if (countryCode == INDIA_COUNTRY_CODE) {
            formatIndianNumber(absoluteValue)
        } else {
            numberFormatter.format(absoluteValue)
        }

        return if (value < 0) {
            "-$currencySymbol$formattedNumber"
        } else {
            "$currencySymbol$formattedNumber"
        }
    }

    private fun formatIndianNumber(value: Long): String {
        val digits = value.toString()
        if (digits.length <= INDIAN_LAST_GROUP_SIZE) {
            return digits
        }

        val lastThree = digits.takeLast(INDIAN_LAST_GROUP_SIZE)
        val remaining = digits.dropLast(INDIAN_LAST_GROUP_SIZE)
        val firstGroupSize = if (remaining.length % INDIAN_MIDDLE_GROUP_SIZE == 0) {
            INDIAN_MIDDLE_GROUP_SIZE
        } else {
            1
        }
        val groupedRemaining = buildString {
            append(remaining.substring(0, firstGroupSize))
            var index = firstGroupSize
            while (index < remaining.length) {
                append(",")
                append(remaining.substring(index, index + INDIAN_MIDDLE_GROUP_SIZE))
                index += INDIAN_MIDDLE_GROUP_SIZE
            }
        }

        return "$groupedRemaining,$lastThree"
    }

    private fun countryLocale(countryCode: String): Locale = runCatching {
        Locale.Builder()
            .setLanguage("en")
            .setRegion(countryCode)
            .build()
    }.getOrDefault(Locale.US)

    private companion object {
        private const val INDIA_COUNTRY_CODE = "IN"
        private const val INDIAN_LAST_GROUP_SIZE = 3
        private const val INDIAN_MIDDLE_GROUP_SIZE = 2
    }
}
