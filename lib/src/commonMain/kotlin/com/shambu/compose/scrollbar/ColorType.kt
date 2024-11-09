package com.shambu.compose.scrollbar

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

sealed interface ColorType {
    data class Solid(
        val color: Color,
    ) : ColorType

    data class Gradient(
        val brush: (bounds: Rect) -> Brush,
    ) : ColorType
}
