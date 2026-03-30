// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.domain.usecase

import com.moneytrack.common.time.CurrentTimeProvider
import com.moneytrack.profile.domain.model.CsvExportPayload
import com.moneytrack.profile.domain.model.ExportDateRange
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.repository.TransactionRepository
import dagger.hilt.android.scopes.ViewModelScoped
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@ViewModelScoped
class BuildTransactionExportUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val currentTimeProvider: CurrentTimeProvider,
) {

    suspend operator fun invoke(dateRange: ExportDateRange): CsvExportPayload {
        val nowMillis = currentTimeProvider.now()
        val startOfRange = exportRangeStart(nowMillis = nowMillis, dateRange = dateRange)
        val transactions = transactionRepository.getTransactionsFrom(fromEpochMillis = startOfRange)
        val generatedDate = fileDateFormatter().format(Date(nowMillis))

        return CsvExportPayload(
            fileName = "moneytrack-export-${dateRange.days}-days-$generatedDate.csv",
            content = buildCsvContent(transactions = transactions),
        )
    }

    private fun exportRangeStart(nowMillis: Long, dateRange: ExportDateRange): Long {
        return Calendar.getInstance().apply {
            timeInMillis = nowMillis
            add(Calendar.DAY_OF_YEAR, -(dateRange.days.toInt() - 1))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun buildCsvContent(transactions: List<TransactionRecord>): String {
        val rows = buildList {
            add(HEADER_ROW)
            transactions.forEach { transaction ->
                add(transaction.toCsvRow())
            }
        }
        return rows.joinToString(separator = "\n")
    }

    private fun TransactionRecord.toCsvRow(): String {
        val transactionDate = Date(occurredAtEpochMillis)
        return listOf(
            id.toString(),
            dateFormatter().format(transactionDate),
            timeFormatter().format(transactionDate),
            type.name,
            title,
            category,
            amount.toString(),
            note.orEmpty(),
        ).joinToString(separator = ",", transform = ::escapeCsvValue)
    }

    private fun escapeCsvValue(value: String): String {
        val escapedValue = value.replace("\"", "\"\"")
        return if (escapedValue.contains(',') || escapedValue.contains('"') || escapedValue.contains('\n')) {
            "\"$escapedValue\""
        } else {
            escapedValue
        }
    }

    private fun dateFormatter(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun timeFormatter(): SimpleDateFormat =
        SimpleDateFormat("hh:mm a", Locale.getDefault())

    private fun fileDateFormatter(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private companion object {
        private const val HEADER_ROW = "id,date,time,type,title,category,amount,note"
    }
}
