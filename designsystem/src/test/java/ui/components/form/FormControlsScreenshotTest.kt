// Copyright (c) 2026 shyakdas

package ui.components.form

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.form.control.PrimaryCheckbox
import ui.components.form.control.PrimaryRadioButton
import ui.components.form.control.PrimarySlider
import ui.components.form.control.PrimarySwitch
import ui.theme.MoneyTrackTheme

class FormControlsScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun formControls_light() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                FormControlsContent()
            }
        }
    }

    @Test
    fun formControls_dark() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                FormControlsContent()
            }
        }
    }
}

@Composable
private fun FormControlsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PrimaryCheckbox(checked = false, onCheckedChange = {})
            PrimaryCheckbox(checked = true, onCheckedChange = {})
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PrimaryRadioButton(selected = false, onClick = {})
            PrimaryRadioButton(selected = true, onClick = {})
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PrimarySwitch(checked = false, onCheckedChange = {})
            PrimarySwitch(checked = true, onCheckedChange = {})
        }

        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            PrimarySlider(value = 0f, onValueChange = {})
            PrimarySlider(value = 50f, onValueChange = {})
            PrimarySlider(value = 90f, onValueChange = {})
        }
    }
}
