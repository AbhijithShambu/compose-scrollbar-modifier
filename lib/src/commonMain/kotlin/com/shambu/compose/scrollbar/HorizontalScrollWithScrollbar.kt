package com.shambu.compose.scrollbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.Modifier


fun Modifier.horizontalScrollWithScrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
): Modifier = this
    .scrollbar(
        scrollState = scrollState,
        scrollbarState = scrollbarState,
        direction = Orientation.Horizontal,
        config = scrollbarConfig,
        isDragEnabled = enabled
    )
    .horizontalScroll(
        state = scrollState,
        enabled = enabled && !scrollbarState.isScrollbarDragActive,
        flingBehavior = flingBehavior,
        reverseScrolling = reverseScrolling
    )
