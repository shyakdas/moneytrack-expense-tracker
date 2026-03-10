// Copyright (c) 2026 shyakdas

package ui.components.navigation.button

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

enum class IconButtonShape(val shape: Shape) {
    CIRCLE(CircleShape),
    ROUNDED_RECT(RoundedCornerShape(12.dp))
}
