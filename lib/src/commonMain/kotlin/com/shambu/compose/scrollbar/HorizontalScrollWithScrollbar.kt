package com.shambu.compose.scrollbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Modify element to allow to scroll horizontally when width of the content is bigger than max
 * constraints allow. Also displays draggable scrollbar
 *
 * @sample com.shambu.compose.scrollbar.sample.HorizontalScrollWithScrollbarSample
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
 * will mean right, when `false`, 0 [ScrollState.value] will mean left
 * @param scrollbarConfig The configuration for the scrollbar's appearance and behavior.
 * @param onDrawScrollbar Optional param to define the custom drawing for Scrollbar.
 *
 * @See [ScrollbarConfig]
 *
 */
fun Modifier.horizontalScrollWithScrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    scrollbarConfig: ScrollbarConfig = ScrollbarConfig(),
    onDrawScrollbar: DrawScope.(measurements: ScrollbarMeasurements) -> Unit = { measurements ->
        drawDefaultScrollbar(measurements, scrollbarConfig)
    },
): Modifier =
    this
        .scrollbar(
            scrollState = scrollState,
            scrollbarState = scrollbarState,
            direction = Orientation.Horizontal,
            config = scrollbarConfig,
            onDraw = onDrawScrollbar,
        ).horizontalScroll(
            state = scrollState,
            enabled = enabled && !scrollbarState.isScrollbarDragActive,
            flingBehavior = flingBehavior,
            reverseScrolling = reverseScrolling,
        )

/**
 * Modify element to allow to scroll horizontally when width of the content is bigger than max
 * constraints allow. Also displays draggable scrollbar
 *
 * @sample com.shambu.compose.scrollbar.sample.HorizontalScrollWithScrollbarSample
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
 * will mean right, when `false`, 0 [ScrollState.value] will mean left
 * @param alwaysShowScrollbar If true, the scrollbar is always visible. Default is false (auto-hide).
 * @param autoHideScrollbarAnimationSpec Animation for auto-hiding the scrollbar. Default auto-hide animation if not specified.
 * @param enableScrollbarDrag If true, the scrollbar is draggable. Default is true.
 * @param onMeasureAndDrawScrollbar To measure and draw the scrollbar.
 */
fun Modifier.horizontalScrollWithScrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    alwaysShowScrollbar: Boolean = false,
    autoHideScrollbarAnimationSpec: AnimationSpec<Float>? = null,
    enableScrollbarDrag: Boolean = true,
    onMeasureAndDrawScrollbar: ScrollbarMeasureAndDraw,
) = this
    .scrollbar(
        scrollState = scrollState,
        scrollbarState = scrollbarState,
        direction = Orientation.Horizontal,
        showAlways = alwaysShowScrollbar,
        autoHideAnimationSpec = autoHideScrollbarAnimationSpec,
        isDragEnabled = enableScrollbarDrag,
        onMeasureAndDraw = onMeasureAndDrawScrollbar,
    ).horizontalScroll(
        state = scrollState,
        enabled = enabled && !scrollbarState.isScrollbarDragActive,
        flingBehavior = flingBehavior,
        reverseScrolling = reverseScrolling,
    )
