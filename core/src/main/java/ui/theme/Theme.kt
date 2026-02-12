package ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// --------------------
// Light Theme
// --------------------
private val LightColorScheme = lightColorScheme(
    primary = Violet60,
    onPrimary = Light20,

    secondary = Blue60,
    onSecondary = Light20,

    tertiary = Green60,
    onTertiary = Light20,

    background = Light40,
    onBackground = Dark100,

    surface = Light20,
    onSurface = Dark100,

    error = Red60,
    onError = Light20
)

// --------------------
// Dark Theme
// --------------------
private val DarkColorScheme = darkColorScheme(
    primary = Violet40,
    onPrimary = Dark100,

    secondary = Blue40,
    onSecondary = Dark100,

    tertiary = Green40,
    onTertiary = Dark100,

    background = Dark100,
    onBackground = Light20,

    surface = Dark75,
    onSurface = Light20,

    error = Red40,
    onError = Dark100
)

@Composable
fun MoneyTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
