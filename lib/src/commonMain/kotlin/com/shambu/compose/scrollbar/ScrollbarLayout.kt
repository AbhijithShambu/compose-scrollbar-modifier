package com.shambu.compose.scrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max
import kotlin.math.min

/**
 * Layout configuration of the scrollable container, providing information on the dimensions,
 * orientation, and content offset.
 *
 * @property layoutDirection The layout direction of the container, specifying whether the content flows
 * left-to-right or right-to-left. See [LayoutDirection] for available options.
 *
 * @property orientation The orientation of the scrollable content, which can be either horizontal or vertical.
 * See [Orientation] for available options.
 *
 * @property viewPortLength The length of the viewport in the primary scroll direction (e.g., height for vertical
 * scrolling, width for horizontal). This represents the visible area of the content.
 *
 * @property viewPortCrossAxisLength The length of the viewport along the cross-axis (e.g., width for vertical
 * scrolling, height for horizontal). Useful for determining the scrollbar's cross-axis positioning.
 *
 * @property contentLength The total length of the scrollable content in the primary scroll direction, which
 * may exceed the viewport length if content is scrollable.
 *
 * @property contentOffset The current offset of the content from the start of the scrollable area, in pixels.
 * Represents how far the content has been scrolled in the primary direction.
 *
 * @property scrollbarAlpha The opacity of the scrollbar, where 0 is fully transparent and 1 is fully opaque.
 * Used to control the visibility of the scrollbar during interactions.
 *
 * @property density The screen density of the device, used for converting between dp and pixels.
 * Implemented from the [Density] interface.
 *
 * @property fontScale The scale factor for fonts, used to adjust text size based on user preferences.
 * Implemented from the [Density] interface.
 *
 * @see Density
 */
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
