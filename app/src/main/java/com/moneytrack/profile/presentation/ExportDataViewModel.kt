// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneytrack.profile.domain.model.CsvExportPayload
import com.moneytrack.profile.domain.model.ExportDataType
import com.moneytrack.profile.domain.model.ExportDateRange
import com.moneytrack.profile.domain.model.ExportFormat
import com.moneytrack.profile.domain.usecase.BuildTransactionExportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExportDataViewModel @Inject constructor(
    private val buildTransactionExportUseCase: BuildTransactionExportUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportDataUiState())
    val uiState: StateFlow<ExportDataUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ExportDataEvent>()
    val events: SharedFlow<ExportDataEvent> = _events.asSharedFlow()

    fun onDataTypeSelected(dataType: ExportDataType) {
        _uiState.update { it.copy(selectedDataType = dataType) }
    }

    fun onDateRangeSelected(dateRange: ExportDateRange) {
        _uiState.update { it.copy(selectedDateRange = dateRange) }
    }

    fun onFormatSelected(format: ExportFormat) {
        _uiState.update { it.copy(selectedFormat = format) }
    }

    fun onExportClick() {
        if (_uiState.value.isExporting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            runCatching {
                buildTransactionExportUseCase(dateRange = _uiState.value.selectedDateRange)
            }.onSuccess { payload ->
                _events.emit(ExportDataEvent.RequestDocumentCreation(payload))
            }.onFailure {
                _uiState.update { it.copy(isExporting = false) }
            }
        }
    }

    fun onExportDocumentCreated(success: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = false) }
            if (success) {
                _events.emit(ExportDataEvent.Completed)
            }
        }
    }
}

data class ExportDataUiState(
    val selectedDataType: ExportDataType = ExportDataType.ALL,
    val selectedDateRange: ExportDateRange = ExportDateRange.LAST_30_DAYS,
    val selectedFormat: ExportFormat = ExportFormat.CSV,
    val isExporting: Boolean = false,
)

sealed interface ExportDataEvent {
    data class RequestDocumentCreation(val payload: CsvExportPayload) : ExportDataEvent
    data object Completed : ExportDataEvent
}
