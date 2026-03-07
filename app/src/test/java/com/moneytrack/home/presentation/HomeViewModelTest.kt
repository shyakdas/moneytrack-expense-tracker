package com.moneytrack.home.presentation

import com.moneytrack.home.domain.model.Budget
import com.moneytrack.home.domain.repository.BudgetRepository
import com.moneytrack.home.domain.usecase.ObserveBudgetUseCase
import com.moneytrack.home.domain.usecase.UpsertBudgetUseCase
import com.moneytrack.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun formatCurrency_formatsUsingIndianGrouping() {
        val viewModel = createViewModel(FakeBudgetRepository())

        val formatted = viewModel.formatCurrency(1000000.0)

        assertEquals("$10,00,000", formatted)
    }

    @Test
    fun saveBudget_blankDescription_savesNullDescription() = runTest {
        val repository = FakeBudgetRepository()
        val viewModel = createViewModel(repository)

        viewModel.saveBudget(amount = 40000.0, description = "   ")
        advanceUntilIdle()

        assertEquals(40000.0, repository.lastUpsertAmount ?: 0.0, 0.0)
        assertEquals(null, repository.lastUpsertDescription)
    }

    @Test
    fun uiState_withBudget_usesFormattedBudgetAndMarksBudgetAvailable() = runTest {
        val repository = FakeBudgetRepository()
        val viewModel = createViewModel(repository)
        val collectJob = launch { viewModel.uiState.collect { } }

        repository.emitBudget(
            Budget(
                amount = 100000.0,
                description = null,
                updatedAtEpochMillis = 0L,
            ),
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasBudget)
        assertEquals("$1,00,000", state.budgetText)
        assertEquals("$0", state.accountBalanceText)
        assertFalse(state.hasExpenses)
        collectJob.cancel()
    }

    @Test
    fun onSelections_updatesUiStateValues() = runTest {
        val viewModel = createViewModel(FakeBudgetRepository())
        val collectJob = launch { viewModel.uiState.collect { } }

        viewModel.onBottomRouteSelected("budget")
        viewModel.onTimeRangeSelected("Month")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("budget", state.selectedBottomRoute)
        assertEquals("Month", state.selectedRange)
        collectJob.cancel()
    }

    private fun createViewModel(repository: FakeBudgetRepository): HomeViewModel {
        return HomeViewModel(
            observeBudgetUseCase = ObserveBudgetUseCase(repository),
            upsertBudgetUseCase = UpsertBudgetUseCase(repository),
        )
    }

    private class FakeBudgetRepository : BudgetRepository {
        private val budgetFlow = MutableStateFlow<Budget?>(null)
        var lastUpsertAmount: Double? = null
        var lastUpsertDescription: String? = null

        override fun observeBudget(): Flow<Budget?> = budgetFlow.asStateFlow()

        override suspend fun upsertBudget(
            amount: Double,
            description: String?,
        ) {
            lastUpsertAmount = amount
            lastUpsertDescription = description
            budgetFlow.value = Budget(
                amount = amount,
                description = description,
                updatedAtEpochMillis = 0L,
            )
        }

        fun emitBudget(budget: Budget?) {
            budgetFlow.value = budget
        }
    }
}
