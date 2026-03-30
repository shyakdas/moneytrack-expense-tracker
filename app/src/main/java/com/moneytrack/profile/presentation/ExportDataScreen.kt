// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneytrack.R
import com.moneytrack.designsystem.R as DsR
import com.moneytrack.profile.domain.model.CsvExportPayload
import com.moneytrack.profile.domain.model.ExportDataType
import com.moneytrack.profile.domain.model.ExportDateRange
import com.moneytrack.profile.domain.model.ExportFormat
import ui.components.navigation.button.LargeButton
import ui.components.navigation.topNav.TopNavigation
import ui.components.navigation.topNav.TopNavigationConfig
import ui.theme.AppTheme
import ui.theme.Dimens

private enum class ExportSelectorMenu {
    DATA_TYPE,
    DATE_RANGE,
    FORMAT,
}

private data class ExportOptionUiModel<T>(
    val value: T,
    val label: String,
)

data class ExportDataActions(
    val onBackClick: () -> Unit,
    val onDataTypeSelected: (ExportDataType) -> Unit,
    val onDateRangeSelected: (ExportDateRange) -> Unit,
    val onFormatSelected: (ExportFormat) -> Unit,
    val onExportClick: () -> Unit,
)

private data class ExportSelectorFieldConfig<T>(
    val label: String,
    val value: String,
    val expanded: Boolean,
    val options: List<ExportOptionUiModel<T>>,
)

private data class ExportScreenOptions(
    val dataTypeOptions: List<ExportOptionUiModel<ExportDataType>>,
    val dateRangeOptions: List<ExportOptionUiModel<ExportDateRange>>,
    val formatOptions: List<ExportOptionUiModel<ExportFormat>>,
)

@Composable
fun ExportDataRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: ExportDataViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    var pendingPayload by remember { mutableStateOf<CsvExportPayload?>(null) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(EXPORT_CSV_MIME_TYPE),
    ) { uri ->
        val payload = pendingPayload
        pendingPayload = null
        val isSuccess = if (uri != null && payload != null) {
            writeCsvToUri(
                context = context,
                uri = uri,
                content = payload.content,
            )
        } else {
            false
        }
        viewModel.onExportDocumentCreated(success = isSuccess)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ExportDataEvent.Completed -> onBackClick()
                is ExportDataEvent.RequestDocumentCreation -> {
                    pendingPayload = event.payload
                    createDocumentLauncher.launch(event.payload.fileName)
                }
            }
        }
    }

    ExportDataScreen(
        uiState = uiState,
        actions = ExportDataActions(
            onBackClick = onBackClick,
            onDataTypeSelected = viewModel::onDataTypeSelected,
            onDateRangeSelected = viewModel::onDateRangeSelected,
            onFormatSelected = viewModel::onFormatSelected,
            onExportClick = viewModel::onExportClick,
        ),
    )
}

@Composable
fun ExportDataScreen(
    uiState: ExportDataUiState,
    actions: ExportDataActions,
) {
    var expandedMenu by remember { mutableStateOf<ExportSelectorMenu?>(null) }
    val options = rememberExportScreenOptions()

    Scaffold(
        containerColor = AppTheme.colors.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background)
                .padding(innerPadding),
        ) {
            TopNavigation(
                config = TopNavigationConfig.BackWithTitle(
                    title = stringResource(id = R.string.export_data_title),
                    onBackClick = actions.onBackClick,
                ),
                containerColor = Color.Transparent,
            )

            ExportDataForm(
                uiState = uiState,
                options = options,
                expandedMenu = expandedMenu,
                onExpandedMenuChanged = { expandedMenu = it },
                actions = actions,
            )
        }
    }
}

@Composable
private fun ColumnScope.ExportDataForm(
    uiState: ExportDataUiState,
    options: ExportScreenOptions,
    expandedMenu: ExportSelectorMenu?,
    onExpandedMenuChanged: (ExportSelectorMenu?) -> Unit,
    actions: ExportDataActions,
) {
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacing16)
            .padding(top = Dimens.spacing12),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing20),
    ) {
        item {
            ExportDataTypeSection(
                uiState = uiState,
                options = options,
                expandedMenu = expandedMenu,
                onExpandedMenuChanged = onExpandedMenuChanged,
                onDataTypeSelected = actions.onDataTypeSelected,
            )
        }

        item {
            ExportDateRangeSection(
                uiState = uiState,
                options = options,
                expandedMenu = expandedMenu,
                onExpandedMenuChanged = onExpandedMenuChanged,
                onDateRangeSelected = actions.onDateRangeSelected,
            )
        }

        item {
            ExportFormatSection(
                uiState = uiState,
                options = options,
                expandedMenu = expandedMenu,
                onExpandedMenuChanged = onExpandedMenuChanged,
                onFormatSelected = actions.onFormatSelected,
            )
        }
    }

    ExportActionButton(
        isExporting = uiState.isExporting,
        onClick = actions.onExportClick,
    )
}

