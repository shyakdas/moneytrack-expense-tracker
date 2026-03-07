// Copyright (c) 2026 shyakdas

package ui.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import ui.components.navigation.bars.ChartActionBar
import ui.components.navigation.bars.FilterBar
import ui.components.navigation.bars.MonthNavigationBar
import ui.components.navigation.bars.ReportCtaBar
import ui.components.navigation.bars.SectionHeaderBar
import ui.components.navigation.bars.SimpleTitleBar
import ui.theme.MoneyTrackTheme

class BarsScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun bars_light_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    FilterBar(
                        onMonthClick = {},
                        onSortClick = {}
                    )

                    SimpleTitleBar(
                        title = "Yesterday"
                    )

                    SectionHeaderBar(
                        title = "Spend Frequency",
                        onSeeAllClick = {}
                    )

                    MonthNavigationBar(
                        month = "May",
                        onPrevious = {},
                        onNext = {}
                    )

                    ChartActionBar(
                        onMonthClick = {},
                        onLineChartClick = {},
                        onPieChartClick = {}
                    )

                    ReportCtaBar(
                        onClick = {}
                    )
                }
            }
        }
    }

    @Test
    fun bars_dark_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    FilterBar(
                        onMonthClick = {},
                        onSortClick = {}
                    )

                    SimpleTitleBar(
                        title = "Yesterday"
                    )

                    SectionHeaderBar(
                        title = "Spend Frequency",
                        onSeeAllClick = {}
                    )

                    MonthNavigationBar(
                        month = "May",
                        onPrevious = {},
                        onNext = {}
                    )

                    ChartActionBar(
                        onMonthClick = {},
                        onLineChartClick = {},
                        onPieChartClick = {}
                    )

                    ReportCtaBar(
                        onClick = {}
                    )
                }
            }
        }
    }
}
