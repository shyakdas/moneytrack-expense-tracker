package ui.components.form

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import com.moneytrack.designsystem.R
import ui.components.form.input.InputField
import ui.theme.MoneyTrackTheme

class InputFieldScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun inputField_light_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                MaterialTheme {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        InputField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Input..."
                        )

                        InputField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Input...",
                            trailingIcon = ImageVector.vectorResource(R.drawable.show)
                        )

                        InputField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Input",
                            leadingIcon = ImageVector.vectorResource(R.drawable.show)
                        )

                        InputField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Input...",
                            leadingIcon = ImageVector.vectorResource(R.drawable.show),
                            trailingIcon = ImageVector.vectorResource(R.drawable.show)
                        )

                        InputField(
                            value = "Add attachment",
                            onValueChange = {},
                            leadingIcon = ImageVector.vectorResource(R.drawable.attachment)
                        )
                    }
                }
            }
        }
    }

    @Test
    fun inputField_dark_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                MaterialTheme {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        InputField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Input..."
                        )

                        InputField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Input...",
                            trailingIcon = ImageVector.vectorResource(R.drawable.show)
                        )

                        InputField(
                            value = "Add attachment",
                            onValueChange = {},
                            leadingIcon = ImageVector.vectorResource(R.drawable.attachment)
                        )
                    }
                }
            }
        }
    }
}

