package com.shambu.compose.scrollbar.sample.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme =
    lightColors(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        error = md_theme_light_error,
        onError = md_theme_light_onError,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
    )

private val DarkColorScheme =
    darkColors(
        primary = violet_vibrant,
        onPrimary = Color.White,
        secondary = violet_300,
        onSecondary = white_light,
        error = md_theme_dark_error,
        onError = md_theme_dark_onError,
        background = violet_300,
        onBackground = white_light,
        surface = violet_700,
        onSurface = Color.White,
    )

@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colors = colorScheme,
        content = content,
        typography =
            MaterialTheme.typography.copy(
                subtitle1 = MaterialTheme.typography.subtitle1.copy(color = colorScheme.onBackground),
                subtitle2 = MaterialTheme.typography.subtitle1.copy(color = colorScheme.onBackground),
                caption = MaterialTheme.typography.caption.copy(color = colorScheme.onBackground),
            ),
        shapes =
            Shapes(
                small = RoundedCornerShape(10.dp),
                medium = RoundedCornerShape(14.dp),
                large = RoundedCornerShape(18.dp),
            ),
    )
}
