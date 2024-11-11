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
 * State for managing the scrollbar's properties and activity.
 * This state is responsible for tracking the scrollbar's position, length, and drag activity.
 *
 * @property indicatorOffset state to track the offset of the scrollbar's indicator (thumb).
 * @property isScrollbarDragActive state to indicate if the scrollbar is currently being dragged.
 * @property barBounds The bounds of the scrollbar's track.
 * @property indicatorBounds The bounds of the scrollbar's indicator (thumb).
 * @property indicatorLength The current length of the indicator, based on content and scrollbar length.
 * @property barLength The total length of the scrollbar.
 * @property contentLength The total length of the scrollable content.
 * @property dragBounds The bounds for detecting dragging interactions with the scrollbar.
 *
 * Instance can be created using [rememberScrollbarState]
 * @See rememberScrollbarState
 */
@Stable
class ScrollbarState internal constructor() {
    var indicatorOffset by mutableFloatStateOf(0f)
        internal set
    var isScrollbarDragActive by mutableStateOf(false)
        internal set

    var barBounds: Rect = Rect(Offset.Zero, Size.Zero)
        internal set
    var indicatorBounds: Rect = Rect(Offset.Zero, Size.Zero)
        internal set

    var contentLength: Float = 0f
        internal set
    var viewPortLength: Float = 0f
        internal set

    internal var isVertical: Boolean = true
    internal var scrollTo: suspend (value: Int) -> Float = { 0f }
    internal var scrollBy: suspend (value: Float) -> Float = { 0f }

    val indicatorLength: Float get() =
        if (isVertical) {
            indicatorBounds.height
        } else {
            indicatorBounds.width
        }

    val barLength: Float get() = if (isVertical) barBounds.height else barBounds.width

    val dragBounds: Rect get() = Rect(
        top = barBounds.top - 16,
        bottom = barBounds.bottom + 16,
        left = barBounds.left - 16,
        right = barBounds.right + 16,
    )

    suspend fun dragTo(indicatorOffset: Float): Float = scrollTo(getContentOffset(indicatorOffset).roundToInt())

    suspend fun dragBy(indicatorOffset: Float): Float = scrollBy(getContentOffset(indicatorOffset))

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
