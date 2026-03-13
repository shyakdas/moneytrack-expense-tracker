// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import com.moneytrack.home.domain.model.Budget
import com.moneytrack.home.domain.repository.BudgetRepository
import com.moneytrack.home.domain.usecase.ObserveBudgetUseCase
import com.moneytrack.home.domain.usecase.UpsertBudgetUseCase
import com.moneytrack.locale.AppCurrencyManager
import com.moneytrack.locale.CountryProvider
import com.moneytrack.locale.CurrencyCatalog
import com.moneytrack.locale.CurrencyFormatter
import com.moneytrack.settings.domain.repository.CurrencyPreferenceRepository
import com.moneytrack.settings.domain.usecase.ObserveAppCurrencyCodeUseCase
import com.moneytrack.settings.domain.usecase.ObserveSelectedCurrencyCodeUseCase
import com.moneytrack.settings.domain.usecase.SaveSelectedCurrencyCodeUseCase
import com.moneytrack.testutil.MainDispatcherRule
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.transaction.domain.repository.TransactionRepository
import com.moneytrack.transaction.domain.usecase.ObserveRecentTransactionsUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun formatCurrency_formatsUsingIndianGrouping() {
        val viewModel = createViewModel(
            budgetRepository = FakeBudgetRepository(),
            transactionRepository = FakeTransactionRepository(),
            countryProvider = FakeCountryProvider(
                countryCode = "IN",
                currencySymbol = "₹",
            ),
        )

        val formatted = viewModel.formatCurrency(1000000.0)

        assertEquals("₹10,00,000", formatted)
    }

    @Test
    fun formatCurrency_formatsUsingUsStyleForUsCountry() {
        val viewModel = createViewModel(
            budgetRepository = FakeBudgetRepository(),
            transactionRepository = FakeTransactionRepository(),
            countryProvider = FakeCountryProvider(
                countryCode = "US",
                currencySymbol = "$",
            ),
        )

        val formatted = viewModel.formatCurrency(1000000.0)

        assertEquals("$1,000,000", formatted)
    }

    @Test
    fun saveBudget_blankDescription_savesNullDescription() = runTest {
        val repository = FakeBudgetRepository()
        val viewModel = createViewModel(
            budgetRepository = repository,
            transactionRepository = FakeTransactionRepository(),
        )

        viewModel.saveBudget(amount = 40000.0, description = "   ")
        advanceUntilIdle()

        assertEquals(40000.0, repository.lastUpsertAmount ?: 0.0, 0.0)
        assertNull(repository.lastUpsertDescription)
    }

    @Test
    fun uiState_withBudgetAndMonthlyExpenses_updatesSummaryValues() = runTest {
        val budgetRepository = FakeBudgetRepository()
        val transactionRepository = FakeTransactionRepository()
        val viewModel = createViewModel(
            budgetRepository = budgetRepository,
            transactionRepository = transactionRepository,
            countryProvider = FakeCountryProvider(
                countryCode = "IN",
                currencySymbol = "₹",
            ),
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        budgetRepository.emitBudget(
            Budget(
                amount = 100000.0,
                description = null,
                updatedAtEpochMillis = 0L,
            ),
        )
        transactionRepository.emitTransactions(
            listOf(
                expenseTransaction(amount = 1500.0, occurredAtEpochMillis = currentMonthTime(dayOfMonth = 5)),
                expenseTransaction(amount = 2500.0, occurredAtEpochMillis = currentMonthTime(dayOfMonth = 10)),
                expenseTransaction(amount = 999.0, occurredAtEpochMillis = previousMonthTime()),
            ),
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasBudget)
        assertTrue(state.hasExpenses)
        assertEquals("₹1,00,000", state.budgetText)
        assertEquals("₹4,000", state.expensesText)
        assertEquals("₹96,000", state.accountBalanceText)
        collectJob.cancel()
    }

    @Test
    fun recentTransactions_areLimitedMappedAndKeepEmptySubtitleHidden() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val viewModel = createViewModel(
            budgetRepository = FakeBudgetRepository(),
            transactionRepository = transactionRepository,
            countryProvider = FakeCountryProvider(
                countryCode = "US",
                currencySymbol = "$",
            ),
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        val records = (1..25).map { index ->
            expenseTransaction(
                amount = index.toDouble(),
                occurredAtEpochMillis = currentMonthTime(dayOfMonth = 1, hour = index % 24),
                note = if (index == 1) "Groceries" else "",
                title = "Expense $index",
            )
        }
        transactionRepository.emitRecentTransactions(records.take(20))
        advanceUntilIdle()

        val transactions = viewModel.uiState.value.transactions
        assertEquals(20, transactions.size)
        assertEquals("Expense 1", transactions.first().title)
        assertEquals("Groceries", transactions.first().subtitle)
        assertNull(transactions[1].subtitle)
        assertEquals("-$1", transactions.first().amount)
        collectJob.cancel()
    }

    @Test
    fun spendFrequency_todayRange_buildsHourlyBuckets() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val viewModel = createViewModel(
            budgetRepository = FakeBudgetRepository(),
            transactionRepository = transactionRepository,
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        transactionRepository.emitTransactions(
            listOf(
                expenseTransaction(
                    amount = 100.0,
                    occurredAtEpochMillis = currentMonthTime(dayOfMonth = todayDay(), hour = 1),
                ),
                expenseTransaction(
                    amount = 200.0,
                    occurredAtEpochMillis = currentMonthTime(dayOfMonth = todayDay(), hour = 8),
                ),
                expenseTransaction(
                    amount = 300.0,
                    occurredAtEpochMillis = currentMonthTime(dayOfMonth = todayDay(), hour = 19),
                ),
            ),
        )
        viewModel.onTimeRangeSelected("Today")
        advanceUntilIdle()

        val points = viewModel.uiState.value.spendFrequencyPoints
        assertEquals(6, points.size)
        assertEquals(100f, points[0])
        assertEquals(200f, points[2])
        assertEquals(300f, points[4])
        assertTrue(viewModel.uiState.value.hasSpendFrequencyData)
        collectJob.cancel()
    }

    @Test
    fun spendFrequency_yearRange_buildsMonthlyBuckets() = runTest {
        val transactionRepository = FakeTransactionRepository()
        val viewModel = createViewModel(
            budgetRepository = FakeBudgetRepository(),
            transactionRepository = transactionRepository,
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        transactionRepository.emitTransactions(
            listOf(
                expenseTransaction(
                    amount = 120.0,
                    occurredAtEpochMillis = timeInCurrentYear(month = 0, dayOfMonth = 5),
                ),
                expenseTransaction(
                    amount = 80.0,
                    occurredAtEpochMillis = timeInCurrentYear(month = 2, dayOfMonth = 12),
                ),
            ),
        )
        viewModel.onTimeRangeSelected("Year")
        advanceUntilIdle()

        val points = viewModel.uiState.value.spendFrequencyPoints
        assertEquals(12, points.size)
        assertEquals(120f, points[0])
        assertEquals(80f, points[2])
        collectJob.cancel()
    }

    @Test
    fun onSelections_updatesUiStateValues() = runTest {
        val viewModel = createViewModel(
            budgetRepository = FakeBudgetRepository(),
            transactionRepository = FakeTransactionRepository(),
            countryProvider = FakeCountryProvider(
                countryCode = "IN",
                currencySymbol = "₹",
            ),
        )
        val collectJob = launch { viewModel.uiState.collect { } }

        viewModel.onBottomRouteSelected("budget")
        viewModel.onTimeRangeSelected("Month")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("budget", state.selectedBottomRoute)
        assertEquals("Month", state.selectedRange)
        collectJob.cancel()
    }

    private fun createViewModel(
        budgetRepository: FakeBudgetRepository,
        transactionRepository: FakeTransactionRepository,
        countryProvider: CountryProvider = FakeCountryProvider(
            countryCode = "IN",
            currencySymbol = "₹",
        ),
    ): HomeViewModel {
        val currencyCatalog = CurrencyCatalog()
        val currencyPreferenceRepository = FakeCurrencyPreferenceRepository()
        val appCurrencyManager = AppCurrencyManager(
            observeSelectedCurrencyCodeUseCase = ObserveSelectedCurrencyCodeUseCase(
                currencyPreferenceRepository = currencyPreferenceRepository,
            ),
            saveSelectedCurrencyCodeUseCase = SaveSelectedCurrencyCodeUseCase(
                currencyPreferenceRepository = currencyPreferenceRepository,
            ),
            countryProvider = countryProvider,
            currencyCatalog = currencyCatalog,
        )
        val currencyFormatter = CurrencyFormatter(
            appCurrencyManager = appCurrencyManager,
            currencyCatalog = currencyCatalog,
        )
        return HomeViewModel(
            observeBudgetUseCase = ObserveBudgetUseCase(budgetRepository),
            observeTransactionsUseCase = ObserveTransactionsUseCase(transactionRepository),
            observeRecentTransactionsUseCase = ObserveRecentTransactionsUseCase(transactionRepository),
            observeAppCurrencyCodeUseCase = ObserveAppCurrencyCodeUseCase(appCurrencyManager),
            upsertBudgetUseCase = UpsertBudgetUseCase(budgetRepository),
            currencyFormatter = currencyFormatter,
        )
    }

    private class FakeBudgetRepository : BudgetRepository {
        private val budgetFlow = MutableStateFlow<Budget?>(null)
        var lastUpsertAmount: Double? = null
        var lastUpsertDescription: String? = null

        override fun observeBudget(): Flow<Budget?> = budgetFlow.asStateFlow()

        override suspend fun upsertBudget(
            amount: Double,
            description: String?,
        ) {
            lastUpsertAmount = amount
            lastUpsertDescription = description
            budgetFlow.value = Budget(
                amount = amount,
                description = description,
                updatedAtEpochMillis = 0L,
            )
        }

        fun emitBudget(budget: Budget?) {
            budgetFlow.value = budget
        }
    }

    private class FakeTransactionRepository : TransactionRepository {
        private val transactionsFlow = MutableStateFlow<List<TransactionRecord>>(emptyList())
        private val recentTransactionsFlow = MutableStateFlow<List<TransactionRecord>>(emptyList())

        override fun observeTransactions(): Flow<List<TransactionRecord>> = transactionsFlow.asStateFlow()

        override fun observeRecentTransactions(limit: Int): Flow<List<TransactionRecord>> =
            recentTransactionsFlow.asStateFlow()

        fun emitTransactions(transactions: List<TransactionRecord>) {
            transactionsFlow.value = transactions
        }

        fun emitRecentTransactions(transactions: List<TransactionRecord>) {
            recentTransactionsFlow.value = transactions
        }
    }

    private class FakeCountryProvider(
        private val countryCode: String,
        private val currencySymbol: String,
    ) : CountryProvider {
        override fun getCountryCode(): String = countryCode
        override fun getCurrencySymbol(): String = currencySymbol
    }

    private class FakeCurrencyPreferenceRepository : CurrencyPreferenceRepository {
        private val selectedCurrencyCode = MutableStateFlow<String?>(null)

        override fun observeSelectedCurrencyCode(): Flow<String?> = selectedCurrencyCode.asStateFlow()

        override suspend fun saveSelectedCurrencyCode(currencyCode: String) {
            selectedCurrencyCode.value = currencyCode
        }
    }
}

private fun expenseTransaction(
    amount: Double,
    occurredAtEpochMillis: Long,
    note: String? = "Note",
    title: String = "Expense",
): TransactionRecord = TransactionRecord(
    id = occurredAtEpochMillis,
    title = title,
    note = note,
    amount = amount,
    type = TransactionRecordType.EXPENSE,
    category = "Food",
    occurredAtEpochMillis = occurredAtEpochMillis,
)

private fun currentMonthTime(
    dayOfMonth: Int,
    hour: Int = 10,
): Long = Calendar.getInstance().apply {
    set(Calendar.DAY_OF_MONTH, dayOfMonth.coerceAtMost(getActualMaximum(Calendar.DAY_OF_MONTH)))
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

private fun timeInCurrentYear(
    month: Int,
    dayOfMonth: Int,
): Long = Calendar.getInstance().apply {
    set(Calendar.MONTH, month)
    set(Calendar.DAY_OF_MONTH, dayOfMonth)
    set(Calendar.HOUR_OF_DAY, 10)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun todayDay(): Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
