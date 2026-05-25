// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.domain.usecase

import com.moneytrack.transaction.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(id: Long) {
        transactionRepository.deleteTransaction(id)
    }
}
