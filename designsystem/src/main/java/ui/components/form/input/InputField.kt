// Copyright (c) 2026 shyakdas

package ui.components.form.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import com.moneytrack.designsystem.R
import ui.theme.AppTheme
import ui.theme.Dimens

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    textStyle: TextStyle = AppTheme.typography.bodyLarge
) {
    Row(
        modifier = modifier
            .height(Dimens.inputHeight)
            .fillMaxWidth()
            .background(
                color = AppTheme.colors.surface,
                shape = RoundedCornerShape(Dimens.radius16)
            )
            .border(
                width = Dimens.borderNormal,
                color = AppTheme.colors.outline,
                shape = RoundedCornerShape(Dimens.radius16)
            )
            .padding(horizontal = Dimens.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(Dimens.icon20)
            )
            Spacer(modifier = Modifier.width(Dimens.spacing12))
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle,
                    color = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.72f)
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                cursorBrush = SolidColor(AppTheme.colors.primary),
                textStyle = textStyle.copy(
                    color = AppTheme.colors.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier
                    .size(Dimens.icon20)
                    .then(
                        if (onTrailingIconClick != null)
                            Modifier.clickable { onTrailingIconClick() }
                        else Modifier
                    )
            )
        }
    }
}

@Preview(name = "Input Field – Light & Dark")
@Composable
private fun InputFieldPreview() {
    Column {

        ui.theme.MoneyTrackTheme(darkTheme = false) {
            InputPreviewContent()
        }

        Spacer(modifier = Modifier.height(Dimens.spacing16))

        ui.theme.MoneyTrackTheme(darkTheme = true) {
            InputPreviewContent()
        }
    }
}

@Composable
private fun InputPreviewContent() {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(Dimens.spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
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
            trailingIcon = ImageVector.vectorResource(id = R.drawable.show)
        )

        InputField(
            value = "",
            onValueChange = {},
            placeholder = "Input",
            leadingIcon = ImageVector.vectorResource(id = R.drawable.show)
        )

        InputField(
            value = "",
            onValueChange = {},
            placeholder = "Input...",
            leadingIcon = ImageVector.vectorResource(id = R.drawable.show),
            trailingIcon = ImageVector.vectorResource(id = R.drawable.show)
        )

        InputField(
            value = "Add attachment",
            onValueChange = {},
            leadingIcon = ImageVector.vectorResource(id = R.drawable.attachment)
        )
    }
}
