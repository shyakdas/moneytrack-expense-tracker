// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.domain.model

enum class ExportDataType {
    ALL,
}

enum class ExportDateRange(val days: Long) {
    LAST_30_DAYS(LAST_30_DAYS_VALUE),
    LAST_60_DAYS(LAST_60_DAYS_VALUE),
    LAST_90_DAYS(LAST_90_DAYS_VALUE),
    LAST_120_DAYS(LAST_120_DAYS_VALUE),
}

enum class ExportFormat {
    CSV,
}

data class CsvExportPayload(
    val fileName: String,
    val content: String,
)

private const val LAST_30_DAYS_VALUE = 30L
private const val LAST_60_DAYS_VALUE = 60L
private const val LAST_90_DAYS_VALUE = 90L
private const val LAST_120_DAYS_VALUE = 120L
