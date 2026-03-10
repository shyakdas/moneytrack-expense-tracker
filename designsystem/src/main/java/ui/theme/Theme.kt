// Copyright (c) 2026 shyakdas

package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

// --------------------
// Material Light Scheme (INTERNAL)
// --------------------
internal val LightColorScheme = lightColorScheme(
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
// Material Dark Scheme (INTERNAL)
// --------------------
internal val DarkColorScheme = darkColorScheme(
    primary = Violet100,
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
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) {
        AppColors(
            primary = Violet100,
            onPrimary = Light100,
            background = Dark100,
            onBackground = Light100,
            surface = Dark75,
            onSurface = Light100,
            surfaceVariant = Dark50,
            onSurfaceVariant = Light60,
            outline = Dark25,
            error = Red100
        )
    } else {
        AppColors(
            primary = Violet100,
            onPrimary = Light100,
            background = Light100,
            onBackground = Dark100,
            surface = Light80,
            onSurface = Dark100,
            surfaceVariant = Light60,
            onSurfaceVariant = Dark75,
            outline = Light40,
            error = Red100
        )
    }

    val materialScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalAppColors provides appColors
    ) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = Typography,
            content = content
        )
    }
}
