package com.example.groupprojectapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/*
    App colour roles:
    - primary: buttons
    - secondaryContainer: left pane, selected filter chips
    - tertiaryContainer: top bars
    - outline: border and grid lines
    - error: overdue tasks, today line
    Status bar colours (Done / In progress / To do) live in Color.kt and
    don't change between light and dark mode.
 */

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,

    secondary = Periwinkle,
    onSecondary = Navy,
    secondaryContainer = Periwinkle,
    onSecondaryContainer = Navy,

    tertiary = LightBlue,
    onTertiary = Navy,
    tertiaryContainer = LightBlue,
    onTertiaryContainer = Navy,

    background = OffWhite,
    onBackground = Navy,
    surface = OffWhite,
    onSurface = Navy,
    onSurfaceVariant = Navy,

    // bottom sheet and other raised surfaces
    surfaceContainerLow = OffWhite,
    surfaceContainer = OffWhite,
    surfaceContainerHigh = OffWhite,

    outline = Navy,
    outlineVariant = Periwinkle,

    error = Red,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = Navy,

    secondary = Periwinkle,
    onSecondary = Navy,
    secondaryContainer = DarkPeriwinkle,
    onSecondaryContainer = OffWhite,

    tertiary = LightBlue,
    onTertiary = Navy,
    // tertiaryContainer = DarkBlue,
    tertiaryContainer = Periwinkle,
    onTertiaryContainer = OffWhite,

    background = DarkNavy,
    onBackground = OffWhite,
    surface = DarkNavy,
    onSurface = OffWhite,
    onSurfaceVariant = OffWhite,

    // bottom sheet and other raised surfaces: slightly lighter than the background
    surfaceContainerLow = DarkBlue,
    surfaceContainer = DarkBlue,
    surfaceContainerHigh = DarkBlue,

    outline = Periwinkle,
    outlineVariant = Periwinkle,

    // same red as light mode, so it never matches the To Do coral (#ff8a8a)
    error = Red,
    onError = Color.White
)

@Composable
fun GroupProjectAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Off so the app always uses its own palette instead of the wallpaper colours (Android 12+)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
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