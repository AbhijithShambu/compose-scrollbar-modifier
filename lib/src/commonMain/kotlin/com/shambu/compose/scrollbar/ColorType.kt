package com.shambu.compose.scrollbar

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

sealed interface StaticColor

sealed interface ColorType {
    data class Solid(
        val color: Color,
    ) : ColorType,
        StaticColor

    data class Gradient(
        val brush: Brush,
    ) : ColorType,
        StaticColor

    data class Provider(
        val colorProvider: (rect: Rect) -> StaticColor,
    ) : ColorType
}
