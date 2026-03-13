// Copyright (c) 2026 shyakdas

package com.moneytrack.locale

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class CurrencyFormatter @Inject constructor(
    private val appCurrencyManager: AppCurrencyManager,
    private val currencyCatalog: CurrencyCatalog,
) {

    fun format(
        value: Double,
        currencyCode: String = currentCurrencyCode(),
    ): String {
        val currencySymbol = currencyCatalog.find(currencyCode)?.symbol ?: currencyCode
        val numberFormatter = NumberFormat.getIntegerInstance(DEFAULT_NUMBER_LOCALE)
        val absoluteValue = abs(value).toLong()
        val formattedNumber = if (currencyCode == INDIA_CURRENCY_CODE) {
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

    fun currentCurrencyCode(): String = appCurrencyManager.currentCurrencyCode()

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

    private companion object {
        private val DEFAULT_NUMBER_LOCALE = Locale.US
        private const val INDIA_CURRENCY_CODE = "INR"
        private const val INDIAN_LAST_GROUP_SIZE = 3
        private const val INDIAN_MIDDLE_GROUP_SIZE = 2
    }
}
