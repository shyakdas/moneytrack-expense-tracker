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
import com.moneytrack.settings.domain.usecase.ObserveAppCurrencyCodeUseCase
import com.moneytrack.transaction.domain.model.TransactionRecordType
import com.moneytrack.transaction.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
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
    observeAppCurrencyCodeUseCase: ObserveAppCurrencyCodeUseCase,
    private val currencyFormatter: CurrencyFormatter,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ExpenseUiState(
            amountText = currencyFormatter.format(DEFAULT_AMOUNT_VALUE),
            isSubmitEnabled = false,
        ),
    )
    private val selectedCurrencyCode = MutableStateFlow(currencyFormatter.currentCurrencyCode())
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
                    val pendingCategoryId = if (state.selectedCategoryId == null && state.pendingCategoryName != null) {
                        categories.firstOrNull { category ->
                            category.name.equals(state.pendingCategoryName, ignoreCase = true)
                        }?.id
                    } else {
                        null
                    }
                    val selectedCategoryId = when {
                        state.selectedCategoryId == null -> null
                        categories.any { it.id == state.selectedCategoryId } -> state.selectedCategoryId
                        else -> null
                    }
                    val resolvedCategoryId = pendingCategoryId ?: selectedCategoryId
                    state.copy(
                        categories = categories,
                        selectedCategoryId = resolvedCategoryId,
                        selectedCategory = categories.firstOrNull { category ->
                            category.id == resolvedCategoryId
                        },
                        pendingCategoryName = if (resolvedCategoryId != null) null else state.pendingCategoryName,
                    )
                }
            }
        }

        viewModelScope.launch {
            observeAppCurrencyCodeUseCase().collect { currencyCode ->
                selectedCurrencyCode.update { currencyCode }
                _uiState.update { state ->
                    val amountValue = state.amountInput.toDoubleOrNull() ?: DEFAULT_AMOUNT_VALUE
                    state.copy(
                        amountText = currencyFormatter.format(
                            value = amountValue,
                            currencyCode = currencyCode,
                        ),
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
                pendingCategoryName = null,
            )
        }
    }

    fun initializeEdit(expenseId: Long?) {
        if (expenseId == null) return
        if (_uiState.value.editingExpenseId == expenseId) return

        viewModelScope.launch {
            val transaction = transactionRepository.getTransactionById(expenseId) ?: return@launch
            if (transaction.type != TransactionRecordType.EXPENSE) return@launch

            val amountInput = normalizeAmountInput(transaction.amount)
            val repeatSchedule = transactionRepository.getRepeatScheduleForTransaction(expenseId)
            val amountValue = amountInput.toDoubleOrNull() ?: DEFAULT_AMOUNT_VALUE
            val savedCategoryCandidates = listOf(transaction.category, transaction.title)
                .map(String::trim)
                .filter(String::isNotBlank)

            _uiState.update { state ->
                val matchedCategory = state.categories.firstOrNull { category ->
                    savedCategoryCandidates.any { savedName ->
                        category.name.equals(savedName, ignoreCase = true)
                    }
                }
                state.copy(
                    editingExpenseId = transaction.id,
                    isEditMode = true,
                    pendingCategoryName = transaction.category,
                    selectedCategoryId = matchedCategory?.id,
                    selectedCategory = matchedCategory,
                    description = transaction.note.orEmpty(),
                    attachment = transaction.toAttachmentUiState(),
                    amountInput = amountInput,
                    amountText = currencyFormatter.format(
                        value = amountValue,
                        currencyCode = selectedCurrencyCode.value,
                    ),
                    isSubmitEnabled = amountValue > DEFAULT_AMOUNT_VALUE,
                    occurredAtEpochMillis = transaction.occurredAtEpochMillis,
                    repeatSchedule = repeatSchedule?.toUi(),
                )
            }
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
                amountText = currencyFormatter.format(
                    value = amountValue,
                    currencyCode = selectedCurrencyCode.value,
                ),
                isSubmitEnabled = amountValue > DEFAULT_AMOUNT_VALUE,
            )
        }
    }

    fun submitExpense() {
        val editExpenseId = _uiState.value.editingExpenseId
        if (editExpenseId != null) {
            submitExpenseEdit(editExpenseId)
            return
        }

        val request = _uiState.value.toSubmitExpenseRequest() ?: return

        viewModelScope.launch {
            val result = submitExpenseUseCase(request)
            _events.send(ExpenseEvent.Saved(result))
        }
    }

    private fun submitExpenseEdit(expenseId: Long) {
        val state = _uiState.value
        val amount = state.amountInput.toDoubleOrNull() ?: return
        if (amount <= DEFAULT_AMOUNT_VALUE) return
        val categoryName = state.selectedCategory?.name ?: DEFAULT_EXPENSE_CATEGORY

        viewModelScope.launch {
            transactionRepository.updateExpenseTransaction(
                id = expenseId,
                amount = amount,
                note = state.description,
                category = categoryName,
                attachmentUri = state.attachment?.uriString,
                attachmentName = state.attachment?.name,
                attachmentType = state.attachment?.type?.name,
                occurredAtEpochMillis = state.occurredAtEpochMillis,
                repeatSchedule = state.repeatSchedule?.toDomain(),
            )
            _events.send(ExpenseEvent.Saved(ExpenseSubmissionResult()))
        }
    }

    fun onOccurredAtChanged(occurredAtEpochMillis: Long) {
        _uiState.update { state ->
            state.copy(occurredAtEpochMillis = occurredAtEpochMillis)
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
    val isSubmitEnabled: Boolean = false,
    val attachment: ExpenseAttachmentUiState? = null,
    val repeatSchedule: ExpenseRepeatUiState? = null,
    val occurredAtEpochMillis: Long = Calendar.getInstance().timeInMillis,
    val isEditMode: Boolean = false,
    val editingExpenseId: Long? = null,
    val pendingCategoryName: String? = null,
)

private const val DEFAULT_AMOUNT_VALUE = 0.0
private const val MAX_AMOUNT_LENGTH = 9
private const val DEFAULT_EXPENSE_CATEGORY = "Expense"
private const val DEFAULT_ATTACHMENT_NAME = "Attachment"

private fun normalizeAmountInput(value: Double): String {
    val normalized = if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        value.toString().replace(".", "")
    }
    return normalized.take(MAX_AMOUNT_LENGTH)
}

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

