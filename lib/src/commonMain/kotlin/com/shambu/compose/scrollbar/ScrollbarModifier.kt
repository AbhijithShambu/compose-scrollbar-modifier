package com.shambu.compose.scrollbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max

/**
 * Adds a scrollbar to the composable that is being modified.
 *
 * Recommended to use [verticalScrollWithScrollbar] or [horizontalScrollWithScrollbar]
 * or [scrollWithScrollbar] for smooth behaviour
 *
 * This modifier allows you to add a scrollbar to a scrollable composable, such as a
 * `Column` or `Row`. The scrollbar's appearance and behavior can be customized
 * using the `config` parameter.
 *
 * @param scrollState The scroll state that the scrollbar will be linked to.
 * @param scrollbarState The state of the scrollbar, including its position and visibility.
 * @param direction The orientation of the scrollbar (horizontal or vertical).
 * @param config The configuration for the scrollbar's appearance and behavior.
 *
 * @return A modifier that adds a scrollbar to the composable.
 *
 * @see ScrollState
 * @see ScrollbarState
 * @see Orientation
 * @see ScrollbarConfig
 */
fun Modifier.scrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    direction: Orientation,
    config: ScrollbarConfig = ScrollbarConfig(),
): Modifier =
    composed {
        var (
            indicatorThickness, indicatorColor, indicatorCornerRadius, minimumIndicatorLength,
            barThickness, barColor, barCornerRadius, showAlways, alphaAnimationSpec, padding,
        ) = config

        val isScrollingOrPanning =
            scrollState.isScrollInProgress || scrollbarState.isScrollbarDragActive

        val isVertical = direction == Orientation.Vertical
        scrollbarState.isVertical = isVertical

        val alpha =
            if (showAlways) {
                1f
            } else if (isScrollingOrPanning) {
                0.8f
            } else {
                0f
            }
        alphaAnimationSpec = alphaAnimationSpec ?: tween(
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
                val (topPadding, bottomPadding, startPadding, endPadding) =
                    arrayOf(
                        padding.calculateTopPadding().toPx(),
                        padding.calculateBottomPadding().toPx(),
                        padding.calculateStartPadding(layoutDirection).toPx(),
                        padding.calculateEndPadding(layoutDirection).toPx(),
                    )

                val isLtr = layoutDirection == LayoutDirection.Ltr
                val contentOffset = scrollState.value
                val viewPortLength = if (isVertical) size.height else size.width
                val viewPortCrossAxisLength = if (isVertical) size.width else size.height
                val contentLength =
                    max(
                        viewPortLength + scrollState.maxValue,
                        // To prevent divide by zero error
                        0.001f,
                    )
                scrollbarState.contentLength = contentLength

                // Scroll indicator measurements
                val scrollbarLength =
                    viewPortLength -
                        (if (isVertical) topPadding + bottomPadding else startPadding + endPadding)
                scrollbarState.scrollbarLength = scrollbarLength

                val indicatorThicknessPx = indicatorThickness.toPx()

                val indicatorLength =
                    max(
                        (scrollbarLength / contentLength) * viewPortLength,
                        minimumIndicatorLength.toPx(),
                    )
                scrollbarState.indicatorLength = indicatorLength

                val indicatorOffset = (scrollbarLength / contentLength) * contentOffset

                val scrollIndicatorSize =
                    if (isVertical) {
                        Size(indicatorThicknessPx, indicatorLength)
                    } else {
                        Size(indicatorLength, indicatorThicknessPx)
                    }

                val scrollIndicatorPosition =
                    if (isVertical) {
                        Offset(
                            x =
                                if (isLtr) {
                                    viewPortCrossAxisLength - indicatorThicknessPx - endPadding
                                } else {
                                    startPadding
                                },
                            y = indicatorOffset + topPadding,
                        )
                    } else {
                        Offset(
                            x =
                                if (isLtr) {
                                    indicatorOffset + startPadding
                                } else {
                                    viewPortLength - indicatorOffset - indicatorLength - endPadding
                                },
                            y = viewPortCrossAxisLength - indicatorThicknessPx - bottomPadding,
                        )
                    }

                scrollbarState.indicatorBounds = Rect(scrollIndicatorPosition, scrollIndicatorSize)

                // Scroll bar measurements
                val barThicknessPx = barThickness.toPx()

                val scrollbarPosition =
                    if (isVertical) {
                        Offset(
                            x = scrollIndicatorPosition.x + (indicatorThicknessPx - barThicknessPx) / 2,
                            y = topPadding,
                        )
                    } else {
                        Offset(
                            x = if (isLtr) startPadding else endPadding,
                            y = scrollIndicatorPosition.y + (indicatorThicknessPx - barThicknessPx) / 2,
                        )
                    }

                val scrollbarSize =
                    if (isVertical) {
                        Size(barThicknessPx, viewPortLength - topPadding - bottomPadding)
                    } else {
                        Size(viewPortLength - startPadding - endPadding, barThicknessPx)
                    }

                scrollbarState.barBounds = Rect(scrollbarPosition, scrollbarSize)

                // Draw bar
                if (barColor.alpha > 0) {
                    drawRoundRect(
                        color = barColor,
                        cornerRadius = CornerRadius(barCornerRadius.toPx(), barCornerRadius.toPx()),
                        topLeft = scrollbarPosition,
                        size = scrollbarSize,
                        alpha = scrollbarAlpha,
                    )
                }

                // Draw indicator
                drawRoundRect(
                    color = indicatorColor,
                    cornerRadius = indicatorCornerRadius.let { CornerRadius(it.toPx(), it.toPx()) },
                    topLeft = scrollIndicatorPosition,
                    size = scrollIndicatorSize,
                    alpha = scrollbarAlpha,
                )
            }
        }
    }.scrollbarDrag(scrollState, scrollbarState, direction, config.isDragEnabled)
