// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.home.domain.model.Budget
import com.moneytrack.home.domain.usecase.ObserveBudgetUseCase
import com.moneytrack.home.domain.usecase.UpsertBudgetUseCase
import com.moneytrack.locale.CurrencyFormatter
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.settings.domain.usecase.ObserveAppCurrencyCodeUseCase
import com.moneytrack.transaction.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    observeBudgetUseCase: ObserveBudgetUseCase,
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    observeAppCurrencyCodeUseCase: ObserveAppCurrencyCodeUseCase,
    private val upsertBudgetUseCase: UpsertBudgetUseCase,
    private val currencyFormatter: CurrencyFormatter,
) : ViewModel() {

    private val _budget = MutableStateFlow<Budget?>(null)
    val budget: StateFlow<Budget?> = _budget.asStateFlow()
    private val _isBudgetLoaded = MutableStateFlow(false)
    val isBudgetLoaded: StateFlow<Boolean> = _isBudgetLoaded.asStateFlow()
    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    private val _selectedBottomRoute = MutableStateFlow(DEFAULT_BOTTOM_ROUTE)
    private val _selectedRange = MutableStateFlow(DEFAULT_TIME_RANGE)
    private val _selectedMonth = MutableStateFlow(currentHomeMonthOption())
    private val _selectedCurrencyCode = MutableStateFlow(currencyFormatter.currentCurrencyCode())

    private val homeSummaryInputs = combine(
        _budget,
        _transactions,
        _selectedBottomRoute,
        _selectedMonth,
    ) { budgetState, transactions, selectedBottomRoute, selectedMonth ->
        HomeSummaryInputs(
            budget = budgetState,
            transactions = transactions,
            selectedBottomRoute = selectedBottomRoute,
            selectedMonth = selectedMonth,
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        homeSummaryInputs,
        _selectedRange,
        _selectedCurrencyCode,
    ) { inputs, selectedRange, selectedCurrencyCode ->
        val budgetState = inputs.budget
        val transactions = inputs.transactions
        val selectedMonth = inputs.selectedMonth
        val selectedMonthTransactions = transactions
            .filterByMonth(selectedMonth)
            .sortedByDescending { transaction -> transaction.occurredAtEpochMillis }
        val monthlyExpenses = selectedMonthTransactions
            .filter { transaction -> transaction.type == TransactionRecordType.EXPENSE }
            .sumOf { transaction -> transaction.amount }
        val selectedBottomRoute = inputs.selectedBottomRoute
        val accountBalance = (budgetState?.amount ?: 0.0) - monthlyExpenses
        val spendFrequencyPoints = transactions.toSpendFrequencyPoints(
            selectedRange = selectedRange,
            selectedMonth = selectedMonth,
        )
        HomeUiState(
            accountBalanceText = formatCurrency(accountBalance, selectedCurrencyCode),
            hasBudget = budgetState != null,
            budgetAmount = budgetState?.amount,
            budgetText = budgetState?.amount?.let { amount ->
                formatCurrency(amount, selectedCurrencyCode)
            },
            hasExpenses = monthlyExpenses > 0.0,
            expensesAmount = monthlyExpenses,
            expensesText = formatCurrency(monthlyExpenses, selectedCurrencyCode),
            spendFrequencyPoints = spendFrequencyPoints,
            hasSpendFrequencyData = spendFrequencyPoints.any { point -> point > 0f },
            transactions = selectedMonthTransactions
                .take(HOME_RECENT_TRANSACTION_LIMIT)
                .map { transaction ->
                    transaction.toHomeTransaction(
                        selectedCurrencyCode = selectedCurrencyCode,
                        currencyFormatter = currencyFormatter,
                    )
                },
            selectedBottomRoute = selectedBottomRoute,
            selectedRange = selectedRange,
            selectedMonth = selectedMonth,
            monthOptions = homeMonthOptions(selectedMonth.year),
            yearOptions = homeYearOptions(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(WHILE_SUBSCRIBED_TIMEOUT_MS),
        initialValue = HomeUiState(
            accountBalanceText = formatCurrency(0.0),
            hasBudget = false,
            budgetAmount = null,
            budgetText = null,
            hasExpenses = false,
            expensesAmount = 0.0,
            expensesText = formatCurrency(0.0),
            spendFrequencyPoints = emptyList(),
            hasSpendFrequencyData = false,
            transactions = emptyList(),
            selectedBottomRoute = DEFAULT_BOTTOM_ROUTE,
            selectedRange = DEFAULT_TIME_RANGE,
            selectedMonth = currentHomeMonthOption(),
            monthOptions = homeMonthOptions(),
            yearOptions = homeYearOptions(),
        ),
    )

    init {
        viewModelScope.launch {
            _selectedMonth.flatMapLatest { selectedMonth ->
                observeBudgetUseCase(
                    month = selectedMonth.monthIndex + 1,
                    year = selectedMonth.year,
                )
            }.collect { budget ->
                _budget.update { budget }
                _isBudgetLoaded.update { true }
            }
        }

        viewModelScope.launch {
            observeTransactionsUseCase().collect { transactions ->
                _transactions.update { transactions }
            }
        }

        viewModelScope.launch {
            observeAppCurrencyCodeUseCase().collect { currencyCode ->
                _selectedCurrencyCode.update { currencyCode }
            }
        }
    }

    fun saveBudget(
        month: Int,
        year: Int,
        amount: Double,
        description: String?,
    ) {
        viewModelScope.launch {
            upsertBudgetUseCase(
                month = month,
                year = year,
                amount = amount,
                description = description?.takeIf { it.isNotBlank() },
            )
        }
    }

    fun onBottomRouteSelected(route: String) {
        _selectedBottomRoute.update { route }
    }

    fun onTimeRangeSelected(range: String) {
        _selectedRange.update { range }
    }

    fun onMonthSelected(month: HomeMonthOption) {
        _selectedMonth.update { month }
    }

    fun onYearSelected(year: Int) {
        _selectedMonth.update { month ->
            month.copy(year = year)
        }
    }

    fun formatCurrency(value: Double): String {
        return formatCurrency(value, _selectedCurrencyCode.value)
    }

    private fun formatCurrency(
        value: Double,
        currencyCode: String,
    ): String = currencyFormatter.format(value = value, currencyCode = currencyCode)

    private companion object {
        private const val DEFAULT_BOTTOM_ROUTE = "home"
        private const val DEFAULT_TIME_RANGE = "Today"
        private const val HOME_RECENT_TRANSACTION_LIMIT = 20
        private const val WHILE_SUBSCRIBED_TIMEOUT_MS = 5_000L
    }
}

private data class HomeSummaryInputs(
    val budget: Budget?,
    val transactions: List<TransactionRecord>,
    val selectedBottomRoute: String,
    val selectedMonth: HomeMonthOption,
)

private fun List<TransactionRecord>.toSpendFrequencyPoints(
    selectedRange: String,
    selectedMonth: HomeMonthOption,
): List<Float> {
    val expenseTransactions = filter { transaction ->
        transaction.type == TransactionRecordType.EXPENSE
    }
    return when (selectedRange) {
        RANGE_TODAY -> expenseTransactions.aggregateToday()
        RANGE_WEEK -> expenseTransactions.aggregateWeek()
        RANGE_MONTH -> expenseTransactions.aggregateMonth(selectedMonth)
        RANGE_YEAR -> expenseTransactions.aggregateYear()
        else -> expenseTransactions.aggregateToday()
    }
}

private fun List<TransactionRecord>.aggregateToday(): List<Float> {
    val now = Calendar.getInstance()
    val buckets = MutableList(TODAY_BUCKET_COUNT) { 0f }
    forEach { transaction ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = transaction.occurredAtEpochMillis
        }
        if (calendar.isSameDayAs(now)) {
            val bucketIndex = calendar.get(Calendar.HOUR_OF_DAY) / TODAY_BUCKET_HOURS
            buckets[bucketIndex] += transaction.amount.toFloat()
        }
    }
    return buckets
}

private fun List<TransactionRecord>.aggregateWeek(): List<Float> {
    val today = Calendar.getInstance().startOfDay()
    val buckets = MutableList(WEEK_DAY_COUNT) { 0f }
    forEach { transaction ->
        val transactionDay = Calendar.getInstance().apply {
            timeInMillis = transaction.occurredAtEpochMillis
        }.startOfDay()
        val dayDiff = ((today.timeInMillis - transactionDay.timeInMillis) / MILLIS_PER_DAY).toInt()
        if (dayDiff in 0 until WEEK_DAY_COUNT) {
            val bucketIndex = WEEK_DAY_COUNT - 1 - dayDiff
            buckets[bucketIndex] += transaction.amount.toFloat()
        }
    }
    return buckets
}

private fun List<TransactionRecord>.aggregateMonth(selectedMonth: HomeMonthOption): List<Float> {
    val monthCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, selectedMonth.year)
        set(Calendar.MONTH, selectedMonth.monthIndex)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val buckets = MutableList(daysInMonth) { 0f }
    forEach { transaction ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = transaction.occurredAtEpochMillis
        }
        if (calendar.isSameMonthAs(selectedMonth)) {
            val bucketIndex = calendar.get(Calendar.DAY_OF_MONTH) - 1
            buckets[bucketIndex] += transaction.amount.toFloat()
        }
    }
    return buckets
}

