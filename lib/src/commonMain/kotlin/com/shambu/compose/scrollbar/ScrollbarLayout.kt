package com.shambu.compose.scrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max

class ScrollbarLayout(
    val layoutDirection: LayoutDirection,
    val orientation: Orientation,
    val viewPortLength: Float,
    val viewPortCrossAxisLength: Float,
    val contentLength: Float,
    val contentOffset: Int,
    val scrollbarAlpha: Float,
    override val density: Float,
    override val fontScale: Float,
) : Density {
    private val isVertical get() = orientation == Orientation.Vertical

    fun calculateBarLength(
        topPadding: Float = 0f,
        startPadding: Float = 0f,
        bottomPadding: Float = 0f,
        endPadding: Float = 0f,
    ): Float {
        val barPadding =
            if (isVertical) {
                topPadding + bottomPadding
            } else {
                startPadding + endPadding
            }
        return viewPortLength - barPadding
    }

    fun calculateIndicatorLength(
        scrollbarLength: Float,
        minimumIndicatorLength: Float = 24f,
    ): Float =
        max(
            (scrollbarLength / contentLength) * viewPortLength,
            minimumIndicatorLength,
        )

    fun calculateIndicatorOffset(scrollbarLength: Float = viewPortLength): Float = (scrollbarLength / contentLength) * contentOffset
}
