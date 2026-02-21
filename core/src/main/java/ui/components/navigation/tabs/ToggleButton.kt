package ui.components.navigation.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun ToggleButton(
    selected: ToggleOption,
    onSelectedChange: (ToggleOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(Dimens.spacing48)
            .background(
                color = AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.spacing28)
            )
            .padding(Dimens.spacing4),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ToggleButtonItem(
            text = "Expense",
            isSelected = selected == ToggleOption.EXPENSE,
            onClick = { onSelectedChange(ToggleOption.EXPENSE) }
        )

        ToggleButtonItem(
            text = "Income",
            isSelected = selected == ToggleOption.INCOME,
            onClick = { onSelectedChange(ToggleOption.INCOME) }
        )
    }
}

@Composable
private fun RowScope.ToggleButtonItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            AppTheme.colors.primary
        else
            Color.Transparent,
        label = "ToggleItemBackground"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected)
            AppTheme.colors.onPrimary
        else
            AppTheme.colors.onSurfaceVariant,
        label = "ToggleItemText"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(Dimens.spacing24)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTheme.typography.bodyMedium,
            color = textColor
        )
    }
}

@Preview(name = "Toggle Button – Light & Dark")
@Composable
private fun ToggleButtonPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            Box(
                modifier = Modifier
                    .padding(Dimens.spacing16)
                    .background(AppTheme.colors.background)
            ) {
                ToggleButton(
                    selected = ToggleOption.EXPENSE,
                    onSelectedChange = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            Box(
                modifier = Modifier
                    .padding(Dimens.spacing16)
                    .background(AppTheme.colors.background)
            ) {
                ToggleButton(
                    selected = ToggleOption.INCOME,
                    onSelectedChange = {}
                )
            }
        }
    }
}
