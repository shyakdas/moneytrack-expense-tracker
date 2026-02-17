package ui.components.button

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
fun LargeButton(
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
        size = ButtonSize.LARGE,
        leadingIcon = leadingIcon,
        enabled = enabled
    )
}

@Preview(
    name = "Large Button – All Variants",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun LargeButtonAllVariantsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            LargeButton(
                text = "Primary",
                onClick = {},
                variant = ButtonVariant.PRIMARY
            )

            LargeButton(
                text = "Primary",
                onClick = {},
                variant = ButtonVariant.PRIMARY,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.add)
            )

            LargeButton(
                text = "Secondary",
                onClick = {},
                variant = ButtonVariant.SECONDARY
            )

            LargeButton(
                text = "Secondary",
                onClick = {},
                variant = ButtonVariant.SECONDARY,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.add)
            )

            LargeButton(
                text = "Sign Up with Google",
                onClick = {},
                variant = ButtonVariant.TERTIARY
            )

            LargeButton(
                text = "Sign Up with Google",
                onClick = {},
                variant = ButtonVariant.TERTIARY,
                leadingIcon = ImageVector.vectorResource(id = R.drawable.add)
            )
        }
    }
}

