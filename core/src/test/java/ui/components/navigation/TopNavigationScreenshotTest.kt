package ui.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme

class TopNavigationScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun topNavigation_light_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                Column {
                    TopNavigation(
                        config = TopNavigationConfig.BackWithTitle(
                            title = "Notification",
                            showMore = false,
                            onBackClick = {}
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TopNavigation(
                        config = TopNavigationConfig.BackWithTitle(
                            title = "Transactions",
                            showMore = true,
                            onBackClick = {},
                            onMoreClick = {}
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TopNavigation(
                        config = TopNavigationConfig.TitleOnly(
                            title = "Dashboard"
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TopNavigation(
                        config = TopNavigationConfig.ProfileWithSelector(
                            profileImage = ColorPainter(Color.Gray),
                            selectedMonth = "October",
                            onMonthClick = {},
                            onActionClick = {}
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TopNavigation(
                        config = TopNavigationConfig.DropdownWithFilter(
                            label = "Month",
                            showBadge = false,
                            badgeCount = 0,
                            onDropdownClick = {},
                            onFilterClick = {}
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
    }

    @Test
    fun topNavigation_dark_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                Column {

                    TopNavigation(
                        config = TopNavigationConfig.BackWithTitle(
                            title = "Notification",
                            showMore = true,
                            onBackClick = {},
                            onMoreClick = {}
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TopNavigation(
                        config = TopNavigationConfig.TitleOnly(
                            title = "Dashboard"
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TopNavigation(
                        config = TopNavigationConfig.ProfileWithSelector(
                            profileImage = ColorPainter(Color.DarkGray),
                            selectedMonth = "October",
                            onMonthClick = {},
                            onActionClick = {}
                        )
                    )
                }
            }
        }
    }
}
