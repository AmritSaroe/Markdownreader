package com.markdownreader.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    error = md_theme_light_error,
    onError = md_theme_light_onError
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError
)

private val SepiaColorScheme = lightColorScheme(
    primary = md_theme_sepia_primary,
    onPrimary = md_theme_sepia_onPrimary,
    primaryContainer = md_theme_sepia_primaryContainer,
    onPrimaryContainer = md_theme_sepia_onPrimaryContainer,
    secondary = md_theme_sepia_secondary,
    onSecondary = md_theme_sepia_onSecondary,
    secondaryContainer = md_theme_sepia_secondaryContainer,
    onSecondaryContainer = md_theme_sepia_onSecondaryContainer,
    background = md_theme_sepia_background,
    onBackground = md_theme_sepia_onBackground,
    surface = md_theme_sepia_surface,
    onSurface = md_theme_sepia_onSurface,
    surfaceVariant = md_theme_sepia_surfaceVariant,
    onSurfaceVariant = md_theme_sepia_onSurfaceVariant,
    error = md_theme_sepia_error,
    onError = md_theme_sepia_onError
)

enum class ReadingTheme {
    SYSTEM, LIGHT, DARK, SEPIA
}

@Composable
fun MarkdownReaderTheme(
    readingTheme: ReadingTheme = ReadingTheme.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (readingTheme) {
        ReadingTheme.SYSTEM -> isSystemInDarkTheme()
        ReadingTheme.DARK -> true
        else -> false
    }

    val colorScheme = when {
        readingTheme == ReadingTheme.SEPIA -> SepiaColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            var context = view.context
            while (context is android.content.ContextWrapper) {
                if (context is Activity) break
                context = context.baseContext
            }
            val window = (context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primaryContainer.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
