package com.shambu.compose.scrollbar

/**
 * A function that measures and draws the scrollbar based on the given layout configuration.
 *
 * It operates within a [ScrollbarLayoutScope] and takes a [ScrollbarLayout] parameter,
 * which provides the necessary layout information for positioning and sizing the scrollbar. It returns
 * a [ScrollbarMeasurementResult] that represents the measurement outcome, such as the dimensions and
 * positioning of the scrollbar.
 *
 * @receiver [ScrollbarLayoutScope] The scope within which the scrollbar measurement and drawing operation
 * is performed, giving access to layout and drawing utilities.
 * @param layout The [ScrollbarLayout] object containing layout details like viewport size, content offset,
 * and scrollbar properties that influence the measurement and drawing of the scrollbar.
 * @return A [ScrollbarMeasurementResult] object containing the calculated size, position, and other relevant
 * details of the scrollbar after measurement.
 */
typealias ScrollbarMeasureAndDraw = ScrollbarLayoutScope.(layout: ScrollbarLayout) -> ScrollbarMeasurementResult
