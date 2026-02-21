package ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val primary: Color,
    val onPrimary: Color,

    val background: Color,
    val onBackground: Color,

    val surface: Color,
    val onSurface: Color,

    val surfaceVariant: Color,
    val outline: Color,

    val error: Color
)

val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("AppColors not provided")
}
