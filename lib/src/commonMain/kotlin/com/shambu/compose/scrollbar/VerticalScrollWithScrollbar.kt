package com.shambu.compose.scrollbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier

fun Modifier.verticalScrollWithScrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig()
) = this
    .scrollbar(
        scrollState = scrollState,
        scrollbarState = scrollbarState,
        direction = Orientation.Vertical,
        config = scrollbarConfig,
        isDragEnabled = enabled
    )
    .verticalScroll(
        state = scrollState,
        enabled = enabled && !scrollbarState.isScrollbarDragActive,
        flingBehavior = flingBehavior,
        reverseScrolling = reverseScrolling
    )