package ui.components.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

        IconButton(onClick = config.onDropdownClick) {
            Text(
                text = config.label,
                style = MaterialTheme.typography.bodyMedium
            )
        }

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
                Badge {
                    Text(badgeCount.toString())
                }
            }
        }
    ) {
        IconButton(onClick = onClick) {
//            Icon(Icons.Default.Tune, contentDescription = "Filter")
        }
    }
}
