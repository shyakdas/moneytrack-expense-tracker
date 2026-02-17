package ui.components.navigation.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R

@Composable
fun SmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    BaseButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        size = ButtonSize.SMALL,
        leadingIcon = leadingIcon,
        enabled = enabled
    )
}


@Preview(
    name = "Small Button – All Variants",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun SmallButtonAllVariantsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Primary
            SmallButton(
                text = "Primary",
                onClick = {},
                variant = ButtonVariant.PRIMARY
            )

            SmallButton(
                text = "Primary",
                onClick = {},
                variant = ButtonVariant.PRIMARY,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.add)
            )

            // Secondary
            SmallButton(
                text = "Secondary",
                onClick = {},
                variant = ButtonVariant.SECONDARY
            )

            SmallButton(
                text = "Secondary",
                onClick = {},
                variant = ButtonVariant.SECONDARY,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.add)
            )

            // Tertiary
            SmallButton(
                text = "Tertiary",
                onClick = {},
                variant = ButtonVariant.TERTIARY
            )

            SmallButton(
                text = "Tertiary",
                onClick = {},
                variant = ButtonVariant.TERTIARY,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.add)
            )
        }
    }
}
