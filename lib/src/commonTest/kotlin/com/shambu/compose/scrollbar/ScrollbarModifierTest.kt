package com.shambu.compose.scrollbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.foundation.ScrollbarConfig
import com.shambu.compose.scrollbar.foundation.ScrollbarSemanticProperties
import com.shambu.compose.scrollbar.foundation.rememberScrollbarState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

class ScrollbarModifierTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun scrollWithScrollbar_existsAndOffsetIsZero_test() =
        runComposeUiTest {
            setContent {
                Box(
                    Modifier
                        .horizontalScrollWithScrollbar(
                            rememberScrollState(),
                            rememberScrollbarState(),
                        ).size(width = 10000.dp, height = 200.dp),
                )
            }

            // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
            val node = onNodeWithTag("scrollbar")
            node.assertExists()
            val indicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]
            println("Expected: 0, Actual: $indicatorOffset")
            assertTrue { indicatorOffset == 0f }
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun scrollWithScrollbar_scrollContentAndIndicatorMoves50Percent_test() =
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
                        .testTag("scrollBox")
                        .width(contentSize),
                )
            }

            // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
            val node = onNodeWithTag("scrollbar")

            node.assertExists()
            mainClock.advanceTimeBy(500)

            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val maxIndicatorOffset = barBounds.width - indicatorBounds.width
            val actualIndicatorOffset = indicatorBounds.topLeft.x - barBounds.topLeft.x
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove

            println("Expected: $expectedIndicatorOffset, Actual: $actualIndicatorOffset")
            assertTrue { expectedIndicatorOffset.roundToInt() == actualIndicatorOffset.roundToInt() }
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun scrollWithScrollbar_dragIndicatorAndContentMoves50Percent_test() =
        runComposeUiTest {
            val containerWidth = 200.dp
            val contentSize = 1000.dp
            val fractionOffsetToMove = 0.5f
            setContent {
                val scrollState = rememberScrollState(0)
                val scrollbarState = rememberScrollbarState()
                LaunchedEffect(true) {
                    delay(1000)
                    println("state in effect : $scrollbarState")
                    val indicatorOffset = (scrollbarState.barLength - scrollbarState.indicatorLength) * fractionOffsetToMove
                    scrollbarState.dragTo(indicatorOffset)
                }
                Box(
                    Modifier
                        .size(containerWidth)
                        .horizontalScrollWithScrollbar(scrollState, scrollbarState)
                        .testTag("scrollBox")
                        .width(contentSize),
                )
            }

            // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
            val node = onNodeWithTag("scrollbar")

            node.assertExists()
            mainClock.advanceTimeBy(1500)

            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val maxIndicatorOffset = barBounds.width - indicatorBounds.width
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove
            val actualIndicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]

            println("Expected: $expectedIndicatorOffset, Actual: $actualIndicatorOffset")
            assertTrue { expectedIndicatorOffset.roundToInt() == actualIndicatorOffset.roundToInt() }
        }

    @OptIn(ExperimentalTestApi::class)
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
                        .testTag("scrollBox")
                        .width(contentSize),
                )
            }

            // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
            val node = onNodeWithTag("scrollbar")

            node.assertExists()

            val fraction = containerWidth / contentSize
            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]

            val expectedIndicatorWidth = barBounds.width * fraction

            println("Expected: $expectedIndicatorWidth, Actual: ${indicatorBounds.width}")
            assertTrue { expectedIndicatorWidth.roundToInt() == indicatorBounds.width.roundToInt() }
        }

    @OptIn(ExperimentalTestApi::class)
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
                    println("state in effect : $scrollbarState")
                    val indicatorOffset = (scrollbarState.barLength - scrollbarState.indicatorLength) * fractionOffsetToMove
                    scrollbarState.dragTo(indicatorOffset)
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
                            ),
                        ).testTag("scrollBox")
                        .width(contentSize),
                )
            }

            // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
            val node = onNodeWithTag("scrollbar")

            node.assertExists()
            mainClock.advanceTimeBy(1500)

            val indicatorBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorBounds]
            val barBounds = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.BarBounds]
            val maxIndicatorOffset = barBounds.width - indicatorBounds.width
            val expectedIndicatorOffset = maxIndicatorOffset * fractionOffsetToMove
            val actualIndicatorOffset = node.fetchSemanticsNode().config[ScrollbarSemanticProperties.IndicatorOffset]

            println("Expected: $expectedIndicatorOffset, Actual: $actualIndicatorOffset")
            assertTrue { expectedIndicatorOffset.roundToInt() == actualIndicatorOffset.roundToInt() }
        }
}
