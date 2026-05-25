// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import java.util.Calendar
import java.util.Locale

data class HomeMonthOption(
    val monthIndex: Int,
    val year: Int,
    val label: String,
    val shortLabel: String,
)

internal fun currentHomeMonthOption(now: Calendar = Calendar.getInstance()): HomeMonthOption =
    HomeMonthOption(
        monthIndex = now.get(Calendar.MONTH),
        year = now.get(Calendar.YEAR),
        label = now.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).orEmpty(),
        shortLabel = now.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).orEmpty(),
    )

internal fun homeMonthOptions(year: Int = Calendar.getInstance().get(Calendar.YEAR)): List<HomeMonthOption> =
    (0 until MONTH_COUNT).map { monthIndex ->
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthIndex)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        HomeMonthOption(
            monthIndex = monthIndex,
            year = year,
            label = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).orEmpty(),
            shortLabel = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).orEmpty(),
        )
    }

internal fun homeYearOptions(currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)): List<Int> =
    (currentYear..currentYear + YEAR_OPTION_OFFSET).toList()

private const val MONTH_COUNT = 12
private const val YEAR_OPTION_OFFSET = 5
