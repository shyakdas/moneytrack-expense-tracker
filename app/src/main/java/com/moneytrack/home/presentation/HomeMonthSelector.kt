// Copyright (c) 2026 shyakdas

package com.moneytrack.home.presentation

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
internal fun MonthSelectorPopup(
    months: List<HomeMonthOption>,
    selectedMonth: HomeMonthOption,
    onMonthSelected: (HomeMonthOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .wrapContentWidth()
            .widthIn(min = 156.dp)
            .heightIn(max = 360.dp)
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
            months.forEach { month ->
                MonthSelectorRow(
                    month = month,
                    selected = month.monthIndex == selectedMonth.monthIndex &&
                        month.year == selectedMonth.year,
                    onClick = { onMonthSelected(month) },
                )
            }
        }
    }
}

@Composable
internal fun MonthSelectorRow(
    month: HomeMonthOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) {
        AppTheme.colors.primaryContainer
    } else {
        Color.Transparent
    }
    val contentColor = if (selected) {
        AppTheme.colors.onPrimaryContainer
    } else {
        AppTheme.colors.onSurface
    }

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
            text = month.label,
            modifier = Modifier.widthIn(min = 96.dp),
            style = AppTheme.typography.bodyMedium,
            color = contentColor,
            textAlign = TextAlign.Center,
        )
    }
}
