package ui.components.button

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
import androidx.compose.ui.unit.dp
import ui.theme.Violet100
import ui.theme.Violet20

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
            backgroundColor = Violet100
            contentColor = Color.White
            borderStroke = null
        }

        ButtonVariant.SECONDARY -> {
            backgroundColor = Violet20
            contentColor = Violet100
            borderStroke = null
        }

        ButtonVariant.TERTIARY -> {
            backgroundColor = Color.Transparent
            contentColor = MaterialTheme.colorScheme.onSurface
            borderStroke = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
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
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        contentColor = contentColor,
        border = borderStroke
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = size.horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(size.iconSize)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
