package ui.components.navigation.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.theme.NeutralC6
import ui.theme.Violet100
import ui.theme.Violet20

@Composable
fun ToggleButton(
    selected: ToggleOption,
    onSelectedChange: (ToggleOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(4.dp),
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
        targetValue = if (isSelected) Violet100 else Violet20,
        label = "ToggleItemBackground"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else NeutralC6,
        label = "ToggleItemText"
    )

    Box(
        modifier = Modifier
            .weight(0.5f)
            .fillMaxHeight()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
    }
}


@Preview(
    name = "Toggle Button",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ToggleButtonPreview() {
    MaterialTheme {
        ToggleButton(
            selected = ToggleOption.EXPENSE,
            onSelectedChange = {}
        )
    }
}
