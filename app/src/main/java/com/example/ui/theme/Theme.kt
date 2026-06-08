package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = VibrantIndigo400,
    secondary = VibrantIndigo500,
    tertiary = VibrantAmber500,
    background = VibrantBackgroundDark,
    surface = VibrantSurfaceDark,
    onPrimary = VibrantBackgroundDark,
    onSecondary = Color.White,
    onBackground = VibrantSlate50,
    onSurface = VibrantSlate100,
    error = VibrantRed500
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantIndigo600,
    secondary = VibrantIndigo500,
    tertiary = VibrantAmber500,
    background = VibrantBackgroundLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = VibrantSlate900,
    onSurface = VibrantSlate800,
    error = VibrantRed500,
    surfaceVariant = VibrantSlate50,
    onSurfaceVariant = VibrantSlate500
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic colors as we prioritize our handcrafted Vibrant Palette
    content: @Composable () -> Unit,
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
