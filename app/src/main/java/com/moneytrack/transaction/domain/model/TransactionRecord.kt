// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.domain.model

data class TransactionRecord(
    val id: Long,
    val title: String,
    val note: String?,
    val amount: Double,
    val type: TransactionRecordType,
    val category: String,
    val attachmentUri: String?,
    val attachmentName: String?,
    val attachmentType: String?,
    val occurredAtEpochMillis: Long,
)

enum class TransactionRecordType {
    EXPENSE,
    INCOME,
}
