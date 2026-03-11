// Copyright (c) 2026 shyakdas

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
    val buttonColors = buttonStyle(variant = variant, enabled = enabled)

    Surface(
        modifier = modifier
            .then(
                if (isFullWidth) Modifier.fillMaxWidth()
                else Modifier.wrapContentWidth()
            )
            .height(size.height)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(Dimens.spacing16),
        color = buttonColors.backgroundColor,
        contentColor = buttonColors.contentColor,
        border = buttonColors.borderStroke
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
                    tint = buttonColors.contentColor
                )
                Spacer(modifier = Modifier.width(Dimens.spacing8))
            }

            Text(
                text = text,
                style = AppTheme.typography.titleMedium,
                color = buttonColors.contentColor
            )
        }
    }
}

@Composable
private fun buttonStyle(
    variant: ButtonVariant,
    enabled: Boolean,
): ButtonStyle {
    // The design language is intentionally unified across variants for now.
    val resolvedVariant = variant
    val baseBackgroundColor = AppTheme.colors.background
    val baseContentColor = AppTheme.colors.onBackground
    val contentAlpha = if (enabled) 1f else 0.48f
    val borderAlpha = if (enabled) 1f else 0.48f
    val borderColor = when (resolvedVariant) {
        ButtonVariant.PRIMARY,
        ButtonVariant.SECONDARY,
        ButtonVariant.TERTIARY -> baseContentColor.copy(alpha = borderAlpha)
    }

    return ButtonStyle(
        backgroundColor = baseBackgroundColor,
        contentColor = baseContentColor.copy(alpha = contentAlpha),
        borderStroke = BorderStroke(
            width = Dimens.spacing1,
            color = borderColor,
        ),
    )
}

private data class ButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderStroke: BorderStroke,
)
