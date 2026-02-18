package ui.components.navigation.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import ui.theme.Light20
import ui.theme.Yellow100
import ui.theme.NeutralC6
import ui.theme.Yellow20

@Composable
fun TimeRangeTab(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .background(
                color = Light20,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            TimeRangeTabItem(
                text = option,
                isSelected = option == selectedOption,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}

@Composable
private fun TimeRangeTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Yellow20 else Color.Transparent,
        label = "TabBackground"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected)
            Yellow100
        else
            NeutralC6,
        label = "TabText"
    )

    Box(
        modifier = Modifier
            .height(36.dp)
            .padding(horizontal = 4.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}


@Preview(
    name = "Time Range Tab",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun TimeRangeTabPreview() {
    MaterialTheme {
        TimeRangeTab(
            options = listOf("Today", "Week", "Month", "Year"),
            selectedOption = "Year",
            onOptionSelected = {}
        )
    }
}
