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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.components.navigation.common.SelectorChip
import ui.theme.Violet100

@Composable
internal fun DropdownFilterNavigation(
    config: TopNavigationConfig.DropdownWithFilter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
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
                    containerColor = Violet100,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = badgeCount.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.sort),
                contentDescription = "Sort"
            )
        }
    }
}

@Preview(
    name = "Dropdown Filter Navigation",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun DropdownFilterNavigationPreview() {
    MaterialTheme {
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
