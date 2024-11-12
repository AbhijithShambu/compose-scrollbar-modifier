package com.shambu.compose.scrollbar.foundation

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Represents a color type.
 * This defines two possible types of colors:
 * - [ColorType.Solid]: A single, solid color.
 * - [ColorType.Gradient]: A gradient that blends between multiple colors for the given bounds.
 */
sealed interface ColorType {
    /**
     * Represents a solid color.
     *
     * @property color The solid color represented by a [Color] instance.
     */
    data class Solid(
        val color: Color,
    ) : ColorType

    /**
     * Represents a gradient color.
     *
     * @property brush A function that returns a [Brush] for the gradient, given the bounds defined by a [Rect].
     */
    class Gradient(
        brush: (bounds: Rect) -> Brush,
    ) : ColorType {
        val brush: (bounds: Rect) -> Brush = { bounds ->
            // To prevent crashes due to infinite bounds
            if (bounds.isFinite) {
                brush(bounds)
            } else {
                println("Could not load brush due to invalid bounds: $bounds")
                Brush.horizontalGradient(
                    0f to Color.Transparent,
                    0f to Color.Transparent,
                    startX = 0f,
                    endX = 1f,
                )
            }
        }
    }
}
