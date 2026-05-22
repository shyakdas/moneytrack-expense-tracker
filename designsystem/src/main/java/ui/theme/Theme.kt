// Copyright (c) 2026 shyakdas

package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// --------------------
// Material Light Scheme (INTERNAL)
// --------------------
internal val LightColorScheme = lightColorScheme(
    primary = Teal100,
    onPrimary = Light100,
    primaryContainer = Teal20,
    onPrimaryContainer = Teal90,

    secondary = Green100,
    onSecondary = Light100,
    secondaryContainer = Green20,
    onSecondaryContainer = Green100,

    tertiary = Blue100,
    onTertiary = Light100,
    tertiaryContainer = Blue20,
    onTertiaryContainer = Blue100,

    background = Light80,
    onBackground = Slate900,

    surface = Light100,
    onSurface = Slate900,

    surfaceVariant = Light60,
    onSurfaceVariant = Slate500,

    outline = Light40,
    outlineVariant = Light40,

    error = Red100,
    onError = Light100,
    errorContainer = Red20,
    onErrorContainer = Red100,
)

// --------------------
// Material Dark Scheme (INTERNAL)
// --------------------
internal val DarkColorScheme = darkColorScheme(
    primary = Teal40,
    onPrimary = Dark100,
    primaryContainer = Teal90,
    onPrimaryContainer = Teal20,

    secondary = Green60,
    onSecondary = Dark100,
    secondaryContainer = Green100,
    onSecondaryContainer = Green20,

    tertiary = Blue60,
    onTertiary = Dark100,
    tertiaryContainer = Blue100,
    onTertiaryContainer = Blue20,

    background = Dark100,
    onBackground = Color(0xFFE6EDF2),

    surface = Dark75,
    onSurface = Color(0xFFE6EDF2),

    surfaceVariant = Dark50,
    onSurfaceVariant = Color(0xFF9FB0BC),

    outline = Dark25,
    outlineVariant = Dark25,

    error = Red60,
    onError = Dark100,
    errorContainer = Color(0xFF7A271A),
    onErrorContainer = Red20,
)


@Composable
fun MoneyTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) {
        AppColors(
            primary = Teal40,
            onPrimary = Dark100,
            primaryContainer = Teal90,
            onPrimaryContainer = Teal20,
            background = Dark100,
            onBackground = Color(0xFFE6EDF2),
            surface = Dark75,
            onSurface = Color(0xFFE6EDF2),
            surfaceVariant = Dark50,
            onSurfaceVariant = Color(0xFF9FB0BC),
            outline = Dark25,
            success = Green60,
            onSuccess = Dark100,
            successContainer = Color(0xFF064E3B),
            warning = Yellow60,
            onWarning = Dark100,
            warningContainer = Color(0xFF78350F),
            error = Red60,
        )
    } else {
        AppColors(
            primary = Teal100,
            onPrimary = Light100,
            primaryContainer = Teal20,
            onPrimaryContainer = Teal90,
            background = Light80,
            onBackground = Slate900,
            surface = Light100,
            onSurface = Slate900,
            surfaceVariant = Light60,
            onSurfaceVariant = Slate500,
            outline = Light40,
            success = Green100,
            onSuccess = Light100,
            successContainer = Green20,
            warning = Yellow100,
            onWarning = Slate900,
            warningContainer = Yellow20,
            error = Red100,
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
