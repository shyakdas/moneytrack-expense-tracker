package ui.components.form.input

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytrack.core.R
import ui.theme.NeutralC6

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
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = NeutralC6,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle,
                    color = NeutralC6
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                textStyle = textStyle.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = NeutralC6,
                modifier = Modifier
                    .size(20.dp)
                    .then(
                        if (onTrailingIconClick != null)
                            Modifier.clickable { onTrailingIconClick() }
                        else Modifier
                    )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun InputFieldPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Basic
            InputField(
                value = "",
                onValueChange = {},
                placeholder = "Input..."
            )

            // Trailing icon (password)
            InputField(
                value = "",
                onValueChange = {},
                placeholder = "Input...",
                trailingIcon = ImageVector.vectorResource(id = R.drawable.show),
            )

            // Leading icon
            InputField(
                value = "",
                onValueChange = {},
                placeholder = "Input",
                leadingIcon = ImageVector.vectorResource(id = R.drawable.show),
            )

            // Leading + trailing icon
            InputField(
                value = "",
                onValueChange = {},
                placeholder = "Input...",
                leadingIcon = ImageVector.vectorResource(id = R.drawable.show),
                trailingIcon = ImageVector.vectorResource(id = R.drawable.show),
            )

            // Attachment style
            InputField(
                value = "Add attachment",
                onValueChange = {},
                leadingIcon = ImageVector.vectorResource(id = R.drawable.attachment),
            )
        }
    }
}

