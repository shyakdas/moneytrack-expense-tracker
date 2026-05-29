// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.presentation

import java.util.Calendar
import java.util.Locale

data class TransactionMonthOption(
    val monthIndex: Int,
    val year: Int,
    val label: String,
    val shortLabel: String,
)

internal fun currentTransactionMonthOption(
    now: Calendar = Calendar.getInstance(),
): TransactionMonthOption = TransactionMonthOption(
    monthIndex = now.get(Calendar.MONTH),
    year = now.get(Calendar.YEAR),
    label = now.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).orEmpty(),
    shortLabel = now.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).orEmpty(),
)

internal fun transactionMonthOptions(
    year: Int = Calendar.getInstance().get(Calendar.YEAR),
): List<TransactionMonthOption> = (0 until MONTH_COUNT).map { monthIndex ->
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, monthIndex)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    TransactionMonthOption(
        monthIndex = monthIndex,
        year = year,
        label = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).orEmpty(),
        shortLabel = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).orEmpty(),
    )
}

internal fun transactionYearOptions(
    currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
): List<Int> = (currentYear..currentYear + YEAR_OPTION_OFFSET).toList()

private const val MONTH_COUNT = 12
private const val YEAR_OPTION_OFFSET = 5
