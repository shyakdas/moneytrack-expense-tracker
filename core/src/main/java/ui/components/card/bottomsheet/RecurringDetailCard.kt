package ui.components.card.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.components.navigation.button.ButtonVariant
import ui.components.navigation.button.LargeButton
import ui.components.navigation.common.SelectorChip
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun RecurringDetailCard(
    title: String,
    ctaText: String,
    onCtaClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(Dimens.radius20),
        color = AppTheme.colors.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing20),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
        ) {

            Text(
                text = title,
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.onSurface
            )

            content()

            LargeButton(
                text = ctaText,
                onClick = onCtaClick,
                variant = ButtonVariant.PRIMARY
            )
        }
    }
}


@Preview(name = "Recurring Detail Card – Light & Dark")
@Composable
private fun RecurringDetailCardPreview() {
    Column {

        MoneyTrackTheme(darkTheme = false) {
            PreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            PreviewContent()
        }
    }
}

@Composable
private fun PreviewContent() {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16)
    ) {

        RecurringDetailCard(
            title = "Yearly",
            ctaText = "Next",
            onCtaClick = {}
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12)
            ) {
                SelectorChip(
                    label = "Yearly",
                    selected = false,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )

                SelectorChip(
                    label = "April",
                    selected = false,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )

                SelectorChip(
                    label = "29",
                    selected = false,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12)
            ) {
                SelectorChip(
                    label = "Date",
                    selected = false,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )

                SelectorChip(
                    label = "27 Aug 2021",
                    selected = false,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
