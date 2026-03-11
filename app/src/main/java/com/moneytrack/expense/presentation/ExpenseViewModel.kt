// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.expense.domain.model.ExpenseCategory
import com.moneytrack.expense.domain.usecase.AddCategoryUseCase
import com.moneytrack.expense.domain.usecase.EnsureDefaultCategoriesUseCase
import com.moneytrack.expense.domain.usecase.ObserveCategoriesUseCase
import com.moneytrack.expense.domain.usecase.ReorderCategoriesUseCase
import com.moneytrack.locale.CurrencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val ensureDefaultCategoriesUseCase: EnsureDefaultCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val reorderCategoriesUseCase: ReorderCategoriesUseCase,
    private val currencyFormatter: CurrencyFormatter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ExpenseUiState(
            amountText = currencyFormatter.format(DEFAULT_AMOUNT_VALUE),
        ),
    )
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()
    private var pendingSelectionName: String? = null

    init {
        viewModelScope.launch {
            ensureDefaultCategoriesUseCase()
        }

        viewModelScope.launch {
            observeCategoriesUseCase().collect { categories ->
                _uiState.update { state ->
                    val selectionFromPending = pendingSelectionName?.let { pendingName ->
                        categories.firstOrNull { category ->
                            category.name.equals(pendingName, ignoreCase = true)
                        }?.id
                    }
                    val selectedCategoryId = when {
                        selectionFromPending != null -> selectionFromPending
                        state.selectedCategoryId == null -> null
                        categories.any { it.id == state.selectedCategoryId } -> state.selectedCategoryId
                        else -> null
                    }
                    if (selectionFromPending != null) {
                        pendingSelectionName = null
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

    fun onCustomCategoryInputChanged(input: String) {
        val filtered = input.take(MAX_CATEGORY_NAME_LENGTH)
        _uiState.update { state ->
            state.copy(customCategoryInput = filtered)
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

    fun addCustomCategory() {
        val categoryName = _uiState.value.customCategoryInput.trim()
        if (categoryName.isEmpty()) return

        viewModelScope.launch {
            addCategoryUseCase(name = categoryName)
            pendingSelectionName = categoryName
            _uiState.update { state ->
                state.copy(customCategoryInput = "")
            }
        }
    }

    fun reorderCategories(categoryIds: List<Long>) {
        viewModelScope.launch {
            reorderCategoriesUseCase(categoryIds = categoryIds)
        }
    }

    private companion object {
        const val MAX_CATEGORY_NAME_LENGTH = 24
        const val MAX_AMOUNT_LENGTH = 9
    }
}

data class ExpenseUiState(
    val categories: List<ExpenseCategory> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedCategory: ExpenseCategory? = null,
    val customCategoryInput: String = "",
    val description: String = "",
    val amountInput: String = "",
    val amountText: String = "",
    val attachment: ExpenseAttachmentUiState? = null,
)

private const val DEFAULT_AMOUNT_VALUE = 0.0

data class ExpenseAttachmentUiState(
    val uriString: String,
    val name: String,
    val type: ExpenseAttachmentType,
)

enum class ExpenseAttachmentType {
    IMAGE,
    DOCUMENT,
}
