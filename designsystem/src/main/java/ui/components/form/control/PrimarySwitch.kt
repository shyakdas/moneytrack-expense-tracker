package ui.components.form.control

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun PrimarySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(Dimens.switchWidth)
            .height(Dimens.switchHeight)
            .background(
                color = if (checked)
                    AppTheme.colors.primary
                else
                    AppTheme.colors.surfaceVariant,
                shape = RoundedCornerShape(Dimens.switchRadius)
            )
            .clickable { onCheckedChange(!checked) }
            .padding(Dimens.spacing2)
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.switchThumbSize)
                .align(
                    if (checked)
                        Alignment.CenterEnd
                    else
                        Alignment.CenterStart
                )
                .background(
                    color = AppTheme.colors.onPrimary,
                    shape = CircleShape
                )
        )
    }
}

@Preview(name = "Primary Switch – Light & Dark")
@Composable
private fun PrimarySwitchPreview() {
    Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            SwitchPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            SwitchPreviewContent()
        }
    }
}

@Composable
private fun SwitchPreviewContent() {
    Row(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PrimarySwitch(
            checked = false,
            onCheckedChange = {}
        )

        PrimarySwitch(
            checked = true,
            onCheckedChange = {}
        )
    }
}
