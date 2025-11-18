package com.numina.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple500,
    secondary = Teal200,
    tertiary = Orange500,
    background = Gray900,
    surface = Gray800,
    onPrimary = Gray50,
    onSecondary = Gray900,
    onTertiary = Gray50,
    onBackground = Gray50,
    onSurface = Gray50
)

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
    secondary = Teal700,
    tertiary = Orange500,
    background = Gray50,
    surface = Gray50,
    onPrimary = Gray50,
    onSecondary = Gray50,
    onTertiary = Gray50,
    onBackground = Gray900,
    onSurface = Gray900
)

@Composable
fun NuminaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
