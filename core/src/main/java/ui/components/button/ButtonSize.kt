package ui.components.button

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp
) {
    LARGE(
        height = 56.dp,
        horizontalPadding = 16.dp,
        iconSize = 20.dp
    ),
    SMALL(
        height = 40.dp,
        horizontalPadding = 12.dp,
        iconSize = 16.dp
    )
}
