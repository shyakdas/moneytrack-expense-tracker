package ui.components.navigation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing8)
    ) {
        options.forEach { option ->
            SelectorChip(
                label = option,
                selected = option == selectedOption, // ✅ FIXED
                onClick = { onOptionSelected(option) }
            )
        }
    }
}


@Preview(name = "Chip Group – Light & Dark")
@Composable
private fun ChipGroupPreview() {
    Column {

        MoneyTrackTheme(darkTheme = false) {
            ChipGroup(
                options = listOf("Income", "Expense", "Transfer"),
                selectedOption = "Expense",
                onOptionSelected = {}
            )
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.height(Dimens.spacing16)
        )

        MoneyTrackTheme(darkTheme = true) {
            ChipGroup(
                options = listOf("Income", "Expense", "Transfer"),
                selectedOption = "Expense",
                onOptionSelected = {}
            )
        }
    }
}
