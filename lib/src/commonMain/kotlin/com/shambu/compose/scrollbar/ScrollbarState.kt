package com.shambu.compose.scrollbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

class ScrollbarState internal constructor() {
    var isScrollbarDragActive by mutableStateOf(false)
        internal set

    var barBounds: Rect = Rect(Offset.Zero, Size.Zero)
        internal set
    var indicatorBounds: Rect = Rect(Offset.Zero, Size.Zero)
        internal set
    var indicatorLength: Float = 0f
        internal set
    var contentLength: Float = 0f
        internal set
    var scrollbarLength: Float = 0f
        internal set

    internal var isVertical: Boolean = true

    val dragBounds: Rect get() = Rect(
        top = barBounds.top - 16,
        bottom = barBounds.bottom + 16,
        left = barBounds.left - 16,
        right = barBounds.right + 16,
    )
}

@Composable
fun rememberScrollbarState(): ScrollbarState = remember { ScrollbarState() }
