// Copyright (c) 2026 shyakdas

package com.moneytrack.transaction.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
internal fun TransactionMonthSelectorPopup(
    months: List<TransactionMonthOption>,
    selectedMonth: TransactionMonthOption,
    onMonthSelected: (TransactionMonthOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    PickerSelectorPopup(
        items = months.map { month ->
            PickerSelectorItem(
                value = month,
                label = month.label,
                selected = month.monthIndex == selectedMonth.monthIndex && month.year == selectedMonth.year,
            )
        },
        onItemSelected = onMonthSelected,
        sizing = MONTH_PICKER_SIZING,
        modifier = modifier,
    )
}

@Composable
internal fun TransactionYearSelectorPopup(
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    PickerSelectorPopup(
        items = years.map { year ->
            PickerSelectorItem(
                value = year,
                label = year.toString(),
                selected = year == selectedYear,
            )
        },
        onItemSelected = onYearSelected,
        sizing = YEAR_PICKER_SIZING,
        modifier = modifier,
    )
}

@Composable
private fun <T> PickerSelectorPopup(
    items: List<PickerSelectorItem<T>>,
    onItemSelected: (T) -> Unit,
    sizing: PickerSelectorSizing,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .wrapContentWidth()
            .widthIn(min = sizing.minWidth)
            .heightIn(max = sizing.maxHeight)
            .border(
                width = Dimens.spacing1,
                color = AppTheme.colors.outline,
                shape = RoundedCornerShape(Dimens.radius20),
            ),
        shape = RoundedCornerShape(Dimens.radius20),
        color = AppTheme.colors.surface,
        tonalElevation = Dimens.elevation4,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing8),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing4),
        ) {
            items.forEach { item ->
                PickerSelectorRow(
                    label = item.label,
                    selected = item.selected,
                    rowMinWidth = sizing.rowMinWidth,
                    onClick = { onItemSelected(item.value) },
                )
            }
        }
    }
}

@Composable
private fun PickerSelectorRow(
    label: String,
    selected: Boolean,
    rowMinWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) AppTheme.colors.primaryContainer else Color.Transparent
    val contentColor = if (selected) AppTheme.colors.onPrimaryContainer else AppTheme.colors.onSurface

    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(Dimens.spacing36)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(Dimens.radius16),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            modifier = Modifier.widthIn(min = rowMinWidth),
            style = AppTheme.typography.bodyMedium,
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}

private data class PickerSelectorSizing(
    val minWidth: androidx.compose.ui.unit.Dp,
    val maxHeight: androidx.compose.ui.unit.Dp,
    val rowMinWidth: androidx.compose.ui.unit.Dp,
)

private data class PickerSelectorItem<T>(
    val value: T,
    val label: String,
    val selected: Boolean,
)

private val MONTH_PICKER_SIZING = PickerSelectorSizing(
    minWidth = 156.dp,
    maxHeight = 360.dp,
    rowMinWidth = 96.dp,
)

private val YEAR_PICKER_SIZING = PickerSelectorSizing(
    minWidth = 104.dp,
    maxHeight = 240.dp,
    rowMinWidth = 72.dp,
)