@Composable
private fun ExportDataTypeSection(
    uiState: ExportDataUiState,
    options: ExportScreenOptions,
    expandedMenu: ExportSelectorMenu?,
    onExpandedMenuChanged: (ExportSelectorMenu?) -> Unit,
    onDataTypeSelected: (ExportDataType) -> Unit,
) {
    ExportSelectorField(
        config = ExportSelectorFieldConfig(
            label = stringResource(id = R.string.export_data_scope_label),
            value = options.dataTypeOptions.first { it.value == uiState.selectedDataType }.label,
            expanded = expandedMenu == ExportSelectorMenu.DATA_TYPE,
            options = options.dataTypeOptions,
        ),
        onExpandedChange = { isExpanded ->
            onExpandedMenuChanged(if (isExpanded) ExportSelectorMenu.DATA_TYPE else null)
        },
        onOptionSelected = {
            onDataTypeSelected(it.value)
            onExpandedMenuChanged(null)
        },
    )
}

@Composable
private fun ExportDateRangeSection(
    uiState: ExportDataUiState,
    options: ExportScreenOptions,
    expandedMenu: ExportSelectorMenu?,
    onExpandedMenuChanged: (ExportSelectorMenu?) -> Unit,
    onDateRangeSelected: (ExportDateRange) -> Unit,
) {
    ExportSelectorField(
        config = ExportSelectorFieldConfig(
            label = stringResource(id = R.string.export_data_range_label),
            value = options.dateRangeOptions.first { it.value == uiState.selectedDateRange }.label,
            expanded = expandedMenu == ExportSelectorMenu.DATE_RANGE,
            options = options.dateRangeOptions,
        ),
        onExpandedChange = { isExpanded ->
            onExpandedMenuChanged(if (isExpanded) ExportSelectorMenu.DATE_RANGE else null)
        },
        onOptionSelected = {
            onDateRangeSelected(it.value)
            onExpandedMenuChanged(null)
        },
    )
}

@Composable
private fun ExportFormatSection(
    uiState: ExportDataUiState,
    options: ExportScreenOptions,
    expandedMenu: ExportSelectorMenu?,
    onExpandedMenuChanged: (ExportSelectorMenu?) -> Unit,
    onFormatSelected: (ExportFormat) -> Unit,
) {
    ExportSelectorField(
        config = ExportSelectorFieldConfig(
            label = stringResource(id = R.string.export_data_format_label),
            value = options.formatOptions.first { it.value == uiState.selectedFormat }.label,
            expanded = expandedMenu == ExportSelectorMenu.FORMAT,
            options = options.formatOptions,
        ),
        onExpandedChange = { isExpanded ->
            onExpandedMenuChanged(if (isExpanded) ExportSelectorMenu.FORMAT else null)
        },
        onOptionSelected = {
            onFormatSelected(it.value)
            onExpandedMenuChanged(null)
        },
    )
}

@Composable
private fun rememberExportScreenOptions(): ExportScreenOptions {
    return ExportScreenOptions(
        dataTypeOptions = listOf(
            ExportOptionUiModel(
                value = ExportDataType.ALL,
                label = stringResource(id = R.string.export_data_scope_all),
            ),
        ),
        dateRangeOptions = listOf(
            ExportOptionUiModel(ExportDateRange.LAST_30_DAYS, stringResource(id = R.string.export_data_range_30)),
            ExportOptionUiModel(ExportDateRange.LAST_60_DAYS, stringResource(id = R.string.export_data_range_60)),
            ExportOptionUiModel(ExportDateRange.LAST_90_DAYS, stringResource(id = R.string.export_data_range_90)),
            ExportOptionUiModel(ExportDateRange.LAST_120_DAYS, stringResource(id = R.string.export_data_range_120)),
        ),
        formatOptions = listOf(
            ExportOptionUiModel(
                value = ExportFormat.CSV,
                label = stringResource(id = R.string.export_data_format_csv),
            ),
        ),
    )
}

@Composable
private fun ExportActionButton(
    isExporting: Boolean,
    onClick: () -> Unit,
) {
    LargeButton(
        text = stringResource(id = R.string.export_data_button),
        onClick = onClick,
        leadingIcon = ImageVector.vectorResource(id = DsR.drawable.download),
        enabled = !isExporting,
        modifier = Modifier
            .navigationBarsPadding()
            .padding(Dimens.spacing16),
    )
}

@Composable
private fun <T> ExportSelectorField(
    config: ExportSelectorFieldConfig<T>,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (ExportOptionUiModel<T>) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing8),
    ) {
        Text(
            text = config.label,
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.onBackground,
            fontWeight = FontWeight.Medium,
        )

        Box {
            ExportFieldContainer(
                value = config.value,
                onClick = { onExpandedChange(true) },
            )
            DropdownMenu(
                expanded = config.expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.fillMaxWidth(DROPDOWN_WIDTH_FRACTION),
            ) {
                config.options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label,
                                style = AppTheme.typography.bodyMedium,
                            )
                        },
                        onClick = { onOptionSelected(option) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ExportFieldContainer(
    value: String,
    onClick: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AppTheme.colors.background,
                shape = RoundedCornerShape(Dimens.radius16),
            )
            .border(
                width = Dimens.borderNormal,
                color = AppTheme.colors.outline,
                shape = RoundedCornerShape(Dimens.radius16),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing18),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = value,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onBackground,
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = DsR.drawable.arrow_down_2),
            contentDescription = null,
            tint = AppTheme.colors.onSurfaceVariant,
        )
    }
}

private const val DROPDOWN_WIDTH_FRACTION = 0.92f
