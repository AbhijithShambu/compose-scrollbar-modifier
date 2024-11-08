package com.shambu.compose.scrollbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier

/**
 * Modify element to allow to scroll vertically when height of the content is bigger than max
 * constraints allow. Also displays draggable scrollbar
 *
 * @sample com.shambu.compose.scrollbar.sample.VerticalScrollExample
 *
 * In order to use this modifier, you need to create and own [ScrollState] and [ScrollbarState]
 * @see [rememberScrollState]
 * @see [rememberScrollbarState]
 *
 * @param scrollState state of the scroll
 * @param scrollbarState state of the scrollbar
 * @param enabled whether or not scrolling via touch input is enabled
 * @param flingBehavior logic describing fling behavior when drag has finished with velocity. If
 * `null`, default from [ScrollableDefaults.flingBehavior] will be used.
 * @param reverseScrolling reverse the direction of scrolling, when `true`, 0 [ScrollState.value]
 * will mean bottom, when `false`, 0 [ScrollState.value] will mean top
 * @param scrollbarConfig The configuration for the scrollbar's appearance and behavior.
 */
fun Modifier.verticalScrollWithScrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig(),
) = this
    .scrollbar(
        scrollState = scrollState,
        scrollbarState = scrollbarState,
        direction = Orientation.Vertical,
        config = scrollbarConfig,
    ).verticalScroll(
        state = scrollState,
        enabled = enabled && !scrollbarState.isScrollbarDragActive,
        flingBehavior = flingBehavior,
        reverseScrolling = reverseScrolling,
    )
