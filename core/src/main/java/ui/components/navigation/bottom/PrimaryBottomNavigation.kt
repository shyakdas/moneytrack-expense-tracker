package ui.components.navigation.bottom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.theme.Light60
import ui.theme.MoneyTrackTheme
import ui.theme.Violet100

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
            .height(90.dp)
            .testTag("PrimaryBottomNavigation")
    ) {

        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Light60,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
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
            containerColor = Violet100,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .size(64.dp)
                .shadow(8.dp, CircleShape)
                .testTag("BottomBarFab")
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

private val previewBottomNavItems = listOf(
    BottomNavItem(
        route = "home",
        icon = R.drawable.home,
        label = "Home"
    ),
    BottomNavItem(
        route = "transaction",
        icon = R.drawable.transaction,
        label = "Transaction"
    ),
    BottomNavItem(
        route = "budget",
        icon = R.drawable.line_chart_2,
        label = "Budget"
    ),
    BottomNavItem(
        route = "profile",
        icon = R.drawable.user,
        label = "Profile"
    )
)

@Preview(
    name = "Light Mode - Default",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun PrimaryBottomNavigationLightPreview() {
    MoneyTrackTheme(darkTheme = false) {
        Box(modifier = Modifier.fillMaxSize()) {
            PrimaryBottomNavigation(
                items = previewBottomNavItems,
                selectedRoute = "home",
                onItemClick = {},
                onFabClick = {},
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
