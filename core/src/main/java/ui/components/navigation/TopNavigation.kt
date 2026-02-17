package ui.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopNavigation(
    config: TopNavigationConfig,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 2.dp
    ) {
        when (config) {

            is TopNavigationConfig.BackWithTitle -> {
                BackTitleNavigation(config)
            }

            is TopNavigationConfig.TitleOnly -> {
                CenterTitleNavigation(config)
            }

            is TopNavigationConfig.ProfileWithSelector -> {
                ProfileSelectorNavigation(config)
            }

            is TopNavigationConfig.DropdownWithFilter -> {
                DropdownFilterNavigation(config)
            }
        }
    }
}

@Preview(
    name = "TopNavigation – All Variants",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun TopNavigationAllVariantsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopNavigation(
                config = TopNavigationConfig.BackWithTitle(
                    title = "Notification",
                    showMore = false,
                    onBackClick = {}
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            TopNavigation(
                config = TopNavigationConfig.BackWithTitle(
                    title = "Transactions",
                    showMore = true,
                    onBackClick = {},
                    onMoreClick = {}
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            TopNavigation(
                config = TopNavigationConfig.TitleOnly(
                    title = "Dashboard"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TopNavigation(
                config = TopNavigationConfig.ProfileWithSelector(
                    profileImage = ColorPainter(Color.Gray),
                    selectedMonth = "October",
                    onMonthClick = {},
                    onActionClick = {}
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TopNavigation(
                config = TopNavigationConfig.DropdownWithFilter(
                    label = "Month",
                    showBadge = false,
                    badgeCount = 0,
                    onDropdownClick = {},
                    onFilterClick = {}
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TopNavigation(
                config = TopNavigationConfig.DropdownWithFilter(
                    label = "Month",
                    showBadge = true,
                    badgeCount = 3,
                    onDropdownClick = {},
                    onFilterClick = {}
                )
            )
        }
    }
}

