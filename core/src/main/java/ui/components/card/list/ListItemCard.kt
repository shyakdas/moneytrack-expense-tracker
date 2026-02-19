package ui.components.card.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.theme.Violet100

@Composable
fun ListItemCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    rightText: String? = null,
    variant: ListItemVariant,
    selected: Boolean = false,
    switchChecked: Boolean = false,
    onClick: (() -> Unit)? = null,
    onSwitchChange: ((Boolean) -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        when (variant) {
            ListItemVariant.DEFAULT -> {
                if (rightText != null) {
                    Text(
                        text = rightText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Violet100
                    )
                }
            }

            ListItemVariant.SELECT -> {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Violet100, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            ListItemVariant.SWITCH -> {
                Switch(
                    checked = switchChecked,
                    onCheckedChange = onSwitchChange
                )
            }
        }
    }
}

@Preview(
    name = "List Item Variants",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ListItemCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ListItemCard(
                title = "Title",
                description = "Description",
                rightText = "19.30",
                variant = ListItemVariant.DEFAULT,
                onClick = {}
            )

            ListItemCard(
                title = "Title",
                variant = ListItemVariant.SELECT,
                selected = true,
                onClick = {}
            )

            ListItemCard(
                title = "Title",
                description = "Description",
                variant = ListItemVariant.SWITCH,
                switchChecked = false,
                onSwitchChange = {}
            )

            ListItemCard(
                title = "Title",
                description = "Description",
                variant = ListItemVariant.SWITCH,
                switchChecked = true,
                onSwitchChange = {}
            )
        }
    }
}
