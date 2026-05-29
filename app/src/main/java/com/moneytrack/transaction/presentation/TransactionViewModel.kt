// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.locale.CurrencyFormatter
import com.moneytrack.expense.domain.usecase.ObserveCategoriesUseCase
import com.moneytrack.settings.domain.usecase.ObserveAppCurrencyCodeUseCase
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.usecase.DeleteTransactionUseCase
import com.moneytrack.transaction.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TransactionViewModel @Inject constructor(
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    observeAppCurrencyCodeUseCase: ObserveAppCurrencyCodeUseCase,
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val currencyFormatter: CurrencyFormatter,
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    private val _selectedCurrencyCode = MutableStateFlow(currencyFormatter.currentCurrencyCode())
    private val _selectedMonth = MutableStateFlow(currentTransactionMonthOption())
    private val _selectedSortOption = MutableStateFlow(TransactionSortOption.NEWEST)
    private val _selectedCategory = MutableStateFlow(ALL_CATEGORIES_FILTER)
    private val _categoryOptions = MutableStateFlow(listOf(ALL_CATEGORIES_FILTER))
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeTransactionsUseCase().collect { transactions ->
                _transactions.update { transactions }
                updateUiState()
            }
        }

        viewModelScope.launch {
            observeAppCurrencyCodeUseCase().collect { currencyCode ->
                _selectedCurrencyCode.update { currencyCode }
                updateUiState()
            }
        }

        viewModelScope.launch {
            observeCategoriesUseCase().collect { categories ->
                val options = buildList {
                    add(ALL_CATEGORIES_FILTER)
                    addAll(categories.map { it.name }.distinct())
                }
                _categoryOptions.update { options }
                if (_selectedCategory.value !in options) {
                    _selectedCategory.update { ALL_CATEGORIES_FILTER }
                }
                updateUiState()
            }
        }
    }

    private fun updateUiState() {
        _uiState.update { state ->
            val selectedMonth = _selectedMonth.value
            val selectedSortOption = _selectedSortOption.value
            val selectedCategory = _selectedCategory.value
            state.copy(
                sections = _transactions.value
                    .filterByMonth(selectedMonth)
                    .filterByCategory(selectedCategory)
                    .sortByOption(selectedSortOption)
                    .toTransactionSections(
                        currencyFormatter = currencyFormatter,
                        currencyCode = _selectedCurrencyCode.value,
                    ),
                selectedMonth = selectedMonth,
                monthOptions = transactionMonthOptions(selectedMonth.year),
                yearOptions = transactionYearOptions(),
                selectedSortOption = selectedSortOption,
                selectedCategory = selectedCategory,
                categoryOptions = _categoryOptions.value,
            )
        }
    }

    fun onMonthSelected(month: TransactionMonthOption) {
        _selectedMonth.update { month }
        updateUiState()
    }

    fun onYearSelected(year: Int) {
        _selectedMonth.update { month ->
            val updatedOptions = transactionMonthOptions(year)
            updatedOptions.firstOrNull { option ->
                option.monthIndex == month.monthIndex
            } ?: updatedOptions.firstOrNull() ?: month
        }
        updateUiState()
    }

    fun onSortOptionSelected(option: TransactionSortOption) {
        _selectedSortOption.update { option }
        updateUiState()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.update { category }
        updateUiState()
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase(transactionId)
        }
    }
}

data class TransactionUiState(
    val sections: List<TransactionSectionUiState> = emptyList(),
    val selectedMonth: TransactionMonthOption = currentTransactionMonthOption(),
    val monthOptions: List<TransactionMonthOption> = transactionMonthOptions(currentTransactionMonthOption().year),
    val yearOptions: List<Int> = transactionYearOptions(),
    val selectedSortOption: TransactionSortOption = TransactionSortOption.NEWEST,
    val selectedCategory: String = ALL_CATEGORIES_FILTER,
    val categoryOptions: List<String> = listOf(ALL_CATEGORIES_FILTER),
)

data class TransactionSectionUiState(
    val title: String,
    val items: List<TransactionItemUiState>,
)

data class TransactionItemUiState(
    val id: Long,
    val iconRes: Int,
    val category: String,
    val title: String,
    val subtitle: String?,
    val amount: String,
    val date: String,
    val time: String,
    val type: ui.components.card.transaction.TransactionType,
)
