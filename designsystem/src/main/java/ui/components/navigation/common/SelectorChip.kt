package ui.components.navigation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun SelectorChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    val backgroundColor =
        if (selected) AppTheme.colors.primary.copy(alpha = 0.12f)
        else AppTheme.colors.surfaceVariant

    val textColor =
        if (selected) AppTheme.colors.primary
        else AppTheme.colors.onSurface

    Row(
        modifier = modifier
            .height(Dimens.spacing36)
            .background(backgroundColor, RoundedCornerShape(Dimens.radius20))
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(Dimens.icon16)
            )
            Spacer(modifier = Modifier.width(Dimens.spacing8))
        }

        Text(
            text = label,
            style = AppTheme.typography.bodyMedium,
            color = textColor
        )
    }
}


@Preview(name = "Selector Chip – Light & Dark")
@Composable
private fun SelectorChipPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            SelectorChip(
                label = "Month",
                selected = true,
                onClick = {},
                leadingIcon = ImageVector.vectorResource(R.drawable.arrow_down_2)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            SelectorChip(
                label = "Month",
                selected = false,
                onClick = {},
                leadingIcon = ImageVector.vectorResource(R.drawable.arrow_down_2)
            )
        }
    }
}
