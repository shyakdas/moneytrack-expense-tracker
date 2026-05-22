// Copyright (c) 2026 shyakdas

package ui.components.navigation.bottomNav

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MotionTokens

@Composable
fun PrimaryBottomNavigation(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = items.indexOfFirst { it.route == selectedRoute }.coerceAtLeast(0)
    val selectedSlot = selectedIndex.toRailSlot()
    val horizontalPadding = Dimens.spacing8

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.bottomNavContainerHeight)
            .testTag("PrimaryBottomNavigation")
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8)
                .height(Dimens.bottomNavHeight)
        ) {
            val navItems = items.withPlusAction(onFabClick = onFabClick)
            val contentWidth = maxWidth - (horizontalPadding * 2)
            val slotWidth = contentWidth / navItems.size
            val indicatorWidth = slotWidth - Dimens.spacing16
            val indicatorOffset by animateDpAsState(
                targetValue = horizontalPadding + (slotWidth * selectedSlot.toFloat()) + ((slotWidth - indicatorWidth) / 2f),
                animationSpec = MotionTokens.pressSpring(),
                label = "BottomNavIndicatorOffset",
            )

            Surface(
                tonalElevation = Dimens.elevation0,
                shadowElevation = Dimens.elevation8,
                shape = RoundedCornerShape(Dimens.radius40),
                color = AppTheme.colors.surface,
                border = BorderStroke(
                    width = Dimens.borderNormal,
                    color = AppTheme.colors.outline.copy(alpha = 0.55f),
                ),
                modifier = Modifier.fillMaxSize(),
            ) {}

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset, y = Dimens.spacing8)
                    .width(indicatorWidth)
                    .height(Dimens.buttonLargeHeight)
                    .background(
                        color = AppTheme.colors.primaryContainer.copy(alpha = 0.62f),
                        shape = RoundedCornerShape(Dimens.radius40),
                    ),
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset, y = Dimens.spacing8)
                    .width(indicatorWidth)
                    .height(Dimens.buttonLargeHeight)
                    .background(
                        color = AppTheme.colors.primary,
                        shape = RoundedCornerShape(Dimens.radius40),
                    ),
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { navItem ->
                    when (navItem) {
                        is BottomNavRailItem.Action -> {
                            BottomBarItem(
                                item = navItem.item,
                                isSelected = false,
                                onClick = navItem.onClick,
                                modifier = Modifier.width(slotWidth),
                            )
                        }

                        is BottomNavRailItem.Destination -> {
                            BottomBarItem(
                                item = navItem.item,
                                isSelected = navItem.item.route == selectedRoute,
                                onClick = { onItemClick(navItem.item) },
                                modifier = Modifier.width(slotWidth),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Primary Bottom Navigation – Light & Dark")
@Composable
private fun PrimaryBottomNavigationPreview() {
    val items = listOf(
        BottomNavItem("home", R.drawable.home, "Home"),
        BottomNavItem("transaction", R.drawable.transaction, "Transaction"),
        BottomNavItem("profile", R.drawable.user, "Profile")
    )

    Column {
        ui.theme.MoneyTrackTheme(darkTheme = false) {
            PrimaryBottomNavigation(
                items = items,
                selectedRoute = "home",
                onItemClick = {},
                onFabClick = {}
            )
        }

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            PrimaryBottomNavigation(
                items = items,
                selectedRoute = "home",
                onItemClick = {},
                onFabClick = {}
            )
        }
    }
}

private sealed interface BottomNavRailItem {
    data class Destination(val item: BottomNavItem) : BottomNavRailItem
    data class Action(val item: BottomNavItem, val onClick: () -> Unit) : BottomNavRailItem
}

private fun List<BottomNavItem>.withPlusAction(onFabClick: () -> Unit): List<BottomNavRailItem> =
    listOf(
        BottomNavRailItem.Destination(this[0]),
        BottomNavRailItem.Destination(this[1]),
        BottomNavRailItem.Action(
            item = BottomNavItem(
                route = "add",
                icon = R.drawable.add,
                label = "Add",
            ),
            onClick = onFabClick,
        ),
        BottomNavRailItem.Destination(this[2]),
    )

private fun Int.toRailSlot(): Int =
    if (this >= 2) this + 1 else this
