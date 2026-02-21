package ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// --------------------
// Light Theme
// --------------------
private val LightColorScheme = lightColorScheme(

    primary = Violet100,
    onPrimary = Light100,

    secondary = Blue100,
    onSecondary = Light100,

    tertiary = Green100,
    onTertiary = Light100,

    background = Light100,
    onBackground = Dark100,

    surface = Light80,
    onSurface = Dark100,

    surfaceVariant = Light60,
    onSurfaceVariant = Dark75,

    outline = Light40,
    outlineVariant = Light40,

    error = Red100,
    onError = Light100
)

// --------------------
// Dark Theme
// --------------------
private val DarkColorScheme = darkColorScheme(

    primary = Violet100,      // brand stays same
    onPrimary = Light100,

    secondary = Blue100,
    onSecondary = Light100,

    tertiary = Green100,
    onTertiary = Light100,

    background = Dark100,
    onBackground = Light100,

    surface = Dark75,
    onSurface = Light100,

    surfaceVariant = Dark50,
    onSurfaceVariant = Light60,

    outline = Dark50,
    outlineVariant = Dark25,

    error = Red100,
    onError = Light100
)

@Composable
fun MoneyTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 🔥 disabled for brand consistency
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
