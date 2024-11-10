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
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
): Modifier =
    this then
        scrollbar(
            scrollState = scrollState,
            scrollbarState = scrollbarState,
            direction = direction,
            showAlways = config.showAlways,
            autoHideAnimationSpec = config.autoHideAnimationSpec,
            isDragEnabled = config.isDragEnabled,
        ) { layout ->
            val topPadding = config.padding.calculateTopPadding().toPx()
            val bottomPadding = config.padding.calculateBottomPadding().toPx()
            val startPadding = config.padding.calculateStartPadding(layout.layoutDirection).toPx()
            val endPadding = config.padding.calculateEndPadding(layout.layoutDirection).toPx()

            val isLtr = layout.layoutDirection == LayoutDirection.Ltr
            val isVertical = layout.orientation == Orientation.Vertical
            val barThicknessPx = config.barThickness.toPx()

            // Scroll indicator measurements
            val scrollbarLength =
                layout.calculateBarLength(topPadding, startPadding, bottomPadding, endPadding)

            val indicatorThicknessPx = config.indicatorThickness.toPx()

            val indicatorLength =
                layout.calculateIndicatorLength(
                    scrollbarLength,
                    config.minimumIndicatorLength.toPx(),
                    config.maximumIndicatorLength.toPx(),
                )

            val indicatorOffset = layout.calculateIndicatorOffset(scrollbarLength, indicatorLength)

            val scrollIndicatorSize =
                if (isVertical) {
                    Size(indicatorThicknessPx, indicatorLength)
                } else {
                    Size(indicatorLength, indicatorThicknessPx)
                }

            val scrollIndicatorPosition =
                if (isVertical) {
                    Offset(
                        x = (indicatorThicknessPx - barThicknessPx) / 2 +
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
                        y = layout.viewPortCrossAxisLength - indicatorThicknessPx - bottomPadding +
                            (indicatorThicknessPx - barThicknessPx) / 2,
                    )
                }

            // Scroll bar measurements
            val scrollbarPosition =
                if (isVertical) {
                    Offset(
                        x = layout.viewPortCrossAxisLength - barThicknessPx - endPadding,
                        y = topPadding,
                    )
                } else {
                    Offset(
                        x = if (isLtr) startPadding else endPadding,
                        y = layout.viewPortCrossAxisLength - barThicknessPx - bottomPadding,
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
                .applyPadding(this, config.indicatorPadding, layout.layoutDirection)

            val measurements = ScrollbarMeasurements(barBounds, indicatorBounds, layout.scrollbarAlpha)

            drawWithMeasurements(measurements) {
                onDraw(this, measurements)
            }
        }

fun DrawScope.drawDefaultScrollbar(
    measurements: ScrollbarMeasurements,
    config: ScrollbarConfig,
) = with(config) {
    val barColor = config.barColor
    val barBorderColor = config.barBorder.color
    val indicatorColor = config.indicatorColor
    val indicatorBorderColor = config.indicatorBorder.color

    // Draw bar
    if (!barColor.isTransparent) {
        val barCornerRadius = barCornerRadius.let { CornerRadius(it.toPx(), it.toPx()) }

        drawRoundRect(
            paint = barColor,
            cornerRadius = barCornerRadius,
            topLeft = measurements.barBounds.topLeft,
            size = measurements.barBounds.size,
            alpha = measurements.alpha,
        )

        if (barBorder.width > 0.dp && !barBorderColor.isTransparent) {
            val borderBounds = measurements.barBounds
            drawRoundRect(
                paint = barBorderColor,
                cornerRadius = barCornerRadius,
                topLeft = borderBounds.topLeft,
                size = borderBounds.size,
                alpha = measurements.alpha,
                style = barBorder.toStroke(this@drawDefaultScrollbar),
            )
        }
    }

    // Draw indicator
    val indicatorCornerRadius = indicatorCornerRadius.let { CornerRadius(it.toPx(), it.toPx()) }

    drawRoundRect(
        paint = indicatorColor,
        cornerRadius = indicatorCornerRadius,
        topLeft = measurements.indicatorBounds.topLeft,
        size = measurements.indicatorBounds.size,
        alpha = measurements.alpha,
    )

    if (indicatorBorder.width > 0.dp && !indicatorBorderColor.isTransparent) {
        val borderBounds = measurements.indicatorBounds
        drawRoundRect(
            paint = indicatorBorderColor,
            cornerRadius = indicatorCornerRadius,
            topLeft = borderBounds.topLeft,
            size = borderBounds.size,
            alpha = measurements.alpha,
            style = indicatorBorder.toStroke(this@drawDefaultScrollbar),
        )
    }
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
    onMeasureAndDraw: ScrollbarMeasureAndDraw,
): Modifier =
    this then
        composed(inspectorInfo = {
            name = "scrollbar"
            testTag("scrollbar")
            debugInspectorInfo {
                properties["barBounds"] = scrollbarState.barBounds
                properties["indicatorBounds"] = scrollbarState.indicatorBounds
                properties["indicatorOffset"] = scrollbarState.indicatorOffset
                properties["direction"] = direction
                properties["showAlways"] = showAlways
                properties["isDragEnabled"] = isDragEnabled
                properties["isDragging"] = scrollbarState.isScrollbarDragActive
                properties["state"] = scrollbarState
            }
        }) {
            val isScrollingOrPanning =
                scrollState.isScrollInProgress || scrollbarState.isScrollbarDragActive

            val isVertical = direction == Orientation.Vertical
            scrollbarState.isVertical = isVertical

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
    ): ScrollbarMeasurementResult {
        scrollbarState.indicatorBounds = measurements.indicatorBounds
        scrollbarState.barBounds = measurements.barBounds

        scrollbarState.indicatorOffset =
            if (scrollbarState.isVertical) {
                measurements.indicatorBounds.topLeft.y
            } else {
                measurements.indicatorBounds.topLeft.x
            }

        drawScrollbarAndIndicator(drawScope)

        return ScrollbarMeasurementResult()
    }
}
