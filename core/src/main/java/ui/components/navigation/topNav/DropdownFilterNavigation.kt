package ui.components.navigation.topNav

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.core.R
import ui.components.navigation.common.SelectorChip
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
internal fun DropdownFilterNavigation(
    config: TopNavigationConfig.DropdownWithFilter
) {
    Surface(
        color = AppTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonLargeHeight)
                .padding(horizontal = Dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SelectorChip(
                label = config.label,
                onClick = config.onDropdownClick,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.arrow_down_2)
            )

            Spacer(modifier = Modifier.weight(1f))

            FilterWithBadge(
                showBadge = config.showBadge,
                badgeCount = config.badgeCount,
                onClick = config.onFilterClick
            )
        }
    }
}


@Composable
fun FilterWithBadge(
    showBadge: Boolean,
    badgeCount: Int,
    onClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (showBadge) {
                Badge(
                    containerColor = AppTheme.colors.primary,
                    contentColor = AppTheme.colors.onPrimary
                ) {
                    Text(
                        text = badgeCount.toString(),
                        style = AppTheme.typography.labelMedium
                    )
                }
            }
        }
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.sort),
                contentDescription = "Sort",
                tint = AppTheme.colors.onSurface
            )
        }
    }
}

@Preview(name = "Dropdown Filter Navigation – Light & Dark")
@Composable
private fun DropdownFilterNavigationPreview() {
    androidx.compose.foundation.layout.Column {

        MoneyTrackTheme(darkTheme = false) {
            Surface(color = AppTheme.colors.background) {
                DropdownFilterNavigation(
                    config = TopNavigationConfig.DropdownWithFilter(
                        label = "Month",
                        showBadge = true,
                        badgeCount = 1,
                        onDropdownClick = {},
                        onFilterClick = {}
                    )
                )
            }
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.height(Dimens.spacing16)
        )

        MoneyTrackTheme(darkTheme = true) {
            Surface(color = AppTheme.colors.background) {
                DropdownFilterNavigation(
                    config = TopNavigationConfig.DropdownWithFilter(
                        label = "Month",
                        showBadge = true,
                        badgeCount = 1,
                        onDropdownClick = {},
                        onFilterClick = {}
                    )
                )
            }
        }
    }
}
