package com.shambu.compose.scrollbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier

/**
 * Configure touch scrolling and flinging for the UI element in a single [Orientation]
 * along with draggable scrollbar
 *
 * If you don't need to have fling or nested scroll support, but want to make component simply
 * draggable, consider using [draggable].
 *
 * @param scrollState [ScrollableState] state of the scrollable. Defines how scroll events will be
 * interpreted by the user land logic and contains useful information about on-going events.
 * @param scrollbarState state of the scrollbar
 * @param orientation orientation of the scrolling
 * @param enabled whether or not scrolling in enabled
 * @param reverseDirection reverse the direction of the scroll, so top to bottom scroll will
 * behave like bottom to top and left to right will behave like right to left.
 * @param flingBehavior logic describing fling behavior when drag has finished with velocity. If
 * `null`, default from [ScrollableDefaults.flingBehavior] will be used.
 * @param interactionSource [MutableInteractionSource] that will be used to emit
 * drag events when this scrollable is being dragged.
 * @param scrollbarConfig The configuration for the scrollbar's appearance and behavior.
 */
fun Modifier.scrollWithScrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    orientation: Orientation,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    flingBehavior: FlingBehavior? = null,
    interactionSource: MutableInteractionSource? = null,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig(),
): Modifier =
    this
        .scrollbar(scrollState, scrollbarState, orientation, scrollbarConfig)
        .scrollable(
            state = scrollState,
            orientation = orientation,
            enabled = enabled && !scrollbarState.isScrollbarDragActive,
            reverseDirection = reverseDirection,
            flingBehavior = flingBehavior,
            interactionSource = interactionSource,
        )
