package com.shambu.compose.scrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max
import kotlin.math.min

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
        maximumIndicatorLength: Float = Float.MAX_VALUE,
    ): Float =
        min(
            max(calculateStandardIndicatorLength(scrollbarLength), minimumIndicatorLength),
            maximumIndicatorLength,
        )

    private fun calculateStandardIndicatorLength(scrollbarLength: Float): Float = (scrollbarLength / contentLength) * viewPortLength

    fun calculateIndicatorOffset(
        scrollbarLength: Float,
        indicatorLength: Float,
    ): Float = contentOffset * (scrollbarLength - indicatorLength) / (contentLength - viewPortLength)
}
