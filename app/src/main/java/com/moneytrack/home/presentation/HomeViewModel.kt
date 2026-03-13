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
import com.moneytrack.transaction.domain.usecase.ObserveRecentTransactionsUseCase
import com.moneytrack.transaction.presentation.toTransactionIconRes
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeBudgetUseCase: ObserveBudgetUseCase,
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    observeRecentTransactionsUseCase: ObserveRecentTransactionsUseCase,
    observeAppCurrencyCodeUseCase: ObserveAppCurrencyCodeUseCase,
    private val upsertBudgetUseCase: UpsertBudgetUseCase,
    private val currencyFormatter: CurrencyFormatter,
) : ViewModel() {

    private val _budget = MutableStateFlow<Budget?>(null)
    val budget: StateFlow<Budget?> = _budget.asStateFlow()
    private val _isBudgetLoaded = MutableStateFlow(false)
    val isBudgetLoaded: StateFlow<Boolean> = _isBudgetLoaded.asStateFlow()
    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    private val _recentTransactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    private val _monthlyExpenses = MutableStateFlow(0.0)
    private val _selectedBottomRoute = MutableStateFlow(DEFAULT_BOTTOM_ROUTE)
    private val _selectedRange = MutableStateFlow(DEFAULT_TIME_RANGE)
    private val _selectedCurrencyCode = MutableStateFlow(currencyFormatter.currentCurrencyCode())

    private val homeSummaryInputs = combine(
        _budget,
        _transactions,
        _recentTransactions,
        _monthlyExpenses,
        _selectedBottomRoute,
    ) { budgetState, transactions, recentTransactions, monthlyExpenses, selectedBottomRoute ->
        HomeSummaryInputs(
            budget = budgetState,
            transactions = transactions,
            recentTransactions = recentTransactions,
            monthlyExpenses = monthlyExpenses,
            selectedBottomRoute = selectedBottomRoute,
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        homeSummaryInputs,
        _selectedRange,
        _selectedCurrencyCode,
    ) { inputs, selectedRange, selectedCurrencyCode ->
        val budgetState = inputs.budget
        val transactions = inputs.transactions
        val recentTransactions = inputs.recentTransactions
        val monthlyExpenses = inputs.monthlyExpenses
        val selectedBottomRoute = inputs.selectedBottomRoute
        val accountBalance = (budgetState?.amount ?: 0.0) - monthlyExpenses
        val spendFrequencyPoints = transactions.toSpendFrequencyPoints(selectedRange)
        HomeUiState(
            accountBalanceText = formatCurrency(accountBalance, selectedCurrencyCode),
            hasBudget = budgetState != null,
            budgetAmount = budgetState?.amount,
            budgetText = budgetState?.amount?.let { amount ->
                formatCurrency(amount, selectedCurrencyCode)
            },
            hasExpenses = monthlyExpenses > 0.0,
            expensesText = formatCurrency(monthlyExpenses, selectedCurrencyCode),
            spendFrequencyPoints = spendFrequencyPoints,
            hasSpendFrequencyData = spendFrequencyPoints.any { point -> point > 0f },
            transactions = recentTransactions.map { transaction ->
                transaction.toHomeTransaction(
                    selectedCurrencyCode = selectedCurrencyCode,
                    currencyFormatter = currencyFormatter,
                )
            },
            selectedBottomRoute = selectedBottomRoute,
            selectedRange = selectedRange,
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
            expensesText = formatCurrency(0.0),
            spendFrequencyPoints = emptyList(),
            hasSpendFrequencyData = false,
            transactions = emptyList(),
            selectedBottomRoute = DEFAULT_BOTTOM_ROUTE,
            selectedRange = DEFAULT_TIME_RANGE,
        ),
    )

    init {
        viewModelScope.launch {
            observeBudgetUseCase().collect { budget ->
                _budget.update { budget }
                _isBudgetLoaded.update { true }
            }
        }

        viewModelScope.launch {
            observeTransactionsUseCase().collect { transactions ->
                _transactions.update { transactions }
                val monthlyExpenses = transactions
                    .filterCurrentMonth()
                    .filter { transaction -> transaction.type == TransactionRecordType.EXPENSE }
                    .sumOf { transaction -> transaction.amount }
                _monthlyExpenses.update { monthlyExpenses }
            }
        }

        viewModelScope.launch {
            observeRecentTransactionsUseCase(HOME_RECENT_TRANSACTION_LIMIT).collect { transactions ->
                _recentTransactions.update { transactions }
            }
        }

        viewModelScope.launch {
            observeAppCurrencyCodeUseCase().collect { currencyCode ->
                _selectedCurrencyCode.update { currencyCode }
            }
        }
    }

    fun saveBudget(
        amount: Double,
        description: String?,
    ) {
        viewModelScope.launch {
            upsertBudgetUseCase(
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

private fun formatTransactionTime(epochMillis: Long): String =
    SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(epochMillis)

private data class HomeSummaryInputs(
    val budget: Budget?,
    val transactions: List<TransactionRecord>,
    val recentTransactions: List<TransactionRecord>,
    val monthlyExpenses: Double,
    val selectedBottomRoute: String,
)

private fun TransactionRecord.toHomeTransaction(
    selectedCurrencyCode: String,
    currencyFormatter: CurrencyFormatter,
): HomeTransaction {
    val isExpense = type == TransactionRecordType.EXPENSE
    return HomeTransaction(
        icon = category.toTransactionIconRes(),
        title = title,
        subtitle = note?.trim()?.takeIf(String::isNotEmpty),
        amount = currencyFormatter.format(
            value = if (isExpense) -amount else amount,
            currencyCode = selectedCurrencyCode,
        ),
        time = formatTransactionTime(occurredAtEpochMillis),
        type = if (isExpense) {
            ui.components.card.transaction.TransactionType.EXPENSE
        } else {
            ui.components.card.transaction.TransactionType.INCOME
        },
    )
}

private fun List<TransactionRecord>.toSpendFrequencyPoints(selectedRange: String): List<Float> {
    val expenseTransactions = filter { transaction ->
        transaction.type == TransactionRecordType.EXPENSE
    }
    return when (selectedRange) {
        RANGE_TODAY -> expenseTransactions.aggregateToday()
        RANGE_WEEK -> expenseTransactions.aggregateWeek()
        RANGE_MONTH -> expenseTransactions.aggregateMonth()
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

private fun List<TransactionRecord>.aggregateMonth(): List<Float> {
    val now = Calendar.getInstance()
    val daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH)
    val buckets = MutableList(daysInMonth) { 0f }
    forEach { transaction ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = transaction.occurredAtEpochMillis
        }
        if (
            calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)
        ) {
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

private fun List<com.moneytrack.transaction.domain.model.TransactionRecord>.filterCurrentMonth():
    List<com.moneytrack.transaction.domain.model.TransactionRecord> {
    val now = Calendar.getInstance()
    return filter { transaction ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = transaction.occurredAtEpochMillis
        }
        calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)
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

private const val TIME_PATTERN = "hh:mm a"
private const val RANGE_TODAY = "Today"
private const val RANGE_WEEK = "Week"
private const val RANGE_MONTH = "Month"
private const val RANGE_YEAR = "Year"
private const val TODAY_BUCKET_COUNT = 6
private const val TODAY_BUCKET_HOURS = 4
private const val WEEK_DAY_COUNT = 7
private const val YEAR_MONTH_COUNT = 12
private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
