package ui.components.navigation

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme
import com.moneytrack.designsystem.R
import ui.components.navigation.bottomNav.BottomNavItem
import ui.components.navigation.bottomNav.PrimaryBottomNavigation

class PrimaryBottomNavigationScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    private val items = listOf(
        BottomNavItem("home", R.drawable.home, "Home"),
        BottomNavItem("transaction", R.drawable.transaction, "Transaction"),
        BottomNavItem("budget", R.drawable.line_chart_2, "Budget"),
        BottomNavItem("profile", R.drawable.user, "Profile"),
    )

    @Test
    fun primaryBottomNavigation_light_homeSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                PrimaryBottomNavigation(
                    items = items,
                    selectedRoute = "home",
                    onItemClick = {},
                    onFabClick = {}
                )
            }
        }
    }

    @Test
    fun primaryBottomNavigation_light_transactionSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                PrimaryBottomNavigation(
                    items = items,
                    selectedRoute = "transaction",
                    onItemClick = {},
                    onFabClick = {}
                )
            }
        }
    }

    @Test
    fun primaryBottomNavigation_dark_homeSelected() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                PrimaryBottomNavigation(
                    items = items,
                    selectedRoute = "home",
                    onItemClick = {},
                    onFabClick = {}
                )
            }
        }
    }
}
