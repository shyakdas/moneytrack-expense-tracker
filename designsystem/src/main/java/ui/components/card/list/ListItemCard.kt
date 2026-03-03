package ui.components.card.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens

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
                color = AppTheme.colors.surface,
                shape = RoundedCornerShape(Dimens.radius16)
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing4)
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onSurface
            )

            if (description != null) {
                Text(
                    text = description,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(Dimens.spacing12))

        when (variant) {

            ListItemVariant.DEFAULT -> {
                if (rightText != null) {
                    Text(
                        text = rightText,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = AppTheme.colors.primary
                    )
                }
            }

            ListItemVariant.SELECT -> {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(Dimens.checkboxSize)
                            .background(
                                color = AppTheme.colors.primary,
                                shape = RoundedCornerShape(Dimens.radius12)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AppTheme.colors.onPrimary,
                            modifier = Modifier.size(Dimens.icon16)
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

@Preview(name = "List Item Card – Light & Dark")
@Composable
private fun ListItemCardPreview() {
    Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            ListItemPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            ListItemPreviewContent()
        }
    }
}

@Composable
private fun ListItemPreviewContent() {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
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
