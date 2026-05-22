// Copyright (c) 2026 shyakdas

package ui.components.navigation.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun TimeRangeTab(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val compact = LocalConfiguration.current.screenWidthDp < 360
    Row(
        modifier = modifier
            .height(Dimens.buttonSmallHeight)
            .background(
                color = AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.radius12)
            )
            .padding(Dimens.spacing4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            TimeRangeTabItem(
                text = option,
                isSelected = option == selectedOption,
                onClick = { onOptionSelected(option) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                compact = compact,
            )
        }
    }
}

@Composable
private fun TimeRangeTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            AppTheme.colors.surface
        else
            Color.Transparent,
        label = "TabBackground"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected)
            AppTheme.colors.primary
        else
            AppTheme.colors.onSurfaceVariant,
        label = "TabText"
    )

    Box(
        modifier = modifier
            .height(Dimens.spacing32)
            .padding(horizontal = Dimens.spacing2)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(Dimens.radius8)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = if (compact) Dimens.spacing8 else Dimens.spacing12),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTheme.typography.bodySmall,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(name = "Time Range Tab – Light & Dark")
@Composable
private fun TimeRangeTabPreview() {
    Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.background)
                    .padding(Dimens.spacing16)
            ) {
                TimeRangeTab(
                    options = listOf("Today", "Week", "Month", "Year"),
                    selectedOption = "Year",
                    onOptionSelected = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.background)
                    .padding(Dimens.spacing16)
            ) {
                TimeRangeTab(
                    options = listOf("Today", "Week", "Month", "Year"),
                    selectedOption = "Year",
                    onOptionSelected = {}
                )
            }
        }
    }
}
