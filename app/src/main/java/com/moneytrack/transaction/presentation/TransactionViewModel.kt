// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.locale.CurrencyFormatter
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TransactionViewModel @Inject constructor(
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val currencyFormatter: CurrencyFormatter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeTransactionsUseCase().collect { transactions ->
                _uiState.update { state ->
                    state.copy(
                        sections = transactions
                            .filterCurrentMonth()
                            .toTransactionSections(currencyFormatter = currencyFormatter),
                    )
                }
            }
        }
    }
}

data class TransactionUiState(
    val sections: List<TransactionSectionUiState> = emptyList(),
    val monthLabel: String = DEFAULT_MONTH_FILTER_LABEL,
)

data class TransactionSectionUiState(
    val title: String,
    val items: List<TransactionItemUiState>,
)

data class TransactionItemUiState(
    val id: Long,
    val iconRes: Int,
    val title: String,
    val subtitle: String?,
    val amount: String,
    val time: String,
    val type: ui.components.card.transaction.TransactionType,
)

private const val DEFAULT_MONTH_FILTER_LABEL = "Month"
