// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.domain.usecase

import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(): Flow<List<TransactionRecord>> = transactionRepository.observeTransactions()
}
