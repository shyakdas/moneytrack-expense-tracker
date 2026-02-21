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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun PrimaryRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(Dimens.radioOuterSize)
            .border(
                width = Dimens.spacing2,
                color = AppTheme.colors.primary,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(Dimens.radioInnerSize)
                    .background(
                        color = AppTheme.colors.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}


@Composable
private fun RadioPreviewContent() {
    Row(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PrimaryRadioButton(
            selected = false,
            onClick = {}
        )

        PrimaryRadioButton(
            selected = true,
            onClick = {}
        )
    }
}

@Preview(name = "Primary Radio Button – Light & Dark")
@Composable
private fun PrimaryRadioButtonPreview() {
    Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            RadioPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            RadioPreviewContent()
        }
    }
}
