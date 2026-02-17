package ui.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ButtonGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        content()
    }
}

@Preview(
    name = "Button Group",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ButtonGroupPreview() {
    MaterialTheme {
        ButtonGroup(
            modifier = Modifier.padding(16.dp)
        ) {
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
    }
}
