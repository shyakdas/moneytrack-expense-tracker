// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.presentation

import com.moneytrack.locale.CountryProvider
import com.moneytrack.locale.CurrencyFormatter
import com.moneytrack.testutil.MainDispatcherRule
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.transaction.domain.repository.TransactionRepository
import com.moneytrack.transaction.domain.usecase.ObserveTransactionsUseCase
import java.util.Calendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun transactions_areGroupedIntoTodayYesterdayAndDateSections() = runTest {
        val repository = FakeTransactionRepository()
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        repository.emitTransactions(
            listOf(
                expenseTransaction(
                    id = 1L,
                    title = "Food",
                    amount = 120.0,
                    occurredAtEpochMillis = currentMonthTime(dayOffset = 0, hour = 12),
                ),
                expenseTransaction(
                    id = 2L,
                    title = "Transport",
                    amount = 80.0,
                    occurredAtEpochMillis = currentMonthTime(dayOffset = -1, hour = 8),
                ),
                incomeTransaction(
                    id = 3L,
                    title = "Salary",
                    amount = 5000.0,
                    occurredAtEpochMillis = currentMonthTime(dayOffset = -2, hour = 10),
                ),
            ),
        )
        advanceUntilIdle()

        val sections = viewModel.uiState.value.sections
        assertEquals(listOf("Today", "Yesterday", dateLabelFor(dayOffset = -2)), sections.map { it.title })
        assertEquals("Food", sections[0].items.first().title)
        assertEquals("Transport", sections[1].items.first().title)
        assertEquals("Salary", sections[2].items.first().title)
        collectJob.cancel()
    }

    @Test
    fun previousMonthTransactions_areFilteredOut() = runTest {
        val repository = FakeTransactionRepository()
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        repository.emitTransactions(
            listOf(
                expenseTransaction(
                    id = 1L,
                    title = "Current",
                    amount = 120.0,
                    occurredAtEpochMillis = currentMonthTime(dayOffset = 0),
                ),
                expenseTransaction(
                    id = 2L,
                    title = "Old",
                    amount = 80.0,
                    occurredAtEpochMillis = previousMonthTime(),
                ),
            ),
        )
        advanceUntilIdle()

        val sections = viewModel.uiState.value.sections
        assertEquals(1, sections.size)
        assertEquals("Current", sections.first().items.first().title)
        collectJob.cancel()
    }

    @Test
    fun amountFormatting_usesCountryCurrencyAndKeepsExpenseNegative() = runTest {
        val repository = FakeTransactionRepository()
        val viewModel = createViewModel(
            repository = repository,
            countryProvider = FakeCountryProvider(
                countryCode = "IN",
                currencySymbol = "₹",
            ),
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        repository.emitTransactions(
            listOf(
                expenseTransaction(
                    id = 1L,
                    title = "Expense",
                    amount = 1200.0,
                    occurredAtEpochMillis = currentMonthTime(dayOffset = 0),
                ),
                incomeTransaction(
                    id = 2L,
                    title = "Income",
                    amount = 100000.0,
                    occurredAtEpochMillis = currentMonthTime(dayOffset = 0, hour = 14),
                ),
            ),
        )
        advanceUntilIdle()

        val items = viewModel.uiState.value.sections.first().items
        assertEquals("-₹1,200", items.first { it.title == "Expense" }.amount)
        assertEquals("₹1,00,000", items.first { it.title == "Income" }.amount)
        collectJob.cancel()
    }

    @Test
    fun blankNote_isHiddenInUiState() = runTest {
        val repository = FakeTransactionRepository()
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        repository.emitTransactions(
            listOf(
                expenseTransaction(
                    id = 1L,
                    title = "Expense",
                    note = "   ",
                    amount = 100.0,
                    occurredAtEpochMillis = currentMonthTime(dayOffset = 0),
                ),
            ),
        )
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.sections.first().items.first().subtitle)
        collectJob.cancel()
    }

    @Test
    fun categoryIconMapping_usesExpectedIcons() {
        assertEquals(
            com.moneytrack.designsystem.R.drawable.shopping_bag,
            "Shopping".toTransactionIconRes(),
        )
        assertEquals(
            com.moneytrack.designsystem.R.drawable.restaurant,
            "Food".toTransactionIconRes(),
        )
        assertEquals(
            com.moneytrack.designsystem.R.drawable.salary,
            "Salary".toTransactionIconRes(),
        )
        assertEquals(
            com.moneytrack.designsystem.R.drawable.expense,
            "Other".toTransactionIconRes(),
        )
    }

    @Test
    fun monthLabel_defaultsToMonth() {
        val viewModel = createViewModel(FakeTransactionRepository())

        assertEquals("Month", viewModel.uiState.value.monthLabel)
        assertTrue(viewModel.uiState.value.sections.isEmpty())
    }

    private fun createViewModel(
        repository: FakeTransactionRepository,
        countryProvider: CountryProvider = FakeCountryProvider(
            countryCode = "US",
            currencySymbol = "$",
        ),
    ): TransactionViewModel {
        return TransactionViewModel(
            observeTransactionsUseCase = ObserveTransactionsUseCase(repository),
            currencyFormatter = CurrencyFormatter(countryProvider),
        )
    }

    private class FakeTransactionRepository : TransactionRepository {
        private val transactionsFlow = MutableStateFlow<List<TransactionRecord>>(emptyList())

        override fun observeTransactions(): Flow<List<TransactionRecord>> = transactionsFlow.asStateFlow()

        override fun observeRecentTransactions(limit: Int): Flow<List<TransactionRecord>> =
            transactionsFlow.asStateFlow()

        fun emitTransactions(transactions: List<TransactionRecord>) {
            transactionsFlow.value = transactions
        }
    }

    private class FakeCountryProvider(
        private val countryCode: String,
        private val currencySymbol: String,
    ) : CountryProvider {
        override fun getCountryCode(): String = countryCode
        override fun getCurrencySymbol(): String = currencySymbol
    }
}

private fun expenseTransaction(
    id: Long,
    title: String,
    amount: Double,
    occurredAtEpochMillis: Long,
    note: String? = "Note",
): TransactionRecord = TransactionRecord(
    id = id,
    title = title,
    note = note,
    amount = amount,
    type = TransactionRecordType.EXPENSE,
    category = title,
    occurredAtEpochMillis = occurredAtEpochMillis,
)

private fun incomeTransaction(
    id: Long,
    title: String,
    amount: Double,
    occurredAtEpochMillis: Long,
    note: String? = "Salary",
): TransactionRecord = TransactionRecord(
    id = id,
    title = title,
    note = note,
    amount = amount,
    type = TransactionRecordType.INCOME,
    category = title,
    occurredAtEpochMillis = occurredAtEpochMillis,
)

private fun currentMonthTime(
    dayOffset: Int,
    hour: Int = 10,
): Long = Calendar.getInstance().apply {
    add(Calendar.DAY_OF_MONTH, dayOffset)
    set(Calendar.HOUR_OF_DAY, hour)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun previousMonthTime(): Long = Calendar.getInstance().apply {
    add(Calendar.MONTH, -1)
    set(Calendar.DAY_OF_MONTH, 5)
    set(Calendar.HOUR_OF_DAY, 10)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun dateLabelFor(dayOffset: Int): String =
    java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(
        currentMonthTime(dayOffset = dayOffset),
    )
