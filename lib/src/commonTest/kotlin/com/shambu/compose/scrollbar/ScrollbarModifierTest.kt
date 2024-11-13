@file:OptIn(ExperimentalTestApi::class)

package com.shambu.compose.scrollbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isZero
import com.shambu.compose.scrollbar.foundation.BorderStyle
import com.shambu.compose.scrollbar.foundation.ColorType
import com.shambu.compose.scrollbar.foundation.ScrollbarConfig
import com.shambu.compose.scrollbar.foundation.ScrollbarMeasurements
import com.shambu.compose.scrollbar.foundation.ScrollbarSemanticProperties
import com.shambu.compose.scrollbar.foundation.rememberScrollbarState
import getPlatformName
import kotlinx.coroutines.delay
import kotlin.test.Test

class ScrollbarModifierTest {
    @Test
    fun scrollWithScrollbar_existsAndOffsetIsZero_test() =
        runComposeUiTest {
            setContent {
                Box(
                    Modifier
                        .verticalScrollWithScrollbar(
                            rememberScrollState(),
                            rememberScrollbarState(),
                        ).size(width = 200.dp, height = 1000.dp),
                )
            }

            val node = onNodeWithTag("scrollbar")
            node.assertExists()
            val indicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]
            assertThat(indicatorOffset).isZero()
        }

    @Test
    fun scrollWithScrollbar_contentIsSmallerThanViewport_test() =
        runComposeUiTest {
            setContent {
                Box(
                    Modifier
                        .size(200.dp)
                        .verticalScrollWithScrollbar(
                            rememberScrollState(),
                            rememberScrollbarState(),
                        ).size(width = 200.dp, height = 100.dp),
                )
            }

            val node = onNodeWithTag("scrollbar")
            node.assertExists()
            val indicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]
            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val isVisible = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IsVisible]

            assertThat(indicatorOffset).isZero()
            assertThat(indicatorBounds.height).isEqualTo(barBounds.height)
            assertThat(isVisible).isEqualTo(false)
        }

    @Test
    fun scrollWithScrollbar_horizontalScrollContentAndIndicatorMoves50Percent_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp
            val maxContentOffset = contentSize - containerWidth
            val fractionOffsetToMove = 0.5f
            setContent {
                val scrollState = rememberScrollState(
                    initial = with(density) {
                        (maxContentOffset * fractionOffsetToMove).roundToPx()
                    },
                )

                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(scrollState, rememberScrollbarState())
                        .width(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val maxIndicatorOffset = barBounds.width - indicatorBounds.width
            val actualIndicatorOffset = indicatorBounds.topLeft.x - barBounds.topLeft.x
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove

            assertThat(expectedIndicatorOffset).isEqualTo(actualIndicatorOffset)
        }

    @Test
    fun scrollWithScrollbar_verticalScrollContentAndIndicatorMoves50Percent_test() =
        runComposeUiTest {
            val containerHeight = 200.dp
            val contentSize = 1000.dp
            val maxContentOffset = contentSize - containerHeight
            val fractionOffsetToMove = 0.5f
            setContent {
                val scrollState = rememberScrollState(
                    initial = with(density) {
                        (maxContentOffset * fractionOffsetToMove).roundToPx()
                    },
                )

                Box(
                    Modifier
                        .size(containerHeight)
                        .verticalScrollWithScrollbar(scrollState, rememberScrollbarState())
                        .height(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val maxIndicatorOffset = barBounds.height - indicatorBounds.height
            val actualIndicatorOffset = indicatorBounds.topLeft.y - barBounds.topLeft.y
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove

            assertThat(expectedIndicatorOffset).isEqualTo(actualIndicatorOffset)
        }

    @Test
    fun scrollWithScrollbar_programaticallyDragIndicatorAndContentMoves50Percent_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp
            val fractionOffsetToMove = 0.5f
            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                LaunchedEffect(true) {
                    delay(1000)
                    val indicatorOffset = (scrollbarState.barLength - scrollbarState.indicatorLength) * fractionOffsetToMove
                    scrollbarState.dragTo(indicatorOffset)
                }
                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(scrollState, scrollbarState)
                        .width(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val maxIndicatorOffset = barBounds.width - indicatorBounds.width
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove
            val actualIndicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]

            assertThat(actualIndicatorOffset).isEqualTo(expectedIndicatorOffset)
        }

    @Test
    fun scrollWithScrollbar_dragIndicatorAndContentMoves50Percent_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp
            val fractionOffsetToMove = 0.5f
            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(scrollState, scrollbarState)
                        .width(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            var indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]

            node.performTouchInput {
                val x = barBounds.topLeft.x + barBounds.width * fractionOffsetToMove
                down(Offset(barBounds.topLeft.x, barBounds.topLeft.y + barBounds.height / 2))
                moveTo(Offset(x - indicatorBounds.width / 2, barBounds.topLeft.y + barBounds.height / 2))
            }

            val scrollbarState = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.State]
            println(scrollbarState)

            indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val maxIndicatorOffset = barBounds.width - indicatorBounds.width
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove
            val actualIndicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]

            println("Expected indicatorOffset: $expectedIndicatorOffset")
            assertThat(actualIndicatorOffset).isEqualTo(expectedIndicatorOffset)
        }

    @Test
    fun scrollWithScrollbar_dragIndicatorWhileContentIsScrolling_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp
            val fractionOffsetToMove = 0.5f
            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(scrollState, scrollbarState)
                        .width(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            var indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]

            node.performTouchInput {
                val x = barBounds.topLeft.x + barBounds.width * fractionOffsetToMove
                swipeLeft(x, x - 400.dp.toPx())
                advanceEventTime(100)
                down(Offset(barBounds.topLeft.x, barBounds.topLeft.y + barBounds.height / 2))
                moveTo(Offset(x - indicatorBounds.width / 2, barBounds.topLeft.y + barBounds.height / 2))
                up()
            }

            val scrollbarState = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.State]
            println(scrollbarState)

            indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val maxIndicatorOffset = barBounds.width - indicatorBounds.width
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove
            val actualIndicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]

            println("Expected indicatorOffset: $expectedIndicatorOffset")
            assertThat(actualIndicatorOffset).isEqualTo(expectedIndicatorOffset)
        }

    @Test
    fun scrollWithScrollbar_disableDragIndicator_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp

            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                var showAlways by remember { mutableStateOf(false) }

                LaunchedEffect(true) {
                    delay(200)
                    showAlways = true
                }

                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(
                            scrollState,
                            scrollbarState,
                            scrollbarConfig = ScrollbarConfig(
                                isDragEnabled = false,
                                showAlways = showAlways,
                            ),
                        ).width(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]

            node.performTouchInput {
                val x = barBounds.topLeft.x + barBounds.width * 0.5f
                down(Offset(barBounds.topLeft.x, barBounds.topLeft.y + barBounds.height / 2))
                moveTo(Offset(x - indicatorBounds.width / 2, barBounds.topLeft.y + barBounds.height / 2))
            }

            val actualIndicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]

            assertThat(actualIndicatorOffset).isEqualTo(0f)
        }

    @Test
    fun scrollWithScrollbar_dynamicIndicatorSizeCorrect_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp
            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(scrollState, scrollbarState)
                        .width(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val fraction = containerWidth / contentSize
            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]

            val expectedIndicatorWidth = barBounds.width * fraction

            assertThat(expectedIndicatorWidth).isEqualTo(indicatorBounds.width)
        }

    @Test
    fun scrollWithScrollbar_indicatorPositionedCorrectlyOnDragForFixedSize_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp
            val fractionOffsetToMove = 0.5f
            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                LaunchedEffect(true) {
                    delay(1000)
                    val indicatorOffset = (scrollbarState.barLength - scrollbarState.indicatorLength) * fractionOffsetToMove
                    scrollbarState.dragTo(indicatorOffset)
                }
                val gradient = ColorType.Gradient { bounds ->
                    Brush.linearGradient(
                        0f to Color(0xFFEDE6F2),
                        1f to Color(0xFF8C4843),
                        start = bounds.topLeft,
                        end = bounds.bottomRight,
                    )
                }
                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(
                            scrollState,
                            scrollbarState,
                            scrollbarConfig = ScrollbarConfig(
                                indicatorThickness = 32.dp,
                                minimumIndicatorLength = 32.dp,
                                maximumIndicatorLength = 32.dp,
                                barBorder = BorderStyle(ColorType.Solid(Color.Black), 1.dp),
                                indicatorBorder = BorderStyle(gradient, 1.dp),
                            ),
                        ).width(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val maxIndicatorOffset = barBounds.width - indicatorBounds.width
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove
            val actualIndicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]

            assertThat(actualIndicatorOffset).isEqualTo(expectedIndicatorOffset)
        }

    @Test
    fun scrollWithScrollbar_horizontalScrollbarCustomMeasurementIndicatorSizeCorrect_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp
            val indicatorLength = with(density) { 24.dp.toPx() }
            println("Platform: ${getPlatformName()}")
            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(
                            scrollState,
                            scrollbarState,
                            onMeasureAndDrawScrollbar = { layout ->
                                val scrollbarLength = layout.calculateBarLength()
                                val thickness = 24.dp.toPx()
                                val scrollbarBounds = Rect(Offset.Zero, Size(scrollbarLength, thickness))

                                val indicatorOffset = layout.calculateIndicatorOffset(scrollbarLength, indicatorLength)
                                val indicatorBounds = Rect(Offset(x = indicatorOffset, y = 0f), Size(indicatorLength, thickness))
                                val measurements = ScrollbarMeasurements(
                                    indicatorBounds = indicatorBounds,
                                    barBounds = scrollbarBounds,
                                    alpha = layout.scrollbarAlpha,
                                )
                                drawWithMeasurements(measurements) {
                                    drawDefaultScrollbar(measurements, ScrollbarConfig())
                                }
                            },
                        ).width(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")
            node.assertExists()

            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]

            assertThat(indicatorLength).isEqualTo(indicatorBounds.width)
        }

    @Test
    fun scrollWithScrollbar_verticalScrollbarCustomMeasurementIndicatorSizeCorrect_test() =
        runComposeUiTest {
            val containerHeight = 200.dp
            val contentSize = 1000.dp
            val indicatorLength = with(density) { 24.dp.toPx() }
            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                Box(
                    Modifier
                        .size(containerHeight)
                        .verticalScrollWithScrollbar(
                            scrollState,
                            scrollbarState,
                            onMeasureAndDrawScrollbar = { layout ->
                                val scrollbarLength = layout.calculateBarLength()
                                val thickness = 24.dp.toPx()
                                val scrollbarBounds = Rect(Offset.Zero, Size(thickness, scrollbarLength))

                                val indicatorOffset = layout.calculateIndicatorOffset(scrollbarLength, indicatorLength)
                                val indicatorBounds = Rect(Offset(x = 0f, y = indicatorOffset), Size(thickness, indicatorLength))
                                val measurements = ScrollbarMeasurements(
                                    indicatorBounds = indicatorBounds,
                                    barBounds = scrollbarBounds,
                                    alpha = layout.scrollbarAlpha,
                                )
                                drawWithMeasurements(measurements) {
                                    drawDefaultScrollbar(measurements, ScrollbarConfig())
                                }
                            },
                        ).height(contentSize),
                )
            }

            val node = onNodeWithTag("scrollbar")
            node.assertExists()

            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]

            assertThat(indicatorLength).isEqualTo(indicatorBounds.height)
        }
}
