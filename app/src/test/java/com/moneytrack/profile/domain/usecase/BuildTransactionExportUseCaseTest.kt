// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.domain.usecase

import com.moneytrack.common.time.CurrentTimeProvider
import com.moneytrack.profile.domain.model.ExportDateRange
import com.moneytrack.testutil.MainDispatcherRule
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.transaction.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BuildTransactionExportUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun invoke_usesSelectedDateRangeToQueryTransactions() = runTest {
        val currentTimeProvider = mockk<CurrentTimeProvider>()
        every { currentTimeProvider.now() } returns 1_774_692_000_000L
        val repository = FakeTransactionRepository()
        val useCase = BuildTransactionExportUseCase(
            transactionRepository = repository,
            currentTimeProvider = currentTimeProvider,
        )

        useCase(ExportDateRange.LAST_60_DAYS)

        val expectedStart = Instant.ofEpochMilli(1_774_692_000_000L)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .minusDays(59)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        assertEquals(expectedStart, repository.requestedFromEpochMillis)
    }

    @Test
    fun invoke_buildsCsvWithHeaderAndEscapedValues() = runTest {
        val currentTimeProvider = mockk<CurrentTimeProvider>()
        every { currentTimeProvider.now() } returns 1_774_692_000_000L
        val repository = FakeTransactionRepository(
            transactions = listOf(
                TransactionRecord(
                    id = 7,
                    title = "Dinner",
                    note = "Pizza, \"movie\" night",
                    amount = 450.0,
                    type = TransactionRecordType.EXPENSE,
                    category = "Food",
                    occurredAtEpochMillis = 1_774_688_400_000L,
                ),
            ),
        )
        val useCase = BuildTransactionExportUseCase(
            transactionRepository = repository,
            currentTimeProvider = currentTimeProvider,
        )

        val payload = useCase(ExportDateRange.LAST_30_DAYS)

        assertTrue(payload.fileName.startsWith("moneytrack-export-30-days-"))
        assertTrue(payload.content.contains("id,date,time,type,title,category,amount,note"))
        assertTrue(payload.content.contains("7,"))
        assertTrue(payload.content.contains("Dinner"))
        assertTrue(payload.content.contains("\"Pizza, \"\"movie\"\" night\""))
    }

    private class FakeTransactionRepository(
        private val transactions: List<TransactionRecord> = emptyList(),
    ) : TransactionRepository {
        var requestedFromEpochMillis: Long = Long.MIN_VALUE
            private set

        override fun observeTransactions(): Flow<List<TransactionRecord>> = emptyFlow()

        override fun observeRecentTransactions(limit: Int): Flow<List<TransactionRecord>> = emptyFlow()

        override suspend fun getTransactionsFrom(fromEpochMillis: Long): List<TransactionRecord> {
            requestedFromEpochMillis = fromEpochMillis
            return transactions
        }
    }
}
