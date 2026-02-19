package ui.components.navigation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChipGroup(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            SelectorChip(
                label = option,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}

@Preview(
    name = "Chip Group",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ChipGroupPreview() {
    MaterialTheme {
        ChipGroup(
            options = listOf("Income", "Expense", "Transfer"),
            selectedOption = "Expense",
            onOptionSelected = {}
        )
    }
}

