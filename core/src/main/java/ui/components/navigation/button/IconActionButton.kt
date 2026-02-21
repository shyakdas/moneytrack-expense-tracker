package ui.components.navigation.button

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun IconActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: IconButtonVariant = IconButtonVariant.OUTLINED,
    shape: IconButtonShape = IconButtonShape.ROUNDED_RECT,
    iconTint: Color = AppTheme.colors.onSurface
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(shape.shape)
            .then(
                when (variant) {
                    IconButtonVariant.OUTLINED ->
                        Modifier.border(
                            width = 1.dp,
                            color = AppTheme.colors.outline,
                            shape = shape.shape
                        )

                    IconButtonVariant.FILLED ->
                        Modifier.background(
                            color = AppTheme.colors.surfaceVariant,
                            shape = shape.shape
                        )
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = iconTint
        )
    }
}

@Preview(name = "Icon Action Button – Light & Dark")
@Composable
private fun IconActionButtonPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            Row(
                modifier = Modifier
                    .background(AppTheme.colors.background)
                    .padding(Dimens.spacing16),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconActionButton(
                    icon = ImageVector.vectorResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    onClick = {},
                    variant = IconButtonVariant.OUTLINED,
                    shape = IconButtonShape.ROUNDED_RECT
                )

                IconActionButton(
                    icon = ImageVector.vectorResource(id = R.drawable.close),
                    contentDescription = "Close",
                    onClick = {},
                    variant = IconButtonVariant.FILLED,
                    shape = IconButtonShape.CIRCLE
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            Row(
                modifier = Modifier
                    .background(AppTheme.colors.background)
                    .padding(Dimens.spacing16),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconActionButton(
                    icon = ImageVector.vectorResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    onClick = {},
                    variant = IconButtonVariant.OUTLINED,
                    shape = IconButtonShape.ROUNDED_RECT
                )

                IconActionButton(
                    icon = ImageVector.vectorResource(id = R.drawable.close),
                    contentDescription = "Close",
                    onClick = {},
                    variant = IconButtonVariant.FILLED,
                    shape = IconButtonShape.CIRCLE
                )
            }
        }
    }
}
