package com.shambu.compose.scrollbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

/**
 * State for managing the scrollbar's properties and activity.
 * This state is responsible for tracking the scrollbar's position, length, and drag activity.
 *
 * @property isScrollbarDragActive Indicates if the scrollbar is currently being dragged.
 * @property barBounds The bounds of the scrollbar's track.
 * @property indicatorBounds The bounds of the scrollbar's indicator (thumb).
 * @property indicatorLength The current length of the indicator, based on content and scrollbar length.
 * @property scrollbarLength The total length of the scrollbar.
 * @property contentLength The total length of the scrollable content.
 * @property dragBounds The bounds for detecting dragging interactions with the scrollbar.
 *
 * Instance can be created using [rememberScrollbarState]
 * @See rememberScrollbarState
 */
class ScrollbarState internal constructor() {
    var isScrollbarDragActive by mutableStateOf(false)
        internal set

    var barBounds: Rect = Rect(Offset.Zero, Size.Zero)
        internal set
    var indicatorBounds: Rect = Rect(Offset.Zero, Size.Zero)
        internal set
    var indicatorLength: Float = 0f
        internal set
    var scrollbarLength: Float = 0f
        internal set
    var contentLength: Float = 0f
        internal set

    internal var isVertical: Boolean = true

    val dragBounds: Rect get() = Rect(
        top = barBounds.top - 16,
        bottom = barBounds.bottom + 16,
        left = barBounds.left - 16,
        right = barBounds.right + 16,
    )
}

/**
 * Create and [remember] the [ScrollbarState] for managing scrollbar interactions
 * and properties within a composable.
 *
 * @return An instance of [ScrollbarState] with remembered state.
 */
@Composable
fun rememberScrollbarState(): ScrollbarState = remember { ScrollbarState() }