private fun RepeatSchedule.toUi(): ExpenseRepeatUiState = ExpenseRepeatUiState(
    frequency = frequency,
    endAtEpochMillis = endAtEpochMillis,
)

private fun ExpenseUiState.toSubmitExpenseRequest(): SubmitExpenseRequest? {
    val amount = amountInput.toDoubleOrNull()
    val categoryName = selectedCategory?.name ?: DEFAULT_EXPENSE_CATEGORY
    return if (amount != null && amount > DEFAULT_AMOUNT_VALUE) {
        SubmitExpenseRequest(
            amount = amount,
            description = description,
            category = categoryName,
            occurredAtEpochMillis = occurredAtEpochMillis,
            attachmentUri = attachment?.uriString,
            attachmentName = attachment?.name,
            attachmentType = attachment?.type?.name,
            repeatSchedule = repeatSchedule?.toDomain(),
        )
    } else {
        null
    }
}

private fun com.moneytrack.transaction.domain.model.TransactionRecord.toAttachmentUiState():
    ExpenseAttachmentUiState? {
    val uri = attachmentUri ?: return null
    val type = attachmentType?.let { raw ->
        runCatching { ExpenseAttachmentType.valueOf(raw) }.getOrNull()
    } ?: ExpenseAttachmentType.IMAGE
    val name = attachmentName ?: DEFAULT_ATTACHMENT_NAME
    return ExpenseAttachmentUiState(
        uriString = uri,
        name = name,
        type = type,
    )
}
