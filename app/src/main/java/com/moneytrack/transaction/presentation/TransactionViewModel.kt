// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.locale.CurrencyFormatter
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
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val currencyFormatter: CurrencyFormatter,
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    private val _selectedCurrencyCode = MutableStateFlow(currencyFormatter.currentCurrencyCode())
    private val _selectedMonth = MutableStateFlow(currentTransactionMonthOption())
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
    }

    private fun updateUiState() {
        _uiState.update { state ->
            val selectedMonth = _selectedMonth.value
            state.copy(
                sections = _transactions.value
                    .filterByMonth(selectedMonth)
                    .toTransactionSections(
                        currencyFormatter = currencyFormatter,
                        currencyCode = _selectedCurrencyCode.value,
                    ),
                selectedMonth = selectedMonth,
                monthOptions = transactionMonthOptions(selectedMonth.year),
                yearOptions = transactionYearOptions(),
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
