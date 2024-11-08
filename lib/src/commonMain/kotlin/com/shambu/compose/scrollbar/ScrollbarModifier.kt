package com.shambu.compose.scrollbar

import androidx.compose.animation.core.AnimationSpec
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max

/**
 * This modifier allows you to add a scrollbar to a scrollable composable.
 * The scrollbar's appearance and behavior can be customized using the `config` parameter.
 *
 * Recommended to use [verticalScrollWithScrollbar] or [horizontalScrollWithScrollbar]
 * for smooth behaviour. Use this modifier if customization is required.
 *
 *
 * @param scrollState The scroll state that the scrollbar will be linked to.
 * @param scrollbarState The state of the scrollbar, including its position and visibility.
 * @param direction The orientation of the scrollbar (horizontal or vertical).
 * @param config The configuration for the scrollbar's appearance and behavior.
 * @param onDraw Optional param to define the custom drawing for Scrollbar.
 *
 * @return A modifier that adds a scrollbar to the composable.
 *
 * @see ScrollState
 * @see ScrollbarState
 * @see Orientation
 * @see ScrollbarConfig
 * @see ScrollbarMeasurements
 */
fun Modifier.scrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    direction: Orientation,
    config: ScrollbarConfig = ScrollbarConfig(),
    onDraw: DrawScope.(measurements: ScrollbarMeasurements) -> Unit = { measurements ->
        drawDefaultScrollbar(measurements, config)
    },
): Modifier {
    val (
        indicatorThickness, _, _, minimumIndicatorLength,
        barThickness, _, _, showAlways, alphaAnimationSpec, padding,
    ) = config

    return scrollbar(
        scrollState = scrollState,
        scrollbarState = scrollbarState,
        direction = direction,
        showAlways = showAlways,
        autoHideAnimationSpec = alphaAnimationSpec,
        isDragEnabled = config.isDragEnabled,
    ) { layout ->
        val topPadding = padding.calculateTopPadding().toPx()
        val bottomPadding = padding.calculateBottomPadding().toPx()
        val startPadding = padding.calculateStartPadding(layout.layoutDirection).toPx()
        val endPadding = padding.calculateEndPadding(layout.layoutDirection).toPx()

        val isLtr = layout.layoutDirection == LayoutDirection.Ltr
        val isVertical = layout.orientation == Orientation.Vertical

        // Scroll indicator measurements
        val scrollbarLength =
            layout.calculateBarLength(topPadding, startPadding, bottomPadding, endPadding)

        val indicatorThicknessPx = indicatorThickness.toPx()

        val indicatorLength =
            layout.calculateIndicatorLength(
                scrollbarLength,
                minimumIndicatorLength.toPx(),
            )

        val indicatorOffset = layout.calculateIndicatorOffset(scrollbarLength)

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
                            layout.viewPortCrossAxisLength - indicatorThicknessPx - endPadding
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
                            layout.viewPortLength - indicatorOffset - indicatorLength - endPadding
                        },
                    y = layout.viewPortCrossAxisLength - indicatorThicknessPx - bottomPadding,
                )
            }

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
                Size(barThicknessPx, layout.viewPortLength - topPadding - bottomPadding)
            } else {
                Size(layout.viewPortLength - startPadding - endPadding, barThicknessPx)
            }

        val barBounds = Rect(scrollbarPosition, scrollbarSize)
        val indicatorBounds = Rect(scrollIndicatorPosition, scrollIndicatorSize)

        val measurements = ScrollbarMeasurements(barBounds, indicatorBounds, layout.scrollbarAlpha)

        drawWithMeasurements(measurements) {
            onDraw(this, measurements)
        }
    }
}

fun DrawScope.drawDefaultScrollbar(
    measurements: ScrollbarMeasurements,
    config: ScrollbarConfig,
) {
    val barColor = config.barColor
    val indicatorColor = config.indicatorColor
    val barCornerRadius = config.barCornerRadius
    val indicatorCornerRadius = config.indicatorCornerRadius

    // Draw bar
    if (barColor.alpha > 0) {
        drawRoundRect(
            color = barColor,
            cornerRadius = CornerRadius(barCornerRadius.toPx(), barCornerRadius.toPx()),
            topLeft = measurements.barBounds.topLeft,
            size = measurements.barBounds.size,
            alpha = measurements.alpha,
        )
    }

    // Draw indicator
    drawRoundRect(
        color = indicatorColor,
        cornerRadius = indicatorCornerRadius.let { CornerRadius(it.toPx(), it.toPx()) },
        topLeft = measurements.indicatorBounds.topLeft,
        size = measurements.indicatorBounds.size,
        alpha = measurements.alpha,
    )
}

/**
 * This modifier allows you to add a scrollbar to a scrollable composable.
 *
 * Recommended to use [verticalScrollWithScrollbar] or [horizontalScrollWithScrollbar]
 * for smooth behaviour. Use this modifier if customization is required.
 *
 * @param scrollState The scroll state that the scrollbar will be linked to.
 * @param scrollbarState The state of the scrollbar, including its position and visibility.
 * @param direction The orientation of the scrollbar (horizontal or vertical).
 * @param showAlways If true, the scrollbar is always visible. Default is false (auto-hide).
 * @param autoHideAnimationSpec Animation for auto-hiding the scrollbar. Default auto-hide animation if not specified.
 * @param isDragEnabled If true, the scrollbar is draggable. Default is true.
 * @param onMeasureAndDraw To measure and draw the scrollbar.
 *
 * @return A modifier that adds a scrollbar to the composable.
 *
 * @see ScrollState
 * @see ScrollbarState
 * @see Orientation
 * @see AnimationSpec
 * @see ScrollbarMeasurements
 * @see ScrollbarLayoutScope
 * @see ScrollbarLayout
 */
fun Modifier.scrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    direction: Orientation,
    showAlways: Boolean = false,
    autoHideAnimationSpec: AnimationSpec<Float>? = null,
    isDragEnabled: Boolean = true,
    onMeasureAndDraw: ScrollbarLayoutScope.(layout: ScrollbarLayout) -> Unit,
): Modifier =
    composed {
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

class DefaultScrollbarLayoutScope(
    private val drawScope: DrawScope,
    private val scrollbarState: ScrollbarState,
    override val density: Float,
    override val fontScale: Float,
) : ScrollbarLayoutScope {
    override fun drawWithMeasurements(
        measurements: ScrollbarMeasurements,
        drawScrollbarAndIndicator: DrawScope.() -> Unit,
    ) {
        scrollbarState.indicatorBounds = measurements.indicatorBounds
        scrollbarState.barBounds = measurements.barBounds

        scrollbarState.indicatorOffset =
            if (scrollbarState.isVertical) {
                measurements.indicatorBounds.topLeft.y
            } else {
                measurements.indicatorBounds.topLeft.x
            }

        drawScrollbarAndIndicator(drawScope)
    }
}
