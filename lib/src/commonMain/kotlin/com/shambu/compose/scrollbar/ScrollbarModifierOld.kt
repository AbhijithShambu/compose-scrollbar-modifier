package com.shambu.compose.scrollbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.testTag
import com.shambu.compose.scrollbar.foundation.ScrollbarLayout
import com.shambu.compose.scrollbar.foundation.ScrollbarMeasureAndDraw
import com.shambu.compose.scrollbar.foundation.ScrollbarSemanticProperties
import com.shambu.compose.scrollbar.foundation.ScrollbarState
import kotlin.math.max

@Suppress("unused")
private fun Modifier.scrollbarOld(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    direction: Orientation,
    showAlways: Boolean = false,
    autoHideAnimationSpec: AnimationSpec<Float>? = null,
    isDragEnabled: Boolean = true,
    onMeasureAndDraw: ScrollbarMeasureAndDraw,
): Modifier =
    composed(inspectorInfo = {
        name = "scrollbar"
        testTag("scrollbar")
        properties[ScrollbarSemanticProperties.Keys.BAR_BOUNDS] = scrollbarState.barBounds
        properties[ScrollbarSemanticProperties.Keys.INDICATOR_BOUNDS] = scrollbarState.indicatorBounds
        properties[ScrollbarSemanticProperties.Keys.INDICATOR_OFFSET] = scrollbarState.indicatorOffset
        properties[ScrollbarSemanticProperties.Keys.DIRECTION] = direction
        properties[ScrollbarSemanticProperties.Keys.SHOW_ALWAYS] = showAlways
        properties[ScrollbarSemanticProperties.Keys.IS_DRAGGING] = scrollbarState.isScrollbarDragActive
        properties[ScrollbarSemanticProperties.Keys.IS_VISIBLE] = showAlways ||
            scrollState.isScrollInProgress ||
            scrollbarState.isScrollbarDragActive
    }) {
        val isScrollingOrPanning =
            scrollState.isScrollInProgress || scrollbarState.isScrollbarDragActive

        val isVertical = direction == Orientation.Vertical
        scrollbarState.isVertical = isVertical
        scrollbarState.scrollTo = scrollState::scrollTo
        scrollbarState.scrollBy = scrollState::scrollBy

        val alpha =
            if (showAlways) {
                1f
            } else if (isScrollingOrPanning) {
                1f
            } else {
                0f
            }
        val alphaAnimationSpec = autoHideAnimationSpec ?: tween(
            delayMillis = if (isScrollingOrPanning) 0 else 1500,
            durationMillis = if (isScrollingOrPanning) 150 else 500,
        )

        val scrollbarAlpha by animateFloatAsState(
            targetValue = alpha,
            animationSpec = alphaAnimationSpec,
        )

        drawWithContent {
            drawContent()

            val showScrollbar = isScrollingOrPanning || scrollbarAlpha > 0.0f

            // Draw scrollbar only if currently scrolling or if scroll animation is ongoing.
            if (showScrollbar) {
                val viewPortLength = if (isVertical) size.height else size.width
                val viewPortCrossAxisLength = if (isVertical) size.width else size.height
                val contentLength =
                    max(
                        viewPortLength + scrollState.maxValue,
                        // To prevent divide by zero error
                        0.001f,
                    )
                scrollbarState.contentLength = contentLength
                scrollbarState.viewPortLength = viewPortLength

                val layout = ScrollbarLayout(
                    layoutDirection = layoutDirection,
                    orientation = direction,
                    viewPortLength = viewPortLength,
                    viewPortCrossAxisLength = viewPortCrossAxisLength,
                    contentLength = contentLength,
                    contentOffset = scrollState.value,
                    scrollbarAlpha = scrollbarAlpha,
                    density = density,
                    fontScale = fontScale,
                )

                with(DefaultScrollbarLayoutScope(this, scrollbarState, density, fontScale)) {
                    onMeasureAndDraw(layout)
                }
            }
        }
    }.scrollbarDrag(scrollState, scrollbarState, direction, isDragEnabled)
