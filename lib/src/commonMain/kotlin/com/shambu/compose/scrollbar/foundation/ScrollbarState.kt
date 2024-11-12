package com.shambu.compose.scrollbar.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlin.math.roundToInt

/**
 * State for controlling and managing the scrollbar's properties and activity.
 * This state is responsible for tracking the scrollbar's position, length, and drag activity.
 *
 * @property indicatorOffset state to track the offset of the scrollbar's indicator (thumb).
 * @property isScrollbarDragActive state to indicate if the scrollbar is currently being dragged.
 * @property barBounds The bounds of the scrollbar's track.
 * @property indicatorBounds The bounds of the scrollbar's indicator (thumb).
 * @property indicatorLength The current length of the indicator, based on content and scrollbar length.
 * @property barLength The total length of the scrollbar.
 * @property contentLength The total length of the scrollable content.
 * @property viewPortLength The length of the viewport (visible area) of the scrollable container.
 * Used alongside `contentLength` to adjust the scrollbar indicator's size and position.
 * * @property dragBounds The bounds for detecting dragging interactions with the scrollbar.
 *
 * Instance can be created using [rememberScrollbarState]
 * @See rememberScrollbarState
 */
@Stable
class ScrollbarState internal constructor() {
    /**
     * indicatorOffset A mutable state representing the offset of the scrollbar's indicator (thumb).
     * The offset indicates the current position of the thumb along the scrollbar.
     */
    var indicatorOffset by mutableFloatStateOf(0f)
        internal set

    /**
     * isScrollbarDragActive is a state indicating whether the scrollbar is currently being dragged by the user.
     * This state can be used to alter the UI, such as highlighting the scrollbar when active.
     */
    var isScrollbarDragActive by mutableStateOf(false)
        internal set

    /**
     * barBounds Defines the rectangular bounds of the scrollbar's track.
     */
    var barBounds: Rect = Rect(Offset.Zero, Size.Zero)
        internal set

    /**
     * indicatorBounds Defines the rectangular bounds of the scrollbar's indicator (thumb).
     */
    var indicatorBounds: Rect = Rect(Offset.Zero, Size.Zero)
        internal set

    /**
     * contentLength is the total length of the scrollable content.
     */
    var contentLength: Float = 0f
        internal set

    /**
     * viewPortLength The length of the viewport (visible area) of the scrollable container.
     */
    var viewPortLength: Float = 0f
        internal set

    internal var isVertical: Boolean = true
    internal var scrollTo: suspend (value: Int) -> Float = { 0f }
    internal var scrollBy: suspend (value: Float) -> Float = { 0f }

    /**
     * Returns the length of the indicator (thumb) based on the scrollbar's orientation.
     */
    val indicatorLength: Float get() =
        if (isVertical) {
            indicatorBounds.height
        } else {
            indicatorBounds.width
        }

    /**
     * Returns the length of the scrollbar track based on its orientation.
     */
    val barLength: Float get() = if (isVertical) barBounds.height else barBounds.width

    /**
     * Returns the expanded bounds for detecting drag interactions on the scrollbar.
     */
    val dragBounds: Rect get() = Rect(
        top = barBounds.top - 16,
        bottom = barBounds.bottom + 16,
        left = barBounds.left - 16,
        right = barBounds.right + 16,
    )

    /**
     * Drag the scrollbar to a specified indicator offset.
     *
     * @param indicatorOffset The offset of the scrollbar's indicator, where the drag action should end.
     * This offset is transformed to a content offset and used to update the scroll position.
     * @return The amount of scroll applied in response to this drag.
     */
    suspend fun dragTo(indicatorOffset: Float): Float = scrollTo(getContentOffset(indicatorOffset).roundToInt())

    /**
     * Drag the scrollbar by a specified offset amount.
     *
     * @param indicatorOffset The amount by which the scrollbar's indicator should move.
     * The offset is transformed to a content offset and used to adjust the scroll position.
     * @return The amount of scroll applied in response to this drag.
     */
    suspend fun dragBy(indicatorOffset: Float): Float = scrollBy(getContentOffset(indicatorOffset))

    /**
     * Converts the indicator offset to a content offset, used to calculate the scrollable content position.
     *
     * @param indicatorOffset The offset of the scrollbar's indicator.
     * @return The corresponding offset within the scrollable content.
     */
    private fun getContentOffset(indicatorOffset: Float): Float {
        val barAndIndicatorLengthDiff = barLength - indicatorLength

        return if (barAndIndicatorLengthDiff > 0) {
            indicatorOffset * (contentLength - viewPortLength) / barAndIndicatorLengthDiff
        } else {
            0f
        }
    }

    override fun toString(): String =
        "ScrollbarState(" +
            "indicatorOffset=$indicatorOffset, " +
            "isScrollbarDragActive=$isScrollbarDragActive, " +
            "barBounds=$barBounds, " +
            "indicatorBounds=$indicatorBounds, " +
            "indicatorLength=$indicatorLength, " +
            "barLength=$barLength, " +
            "contentLength=$contentLength, " +
            "viewPortLength=$viewPortLength, " +
            "dragBounds=$dragBounds" +
            ")"
}

/**
 * Create and [remember] the [ScrollbarState] for managing scrollbar interactions
 * and properties within a composable.
 *
 * @return An instance of [ScrollbarState] with remembered state.
 */
@Composable
fun rememberScrollbarState(): ScrollbarState = remember { ScrollbarState() }
