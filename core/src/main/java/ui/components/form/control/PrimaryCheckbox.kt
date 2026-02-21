package ui.components.form.control

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun PrimaryCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(Dimens.checkboxSize)
            .border(
                width = Dimens.spacing1_5,
                color = AppTheme.colors.primary,
                shape = RoundedCornerShape(Dimens.radius6)
            )
            .background(
                color = if (checked)
                    AppTheme.colors.primary
                else
                    AppTheme.colors.background,
                shape = RoundedCornerShape(Dimens.radius6)
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AppTheme.colors.onPrimary,
                modifier = Modifier.size(Dimens.icon16)
            )
        }
    }
}

@Preview(name = "Primary Checkbox – Light & Dark")
@Composable
private fun PrimaryCheckboxPreview() {
    Column {

        MoneyTrackTheme(darkTheme = false) {
            CheckboxPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            CheckboxPreviewContent()
        }
    }
}

@Composable
private fun CheckboxPreviewContent() {
    Row(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PrimaryCheckbox(
            checked = false,
            onCheckedChange = {}
        )

        PrimaryCheckbox(
            checked = true,
            onCheckedChange = {}
        )
    }
}
