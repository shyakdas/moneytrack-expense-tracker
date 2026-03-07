// Copyright (c) 2026 shyakdas

package ui.components.navigation.topNav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
fun TopNavigation(
    config: TopNavigationConfig,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppTheme.colors.surface,
        tonalElevation = Dimens.spacing2
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


@Preview(name = "TopNavigation – All Variants (Light & Dark)")
@Composable
private fun TopNavigationAllVariantsPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            Surface(color = AppTheme.colors.background) {
                TopNavigationPreviewContent()
            }
        }
        MoneyTrackTheme(darkTheme = true) {
            Surface(color = AppTheme.colors.background) {
                TopNavigationPreviewContent()
            }
        }
    }
}

@Composable
private fun TopNavigationPreviewContent() {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        TopNavigation(
            config = TopNavigationConfig.BackWithTitle(
                title = "Notification",
                showMore = false,
                onBackClick = {}
            )
        )

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        TopNavigation(
            config = TopNavigationConfig.BackWithTitle(
                title = "Transactions",
                showMore = true,
                onBackClick = {},
                onMoreClick = {}
            )
        )

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        TopNavigation(
            config = TopNavigationConfig.TitleOnly(
                title = "Dashboard"
            )
        )

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        TopNavigation(
            config = TopNavigationConfig.ProfileWithSelector(
                profileImage = androidx.compose.ui.graphics.painter.ColorPainter(
                    androidx.compose.ui.graphics.Color.Gray
                ),
                selectedMonth = "October",
                onMonthClick = {},
                onActionClick = {}
            )
        )

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        TopNavigation(
            config = TopNavigationConfig.DropdownWithFilter(
                label = "Month",
                showBadge = false,
                badgeCount = 0,
                onDropdownClick = {},
                onFilterClick = {}
            )
        )

        Spacer(modifier = Modifier.height(Dimens.spacing16))

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
