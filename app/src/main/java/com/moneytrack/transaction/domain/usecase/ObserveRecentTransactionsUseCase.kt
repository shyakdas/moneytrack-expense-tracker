// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.domain.usecase

import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRecentTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(limit: Int): Flow<List<TransactionRecord>> =
        transactionRepository.observeRecentTransactions(limit = limit)
}
