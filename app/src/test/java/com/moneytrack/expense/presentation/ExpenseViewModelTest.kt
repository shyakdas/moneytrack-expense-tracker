// Copyright (c) 2026 shyakdas

package com.moneytrack.expense.presentation

import com.moneytrack.expense.domain.model.ExpenseCategory
import com.moneytrack.expense.domain.model.ExpenseSubmissionResult
import com.moneytrack.expense.domain.model.RecurringExpenseSchedule
import com.moneytrack.expense.domain.model.RepeatFrequency
import com.moneytrack.expense.domain.model.SubmitExpenseRequest
import com.moneytrack.expense.domain.repository.CategoryRepository
import com.moneytrack.expense.domain.repository.ExpenseRepository
import com.moneytrack.expense.domain.usecase.EnsureDefaultCategoriesUseCase
import com.moneytrack.expense.domain.usecase.ObserveCategoriesUseCase
import com.moneytrack.expense.domain.usecase.SubmitExpenseUseCase
import com.moneytrack.locale.CountryProvider
import com.moneytrack.locale.CurrencyFormatter
import com.moneytrack.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun submitExpense_withoutCategory_usesDefaultExpenseCategory() = runTest {
        val expenseRepository = FakeExpenseRepository()
        val viewModel = createViewModel(expenseRepository = expenseRepository)

        viewModel.onAmountChanged("2500")
        advanceUntilIdle()
        viewModel.submitExpense()
        advanceUntilIdle()

        val request = expenseRepository.lastRequest
        assertNotNull(request)
        assertEquals(2500.0, request?.amount ?: 0.0, 0.0)
        assertEquals("Expense", request?.category)
        assertTrue(viewModel.uiState.value.isSubmitEnabled)
    }

    @Test
    fun submitExpense_withSelectedCategoryAndRepeat_includesOptionalFields() = runTest {
        val expenseRepository = FakeExpenseRepository()
        val categoryRepository = FakeCategoryRepository()
        val viewModel = createViewModel(
            expenseRepository = expenseRepository,
            categoryRepository = categoryRepository,
        )
        val category = ExpenseCategory(
            id = 4L,
            name = "Food",
            colorHex = "#FD3C4A",
            sortOrder = 0,
            isDefault = true,
        )

        categoryRepository.emitCategories(listOf(category))
        advanceUntilIdle()
        viewModel.onCategorySelected(category.id)
        viewModel.onDescriptionChanged("Dinner")
        viewModel.onRepeatConfigured(
            frequency = RepeatFrequency.MONTHLY,
            endAtEpochMillis = 1_767_132_799_999L,
        )
        viewModel.onAmountChanged("3400")
        advanceUntilIdle()
        viewModel.submitExpense()
        advanceUntilIdle()

        val request = expenseRepository.lastRequest
        assertNotNull(request)
        assertEquals("Food", request?.category)
        assertEquals("Dinner", request?.description)
        assertEquals(RepeatFrequency.MONTHLY, request?.repeatSchedule?.frequency)
    }

    @Test
    fun submitExpense_withZeroAmount_doesNotSaveExpense() = runTest {
        val expenseRepository = FakeExpenseRepository()
        val viewModel = createViewModel(expenseRepository = expenseRepository)

        viewModel.onAmountChanged("0")
        advanceUntilIdle()
        viewModel.submitExpense()
        advanceUntilIdle()

        assertEquals(null, expenseRepository.lastRequest)
        assertFalse(viewModel.uiState.value.isSubmitEnabled)
    }

    @Test
    fun onAmountChanged_updatesFormattedAmountAndSubmitState() = runTest {
        val viewModel = createViewModel(expenseRepository = FakeExpenseRepository())

        viewModel.onAmountChanged("250000")
        advanceUntilIdle()

        assertEquals("$250,000", viewModel.uiState.value.amountText)
        assertTrue(viewModel.uiState.value.isSubmitEnabled)
    }

    @Test
    fun onAttachmentSelectedAndRemoved_updatesUiState() = runTest {
        val viewModel = createViewModel(expenseRepository = FakeExpenseRepository())

        viewModel.onAttachmentSelected(
            uriString = "content://receipt",
            name = "receipt.pdf",
            type = ExpenseAttachmentType.DOCUMENT,
        )
        advanceUntilIdle()
        assertEquals("receipt.pdf", viewModel.uiState.value.attachment?.name)

        viewModel.onAttachmentRemoved()
        advanceUntilIdle()
        assertEquals(null, viewModel.uiState.value.attachment)
    }

    @Test
    fun onRepeatRemoved_clearsConfiguredRepeatState() = runTest {
        val viewModel = createViewModel(expenseRepository = FakeExpenseRepository())

        viewModel.onRepeatConfigured(
            frequency = RepeatFrequency.WEEKLY,
            endAtEpochMillis = 1_767_132_799_999L,
        )
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.repeatSchedule)

        viewModel.onRepeatRemoved()
        advanceUntilIdle()
        assertEquals(null, viewModel.uiState.value.repeatSchedule)
    }

    @Test
    fun submitExpense_emitsSavedEvent() = runTest {
        val expenseRepository = FakeExpenseRepository()
        val viewModel = createViewModel(expenseRepository = expenseRepository)
        val eventDeferred = async { viewModel.events.first() }

        viewModel.onAmountChanged("1800")
        advanceUntilIdle()
        viewModel.submitExpense()
        advanceUntilIdle()

        val event = eventDeferred.await()
        assertTrue(event is ExpenseEvent.Saved)
    }

    private fun createViewModel(
        expenseRepository: FakeExpenseRepository,
        categoryRepository: FakeCategoryRepository = FakeCategoryRepository(),
    ): ExpenseViewModel {
        return ExpenseViewModel(
            observeCategoriesUseCase = ObserveCategoriesUseCase(categoryRepository),
            ensureDefaultCategoriesUseCase = EnsureDefaultCategoriesUseCase(categoryRepository),
            submitExpenseUseCase = SubmitExpenseUseCase(expenseRepository),
            currencyFormatter = CurrencyFormatter(FakeCountryProvider()),
        )
    }

    private class FakeCategoryRepository : CategoryRepository {
        private val categories = MutableStateFlow<List<ExpenseCategory>>(emptyList())

        override fun observeCategories(): Flow<List<ExpenseCategory>> = categories.asStateFlow()

        override suspend fun ensureDefaultCategories() = Unit

        override suspend fun addCategory(name: String) = Unit

        override suspend fun moveCategoryUp(categoryId: Long) = Unit

        override suspend fun moveCategoryDown(categoryId: Long) = Unit

        override suspend fun reorderCategories(categoryIds: List<Long>) = Unit

        fun emitCategories(items: List<ExpenseCategory>) {
            categories.value = items
        }
    }

    private class FakeExpenseRepository : ExpenseRepository {
        var lastRequest: SubmitExpenseRequest? = null

        override suspend fun submitExpense(request: SubmitExpenseRequest): ExpenseSubmissionResult {
            lastRequest = request
            return ExpenseSubmissionResult()
        }

        override suspend fun processRecurringExpense(
            recurringExpenseId: Long,
        ): RecurringExpenseSchedule? = null

        override suspend fun getActiveRecurringSchedules(): List<RecurringExpenseSchedule> = emptyList()
    }

    private class FakeCountryProvider : CountryProvider {
        override fun getCountryCode(): String = "US"

        override fun getCurrencySymbol(): String = "$"
    }
}
