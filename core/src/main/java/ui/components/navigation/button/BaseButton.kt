package ui.components.navigation.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
internal fun BaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant,
    size: ButtonSize,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {

    val isFullWidth = size == ButtonSize.LARGE

    val backgroundColor: Color
    val contentColor: Color
    val borderStroke: BorderStroke?

    when (variant) {

        ButtonVariant.PRIMARY -> {
            backgroundColor = AppTheme.colors.primary
            contentColor = AppTheme.colors.onPrimary
            borderStroke = null
        }

        ButtonVariant.SECONDARY -> {
            backgroundColor = AppTheme.colors.primary.copy(alpha = 0.12f)
            contentColor = AppTheme.colors.primary
            borderStroke = null
        }

        ButtonVariant.TERTIARY -> {
            backgroundColor = Color.Transparent
            contentColor = AppTheme.colors.onSurface
            borderStroke = BorderStroke(
                width = Dimens.spacing1,
                color = AppTheme.colors.outline
            )
        }
    }

    Surface(
        modifier = modifier
            .then(
                if (isFullWidth) Modifier.fillMaxWidth()
                else Modifier.wrapContentWidth()
            )
            .height(size.height)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(Dimens.spacing16),
        color = backgroundColor,
        contentColor = contentColor,
        border = borderStroke
    ) {
        Row(
            modifier = Modifier.padding(horizontal = size.horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(size.iconSize),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(Dimens.spacing8))
            }

            Text(
                text = text,
                style = AppTheme.typography.titleMedium,
                color = contentColor
            )
        }
    }
}
