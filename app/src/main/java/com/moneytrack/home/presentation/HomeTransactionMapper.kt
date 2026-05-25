// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import com.moneytrack.locale.CurrencyFormatter
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.transaction.presentation.toTransactionIconRes
import java.text.SimpleDateFormat
import java.util.Locale
import ui.components.card.transaction.TransactionType

internal fun TransactionRecord.toHomeTransaction(
    selectedCurrencyCode: String,
    currencyFormatter: CurrencyFormatter,
): HomeTransaction {
    val isExpense = type == TransactionRecordType.EXPENSE
    return HomeTransaction(
        id = id,
        icon = category.toTransactionIconRes(),
        category = category,
        title = title,
        subtitle = note?.trim()?.takeIf(String::isNotEmpty),
        amount = currencyFormatter.format(
            value = if (isExpense) -amount else amount,
            currencyCode = selectedCurrencyCode,
        ),
        date = formatTransactionDate(occurredAtEpochMillis),
        time = formatTransactionTime(occurredAtEpochMillis),
        type = if (isExpense) {
            TransactionType.EXPENSE
        } else {
            TransactionType.INCOME
        },
    )
}

private fun formatTransactionTime(epochMillis: Long): String =
    SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(epochMillis)

private fun formatTransactionDate(epochMillis: Long): String =
    SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(epochMillis)

private const val TIME_PATTERN = "hh:mm a"
private const val DATE_PATTERN = "dd MMM yyyy"
