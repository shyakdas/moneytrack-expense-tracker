// Copyright (c) 2026 shyakdas

package ui.components.navigation

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import ui.components.navigation.tabs.TimeRangeTab
import ui.components.navigation.tabs.ToggleButton
import ui.components.navigation.tabs.ToggleOption
import ui.theme.MoneyTrackTheme

class TabsScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun tabs_light_allStates() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    TimeRangeTab(
                        options = listOf("Today", "Week", "Month", "Year"),
                        selectedOption = "Today",
                        onOptionSelected = {}
                    )

                    TimeRangeTab(
                        options = listOf("Today", "Week", "Month", "Year"),
                        selectedOption = "Year",
                        onOptionSelected = {}
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ToggleButton(
                        selected = ToggleOption.EXPENSE,
                        onSelectedChange = {}
                    )

                    ToggleButton(
                        selected = ToggleOption.INCOME,
                        onSelectedChange = {}
                    )
                }
            }
        }
    }

    @Test
    fun tabs_dark_allStates() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    TimeRangeTab(
                        options = listOf("Today", "Week", "Month", "Year"),
                        selectedOption = "Month",
                        onOptionSelected = {}
                    )

                    ToggleButton(
                        selected = ToggleOption.INCOME,
                        onSelectedChange = {}
                    )
                }
            }
        }
    }
}
