// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.expense.domain.model.ExpenseSubmissionResult
import com.moneytrack.expense.domain.model.ExpenseCategory
import com.moneytrack.expense.domain.model.RepeatFrequency
import com.moneytrack.expense.domain.model.RepeatSchedule
import com.moneytrack.expense.domain.model.SubmitExpenseRequest
import com.moneytrack.expense.domain.usecase.EnsureDefaultCategoriesUseCase
import com.moneytrack.expense.domain.usecase.ObserveCategoriesUseCase
import com.moneytrack.expense.domain.usecase.SubmitExpenseUseCase
import com.moneytrack.locale.CurrencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val ensureDefaultCategoriesUseCase: EnsureDefaultCategoriesUseCase,
    private val submitExpenseUseCase: SubmitExpenseUseCase,
    private val currencyFormatter: CurrencyFormatter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ExpenseUiState(
            amountText = currencyFormatter.format(DEFAULT_AMOUNT_VALUE),
        ),
    )
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()
    private val _events = Channel<ExpenseEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            ensureDefaultCategoriesUseCase()
        }

        viewModelScope.launch {
            observeCategoriesUseCase().collect { categories ->
                _uiState.update { state ->
                    val selectedCategoryId = when {
                        state.selectedCategoryId == null -> null
                        categories.any { it.id == state.selectedCategoryId } -> state.selectedCategoryId
                        else -> null
                    }
                    state.copy(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        selectedCategory = categories.firstOrNull { category ->
                            category.id == selectedCategoryId
                        },
                    )
                }
            }
        }
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.update { state ->
            state.copy(
                selectedCategoryId = categoryId,
                selectedCategory = state.categories.firstOrNull { category ->
                    category.id == categoryId
                },
            )
        }
    }

    fun onDescriptionChanged(input: String) {
        _uiState.update { state ->
            state.copy(description = input)
        }
    }

    fun onAttachmentSelected(
        uriString: String,
        name: String,
        type: ExpenseAttachmentType,
    ) {
        _uiState.update { state ->
            state.copy(
                attachment = ExpenseAttachmentUiState(
                    uriString = uriString,
                    name = name,
                    type = type,
                ),
            )
        }
    }

    fun onAttachmentRemoved() {
        _uiState.update { state ->
            state.copy(attachment = null)
        }
    }

    fun onRepeatConfigured(
        frequency: RepeatFrequency,
        endAtEpochMillis: Long,
    ) {
        _uiState.update { state ->
            state.copy(
                repeatSchedule = ExpenseRepeatUiState(
                    frequency = frequency,
                    endAtEpochMillis = endAtEpochMillis,
                ),
            )
        }
    }

    fun onRepeatRemoved() {
        _uiState.update { state ->
            state.copy(repeatSchedule = null)
        }
    }

    fun onAmountChanged(input: String) {
        val digitsOnly = input.filter(Char::isDigit).take(MAX_AMOUNT_LENGTH)
        val amountValue = digitsOnly.toDoubleOrNull() ?: DEFAULT_AMOUNT_VALUE
        _uiState.update { state ->
            state.copy(
                amountInput = digitsOnly,
                amountText = currencyFormatter.format(amountValue),
            )
        }
    }

    fun submitExpense() {
        val request = _uiState.value.toSubmitExpenseRequest() ?: return

        viewModelScope.launch {
            val result = submitExpenseUseCase(request)
            _events.send(ExpenseEvent.Saved(result))
        }
    }
}

data class ExpenseUiState(
    val categories: List<ExpenseCategory> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedCategory: ExpenseCategory? = null,
    val description: String = "",
    val amountInput: String = "",
    val amountText: String = "",
    val attachment: ExpenseAttachmentUiState? = null,
    val repeatSchedule: ExpenseRepeatUiState? = null,
)

private const val DEFAULT_AMOUNT_VALUE = 0.0
private const val MAX_AMOUNT_LENGTH = 9

data class ExpenseAttachmentUiState(
    val uriString: String,
    val name: String,
    val type: ExpenseAttachmentType,
)

enum class ExpenseAttachmentType {
    IMAGE,
    DOCUMENT,
}

data class ExpenseRepeatUiState(
    val frequency: RepeatFrequency,
    val endAtEpochMillis: Long,
)

sealed interface ExpenseEvent {
    data class Saved(
        val result: ExpenseSubmissionResult,
    ) : ExpenseEvent
}

private fun ExpenseRepeatUiState.toDomain(): RepeatSchedule = RepeatSchedule(
    frequency = frequency,
    endAtEpochMillis = endAtEpochMillis,
)

private fun ExpenseUiState.toSubmitExpenseRequest(): SubmitExpenseRequest? {
    val amount = amountInput.toDoubleOrNull()
    val categoryName = selectedCategory?.name
    return if (amount != null && amount > DEFAULT_AMOUNT_VALUE && categoryName != null) {
        SubmitExpenseRequest(
            amount = amount,
            description = description,
            category = categoryName,
            occurredAtEpochMillis = System.currentTimeMillis(),
            repeatSchedule = repeatSchedule?.toDomain(),
        )
    } else {
        null
    }
}
