// Copyright (c) 2026 shyakdas

package ui.components.navigation.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens
import ui.theme.MoneyTrackTheme

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

@Preview(name = "Large Button – Light & Dark")
@Composable
private fun LargeButtonAllVariantsPreview() {
    Column {

        MoneyTrackTheme(darkTheme = false) {
            Column(
                modifier = Modifier
                    .background(AppTheme.colors.background)
                    .padding(Dimens.spacing16),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing12)
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

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        MoneyTrackTheme(darkTheme = true) {
            Column(
                modifier = Modifier
                    .background(AppTheme.colors.background)
                    .padding(Dimens.spacing16),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing12)
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
}
