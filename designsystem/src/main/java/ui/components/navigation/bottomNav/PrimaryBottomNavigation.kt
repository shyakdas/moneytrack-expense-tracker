package ui.components.navigation.bottomNav

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun PrimaryBottomNavigation(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.bottomNavContainerHeight)
            .testTag("PrimaryBottomNavigation")
    ) {

        Surface(
            tonalElevation = Dimens.spacing6,
            shadowElevation = Dimens.spacing8,
            shape = RoundedCornerShape(
                topStart = Dimens.radius24,
                topEnd = Dimens.radius24
            ),
            color = AppTheme.colors.surfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(Dimens.bottomNavHeight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.spacing32),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    BottomBarItem(
                        item = item,
                        isSelected = item.route == selectedRoute,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onFabClick,
            containerColor = AppTheme.colors.primary,
            contentColor = AppTheme.colors.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -Dimens.spacing28)
                .size(Dimens.fabSize)
                .shadow(Dimens.spacing8, CircleShape)
                .testTag("BottomBarFab")
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add"
            )
        }
    }
}

@Preview(name = "Primary Bottom Navigation – Light & Dark")
@Composable
private fun PrimaryBottomNavigationPreview() {
    val items = listOf(
        BottomNavItem("home", R.drawable.home, "Home"),
        BottomNavItem("transaction", R.drawable.transaction, "Transaction"),
        BottomNavItem("budget", R.drawable.line_chart_2, "Budget"),
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

