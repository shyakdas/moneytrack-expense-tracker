package ui.components.navigation.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@Composable
fun IconActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: IconButtonVariant = IconButtonVariant.OUTLINED,
    shape: IconButtonShape = IconButtonShape.ROUNDED_RECT,
    iconTint: Color = MaterialTheme.colorScheme.onSurface
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
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = shape.shape
                        )

                    IconButtonVariant.FILLED ->
                        Modifier.background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = shape.shape
                        )
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = iconTint
        )
    }
}

@Preview(
    name = "Icon Action Button – All Variants",
    showBackground = true,
    backgroundColor = 0x808080
)
@Composable
private fun IconActionButtonPreview() {
    MaterialTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
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
