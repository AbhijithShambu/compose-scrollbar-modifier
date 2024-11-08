package com.shambu.compose.scrollbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * ***(For internal use only)***
 *
 * Enables drag interaction with a scrollbar to control scrolling.
 *
 * This internal modifier is applied to the scrollbar's draggable area to enable
 * user interaction for controlling the scroll position. It links the scrollbar's
 * drag gestures to the provided [scrollState] and [scrollbarState], allowing
 * users to drag the scrollbar thumb to scroll the content.
 *
 * @param scrollState The [ScrollState] that controls the scrolling of the content.
 * @param scrollbarState The [ScrollbarState] that manages the scrollbar's position and visibility.
 * @param direction The orientation of the scrollbar (horizontal or vertical).
 * @param isDragEnabled Whether drag interaction with the scrollbar is enabled. If `false`,
 * the scrollbar will not respond to drag gestures. Defaults to `true`.
 *
 * @return A [Modifier] that enables drag interaction with the scrollbar.
 */
internal fun Modifier.scrollbarDrag(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    direction: Orientation,
    isDragEnabled: Boolean = true,
): Modifier =
    composed {
        val coroutineScope = rememberCoroutineScope()

        pointerInput(isDragEnabled) {
            if (isDragEnabled) {
                awaitEachGesture {
                    // Wait for the first down event (start of pan)
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val firstPosition = down.position

                    val mode = scrollbarState.dragBounds.contains(firstPosition)
                    scrollbarState.isScrollbarDragActive = mode

                    if (!scrollbarState.isScrollbarDragActive) return@awaitEachGesture

                    if (down.isConsumed) {
                        coroutineScope.launch { scrollState.stopScroll() }
                    }

                    // Handle the drag (pan) and track movements
                    val isVertical = direction == Orientation.Vertical
                    val firstPositionLength = if (isVertical) firstPosition.y else firstPosition.x
                    val barBoundsTopLeft = scrollbarState.dragBounds.topLeft
                    val startBoundsPosition = if (isVertical) barBoundsTopLeft.y else barBoundsTopLeft.x
                    val initialBarOffset =
                        firstPositionLength - scrollbarState.indicatorLength / 2 - startBoundsPosition

                    val getContentOffset = { barOffset: Float ->
                        barOffset * scrollbarState.contentLength / scrollbarState.barLength
                    }

                    val shouldScrollToInitialPosition =
                        !scrollbarState.indicatorBounds.contains(firstPosition)

                    val scrollToInitialPosition =
                        suspend {
                            // Scroll to initial position
                            val initialContentOffset = getContentOffset(initialBarOffset).roundToInt()
                            scrollState.scrollTo(initialContentOffset)
                        }

                    if (shouldScrollToInitialPosition) {
                        coroutineScope.launch {
                            if (down.isConsumed) {
                                delay(10)
                            }
                            scrollToInitialPosition()
                        }
                    }

                    var jumpToInitialPosition = shouldScrollToInitialPosition && down.isConsumed

                    do {
                        val event = awaitPointerEvent()

                        // Fallback
                        if (jumpToInitialPosition) {
                            jumpToInitialPosition = false
                            coroutineScope.launch { scrollToInitialPosition() }
                        }

                        val panChange = event.changes.firstOrNull()?.positionChange()

                        if (panChange != null) {
                            val updatedBarOffset = if (isVertical) panChange.y else panChange.x
                            coroutineScope.launch {
                                // Panning
                                scrollState.scrollBy(getContentOffset(updatedBarOffset))
                            }
                            event.changes.forEach { it.consume() } // Consume the change to avoid interference
                        }
                    } while (event.changes.any { it.pressed }) // Continue until the finger is lifted

                    // Pan gesture has ended when `pressed` is false
                    scrollbarState.isScrollbarDragActive = false
                }
            }
        }
    }
