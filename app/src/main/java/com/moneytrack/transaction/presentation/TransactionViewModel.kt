// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.locale.CurrencyFormatter
import com.moneytrack.transaction.domain.model.TransactionRecord
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.transaction.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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
                            .groupIntoSections(currencyFormatter = currencyFormatter),
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

private fun List<TransactionRecord>.filterCurrentMonth(): List<TransactionRecord> {
    val now = Calendar.getInstance()
    return filter { transaction ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = transaction.occurredAtEpochMillis
        }
        calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)
    }
}

private fun List<TransactionRecord>.groupIntoSections(
    currencyFormatter: CurrencyFormatter,
): List<TransactionSectionUiState> {
    return sortedByDescending(TransactionRecord::occurredAtEpochMillis)
        .groupBy { transaction ->
            sectionTitleFor(transaction.occurredAtEpochMillis)
        }
        .map { (title, items) ->
        TransactionSectionUiState(
            title = title,
            items = items.map { transaction ->
                transaction.toUiState(currencyFormatter = currencyFormatter)
            },
        )
        }
}

private fun TransactionRecord.toUiState(
    currencyFormatter: CurrencyFormatter,
): TransactionItemUiState {
    val isExpense = type == TransactionRecordType.EXPENSE
    val signedAmount = if (isExpense) -amount else amount
    return TransactionItemUiState(
        id = id,
        iconRes = category.iconRes(),
        title = title,
        subtitle = note?.trim()?.takeIf(String::isNotEmpty),
        amount = currencyFormatter.format(signedAmount),
        time = timeFormatter().format(occurredAtEpochMillis),
        type = if (isExpense) {
            ui.components.card.transaction.TransactionType.EXPENSE
        } else {
            ui.components.card.transaction.TransactionType.INCOME
        },
    )
}

private fun sectionTitleFor(epochMillis: Long): String {
    val transactionCalendar = Calendar.getInstance().apply { timeInMillis = epochMillis }
    val todayCalendar = Calendar.getInstance()
    val yesterdayCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        transactionCalendar.isSameDayAs(todayCalendar) -> TODAY_LABEL
        transactionCalendar.isSameDayAs(yesterdayCalendar) -> YESTERDAY_LABEL
        else -> sectionDateFormatter().format(epochMillis)
    }
}

private fun String.iconRes(): Int {
    val normalized = lowercase(Locale.getDefault())
    return when {
        normalized.contains("shop") -> com.moneytrack.designsystem.R.drawable.shopping_bag
        normalized.contains("sub") -> com.moneytrack.designsystem.R.drawable.recurring_bill
        normalized.contains("food") || normalized.contains("meal") || normalized.contains("restaurant") ->
            com.moneytrack.designsystem.R.drawable.restaurant
        normalized.contains("transport") || normalized.contains("travel") || normalized.contains("car") ->
            com.moneytrack.designsystem.R.drawable.car
        normalized.contains("salary") || normalized.contains("income") ->
            com.moneytrack.designsystem.R.drawable.salary
        else -> com.moneytrack.designsystem.R.drawable.expense
    }
}

private fun Calendar.isSameDayAs(other: Calendar): Boolean =
    get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
        get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)

private fun sectionDateFormatter(): SimpleDateFormat =
    SimpleDateFormat(SECTION_DATE_PATTERN, Locale.getDefault())

private fun timeFormatter(): SimpleDateFormat =
    SimpleDateFormat(TIME_PATTERN, Locale.getDefault())

private const val DEFAULT_MONTH_FILTER_LABEL = "Month"
private const val TODAY_LABEL = "Today"
private const val YESTERDAY_LABEL = "Yesterday"
private const val SECTION_DATE_PATTERN = "dd MMM yyyy"
private const val TIME_PATTERN = "hh:mm a"
