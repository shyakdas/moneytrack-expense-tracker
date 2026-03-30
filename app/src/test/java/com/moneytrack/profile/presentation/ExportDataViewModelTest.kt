// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import com.moneytrack.profile.domain.model.CsvExportPayload
import com.moneytrack.profile.domain.model.ExportDateRange
import com.moneytrack.profile.domain.usecase.BuildTransactionExportUseCase
import com.moneytrack.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ExportDataViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Test
    fun initialState_usesDefaultSelections() {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value

        assertEquals(ExportDateRange.LAST_30_DAYS, state.selectedDateRange)
        assertFalse(state.isExporting)
    }

    @Test
    fun onDateRangeSelected_updatesUiState() {
        val viewModel = createViewModel()

        viewModel.onDateRangeSelected(ExportDateRange.LAST_90_DAYS)

        assertEquals(ExportDateRange.LAST_90_DAYS, viewModel.uiState.value.selectedDateRange)
    }

    @Test
    fun onExportClick_emitsDocumentCreationEvent() = runTest {
        val useCase = mockk<BuildTransactionExportUseCase>()
        coEvery { useCase.invoke(ExportDateRange.LAST_30_DAYS) } returns CsvExportPayload(
            fileName = "moneytrack-export.csv",
            content = "id,date\n1,2026-03-23",
        )
        val viewModel = createViewModel(useCase)
        val events = mutableListOf<ExportDataEvent>()
        val collectJob = launch { viewModel.events.collect(events::add) }

        viewModel.onExportClick()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isExporting)
        assertEquals(
            ExportDataEvent.RequestDocumentCreation(
                CsvExportPayload(
                    fileName = "moneytrack-export.csv",
                    content = "id,date\n1,2026-03-23",
                ),
            ),
            events.single(),
        )
        collectJob.cancel()
    }

    @Test
    fun onExportDocumentCreated_successResetsLoadingAndEmitsCompleted() = runTest {
        val useCase = mockk<BuildTransactionExportUseCase>()
        coEvery { useCase.invoke(any()) } returns CsvExportPayload(
            fileName = "moneytrack-export.csv",
            content = "csv",
        )
        val viewModel = createViewModel(useCase)
        val events = mutableListOf<ExportDataEvent>()
        val collectJob = launch { viewModel.events.collect(events::add) }

        viewModel.onExportClick()
        advanceUntilIdle()
        viewModel.onExportDocumentCreated(success = true)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isExporting)
        assertEquals(ExportDataEvent.Completed, events.last())
        collectJob.cancel()
    }

    private fun createViewModel(
        useCase: BuildTransactionExportUseCase = mockk(relaxed = true),
    ): ExportDataViewModel = ExportDataViewModel(
        buildTransactionExportUseCase = useCase,
    )
}
