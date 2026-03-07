package com.moneytrack.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.home.domain.model.Budget
import com.moneytrack.home.domain.usecase.ObserveBudgetUseCase
import com.moneytrack.home.domain.usecase.UpsertBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
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
    private val upsertBudgetUseCase: UpsertBudgetUseCase,
) : ViewModel() {

    private val accountBalance = 0.0
    private val expenses = 0.0
    private val _budget = MutableStateFlow<Budget?>(null)
    val budget: StateFlow<Budget?> = _budget.asStateFlow()
    private val _isBudgetLoaded = MutableStateFlow(false)
    val isBudgetLoaded: StateFlow<Boolean> = _isBudgetLoaded.asStateFlow()
    private val _selectedBottomRoute = MutableStateFlow(DEFAULT_BOTTOM_ROUTE)
    private val _selectedRange = MutableStateFlow(DEFAULT_TIME_RANGE)

    val uiState: StateFlow<HomeUiState> = combine(
        _budget,
        _selectedBottomRoute,
        _selectedRange,
    ) { budgetState, selectedBottomRoute, selectedRange ->
        HomeUiState(
            accountBalanceText = formatCurrency(accountBalance),
            hasBudget = budgetState != null,
            budgetText = budgetState?.amount?.let(::formatCurrency),
            hasExpenses = expenses > 0.0,
            expensesText = formatCurrency(expenses),
            transactions = emptyList(),
            selectedBottomRoute = selectedBottomRoute,
            selectedRange = selectedRange,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(
            accountBalanceText = formatCurrency(accountBalance),
            hasBudget = false,
            budgetText = null,
            hasExpenses = false,
            expensesText = formatCurrency(expenses),
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
        return "$${indianNumberFormatter.format(value.toLong())}"
    }

    private companion object {
        private const val DEFAULT_BOTTOM_ROUTE = "home"
        private const val DEFAULT_TIME_RANGE = "Today"

        val indianNumberFormatter: NumberFormat = NumberFormat.getIntegerInstance(
            Locale.Builder()
                .setLanguage("en")
                .setRegion("IN")
                .build(),
        )
    }
}