private fun List<TransactionRecord>.aggregateYear(): List<Float> {
    val now = Calendar.getInstance()
    val buckets = MutableList(YEAR_MONTH_COUNT) { 0f }
    forEach { transaction ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = transaction.occurredAtEpochMillis
        }
        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            val bucketIndex = calendar.get(Calendar.MONTH)
            buckets[bucketIndex] += transaction.amount.toFloat()
        }
    }
    return buckets
}

private fun List<TransactionRecord>.filterByMonth(selectedMonth: HomeMonthOption): List<TransactionRecord> {
    return filter { transaction ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = transaction.occurredAtEpochMillis
        }
        calendar.isSameMonthAs(selectedMonth)
    }
}

private fun Calendar.startOfDay(): Calendar = apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun Calendar.isSameDayAs(other: Calendar): Boolean =
    get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
        get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)

private fun Calendar.isSameMonthAs(month: HomeMonthOption): Boolean =
    get(Calendar.YEAR) == month.year &&
        get(Calendar.MONTH) == month.monthIndex

private const val RANGE_TODAY = "Today"
private const val RANGE_WEEK = "Week"
private const val RANGE_MONTH = "Month"
private const val RANGE_YEAR = "Year"
private const val TODAY_BUCKET_COUNT = 6
private const val TODAY_BUCKET_HOURS = 4
private const val WEEK_DAY_COUNT = 7
private const val YEAR_MONTH_COUNT = 12
private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
