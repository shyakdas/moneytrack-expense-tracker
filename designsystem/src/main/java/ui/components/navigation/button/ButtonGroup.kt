package ui.components.navigation.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun ButtonGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12)
    ) {
        content()
    }
}

@Preview(name = "Button Group – Light & Dark")
@Composable
private fun ButtonGroupPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            Row(
                modifier = Modifier
                    .background(AppTheme.colors.background)
                    .padding(Dimens.spacing16)
            ) {
                ButtonGroup {
                    SmallButton(
                        text = "Secondary",
                        onClick = {},
                        variant = ButtonVariant.SECONDARY
                    )

                    SmallButton(
                        text = "Primary",
                        onClick = {},
                        variant = ButtonVariant.PRIMARY
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            Row(
                modifier = Modifier
                    .background(AppTheme.colors.background)
                    .padding(Dimens.spacing16)
            ) {
                ButtonGroup {
                    SmallButton(
                        text = "Secondary",
                        onClick = {},
                        variant = ButtonVariant.SECONDARY
                    )

                    SmallButton(
                        text = "Primary",
                        onClick = {},
                        variant = ButtonVariant.PRIMARY
                    )
                }
            }
        }
    }
}
