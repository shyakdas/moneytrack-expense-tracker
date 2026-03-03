package ui.components.navigation


import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import ui.theme.MoneyTrackTheme
import com.moneytrack.designsystem.R
import ui.components.navigation.button.ButtonGroup
import ui.components.navigation.button.ButtonVariant
import ui.components.navigation.button.IconActionButton
import ui.components.navigation.button.IconButtonShape
import ui.components.navigation.button.IconButtonVariant
import ui.components.navigation.button.LargeButton
import ui.components.navigation.button.SmallButton
import ui.components.navigation.common.SeeAllPill

class ButtonsScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun buttons_light_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    LargeButton(
                        text = "Primary",
                        onClick = {},
                        variant = ButtonVariant.PRIMARY
                    )

                    LargeButton(
                        text = "Secondary",
                        onClick = {},
                        variant = ButtonVariant.SECONDARY
                    )

                    LargeButton(
                        text = "Tertiary",
                        onClick = {},
                        variant = ButtonVariant.TERTIARY
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SmallButton(
                        text = "Primary",
                        onClick = {},
                        variant = ButtonVariant.PRIMARY
                    )

                    SmallButton(
                        text = "Secondary",
                        onClick = {},
                        variant = ButtonVariant.SECONDARY
                    )

                    SmallButton(
                        text = "Tertiary",
                        onClick = {},
                        variant = ButtonVariant.TERTIARY
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ButtonGroup {
                        SmallButton(
                            text = "Secondary",
                            onClick = {},
                            variant = ButtonVariant.SECONDARY
                        )

                        SmallButton(
                            text = "Primary",
                            onClick = {},
                            variant = ButtonVariant.PRIMARY
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        IconActionButton(
                            icon = ImageVector.vectorResource(id = R.drawable.edit),
                            contentDescription = "Edit",
                            onClick = {},
                            variant = IconButtonVariant.OUTLINED,
                            shape = IconButtonShape.ROUNDED_RECT
                        )

                        IconActionButton(
                            icon = ImageVector.vectorResource(id = R.drawable.close),
                            contentDescription = "Close",
                            onClick = {},
                            variant = IconButtonVariant.FILLED,
                            shape = IconButtonShape.CIRCLE
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SeeAllPill(
                        onClick = {}
                    )
                }
            }
        }
    }

    @Test
    fun buttons_dark_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    LargeButton(
                        text = "Primary",
                        onClick = {},
                        variant = ButtonVariant.PRIMARY
                    )

                    SmallButton(
                        text = "Secondary",
                        onClick = {},
                        variant = ButtonVariant.SECONDARY
                    )

                    ButtonGroup {
                        SmallButton(
                            text = "Secondary",
                            onClick = {},
                            variant = ButtonVariant.SECONDARY
                        )

                        SmallButton(
                            text = "Primary",
                            onClick = {},
                            variant = ButtonVariant.PRIMARY
                        )
                    }

                    IconActionButton(
                        icon = ImageVector.vectorResource(id = R.drawable.close),
                        contentDescription = "Close",
                        onClick = {},
                        variant = IconButtonVariant.FILLED,
                        shape = IconButtonShape.CIRCLE
                    )

                    SeeAllPill(
                        onClick = {}
                    )
                }
            }
        }
    }
}
