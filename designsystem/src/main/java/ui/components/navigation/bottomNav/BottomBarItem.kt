// Copyright (c) 2026 shyakdas

package ui.components.navigation.bottomNav

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

@Composable
internal fun BottomBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val color by animateColorAsState(
        targetValue =
            if (isSelected) AppTheme.colors.primary
            else AppTheme.colors.onSurfaceVariant,
        label = "BottomNavColorAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(Dimens.iconButtonSize)
                .clickable(onClick = onClick)
                .testTag("NavItem_${item.route}"),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = item.icon),
                contentDescription = item.label,
                tint = color,
                modifier = Modifier.size(Dimens.icon24)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing1))

        Text(
            text = item.label,
            style = AppTheme.typography.labelMedium,
            color = color
        )
    }
}


@Preview(name = "BottomBarItem – Light & Dark")
@Composable
private fun BottomBarItemPreview() {
    Column {
        MoneyTrackTheme(darkTheme = false) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarItem(
                    item = BottomNavItem(
                        route = "home",
                        icon = R.drawable.home,
                        label = "Home"
                    ),
                    isSelected = true,
                    onClick = {}
                )

                Spacer(modifier = Modifier.size(Dimens.spacing24))

                BottomBarItem(
                    item = BottomNavItem(
                        route = "profile",
                        icon = R.drawable.user,
                        label = "Profile"
                    ),
                    isSelected = false,
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.size(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarItem(
                    item = BottomNavItem(
                        route = "home",
                        icon = R.drawable.home,
                        label = "Home"
                    ),
                    isSelected = true,
                    onClick = {}
                )

                Spacer(modifier = Modifier.size(Dimens.spacing24))

                BottomBarItem(
                    item = BottomNavItem(
                        route = "profile",
                        icon = R.drawable.user,
                        label = "Profile"
                    ),
                    isSelected = false,
                    onClick = {}
                )
            }
        }
    }
}
