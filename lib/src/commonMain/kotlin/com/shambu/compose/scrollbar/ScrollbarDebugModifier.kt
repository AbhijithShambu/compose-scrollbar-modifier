package com.shambu.compose.scrollbar

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.foundation.ScrollbarState

// Only for Debugging
private const val IS_DEBUG_MODE = false

@Suppress("unused")
internal fun Modifier.scrollbarDebugger(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    debugState: ScrollbarDebugState,
): Modifier =
    composed {
        val isGestureOngoing = remember { ValueHolder(false) }

        LaunchedEffect(scrollState.isScrollInProgress, scrollbarState.isScrollbarDragActive) {
            if (!scrollbarState.isScrollbarDragActive && !scrollState.isScrollInProgress) {
                debugState.touchPosition = null
            }
        }

        LaunchedEffect(debugState.touchPosition) {
            if (!isGestureOngoing.value && debugState.touchPosition != null) {
                isGestureOngoing.value = true
            }

            if (debugState.touchPosition == null) {
                isGestureOngoing.value = false
            }
        }
        drawWithCache {
            onDrawWithContent {
                drawContent()

                // Added for debugging
                // Draw indicator
                drawRoundRect(
                    color = Color.Black,
                    cornerRadius = CornerRadius.Zero,
                    topLeft = scrollbarState.dragBounds.topLeft,
                    size = scrollbarState.dragBounds.size,
                    alpha = 1f,
                    style = Stroke(1.5f),
                )

                if (debugState.touchPosition != null) {
                    drawCircle(
                        Color.Black,
                        10.dp.toPx(),
                        center = debugState.touchPosition!!,
                    )
                }
            }
        }
    }.takeIf { IS_DEBUG_MODE } ?: Modifier

internal class ScrollbarDebugState {
    var touchPosition by mutableStateOf<Offset?>(null)
}

internal class ValueHolder<T>(
    var value: T,
)
