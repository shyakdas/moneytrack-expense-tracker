package ui.components.card.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.components.navigation.button.ButtonVariant
import ui.components.navigation.button.LargeButton
import ui.components.navigation.common.SelectorChip
import ui.theme.MoneyTrackTheme

@Composable
fun RecurringDetailCard(
    title: String,
    ctaText: String,
    onCtaClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            content()

            LargeButton(
                text = ctaText,
                onClick = onCtaClick,
                variant = ButtonVariant.PRIMARY
            )
        }
    }
}

@Preview(
    name = "Recurring Detail Card – Yearly",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun RecurringDetailCardPreview() {
    MoneyTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            RecurringDetailCard(
                title = "Yearly",
                ctaText = "Next",
                onCtaClick = {}
            ) {

                // ROW 1 — 3 equal chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectorChip(
                        label = "Yearly",
                        onClick = {},
                        selected = false,
                        modifier = Modifier.weight(1f)
                    )

                    SelectorChip(
                        label = "April",
                        onClick = {},
                        selected = false,
                        modifier = Modifier.weight(1f)
                    )

                    SelectorChip(
                        label = "29",
                        onClick = {},
                        selected = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectorChip(
                        label = "Date",
                        onClick = {},
                        selected = false,
                        modifier = Modifier.weight(1f)
                    )

                    SelectorChip(
                        label = "27 Aug 2021",
                        onClick = {},
                        selected = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
