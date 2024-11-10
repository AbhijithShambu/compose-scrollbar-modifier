package com.shambu.compose.scrollbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.foundation.ColorType
import com.shambu.compose.scrollbar.foundation.ScrollbarConfig
import com.shambu.compose.scrollbar.foundation.rememberScrollbarState
import kotlin.test.Test

class ScrollbarModifierTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun horizontalScrollWithScrollbar_exists_test() =
        runComposeUiTest {
            setContent {
                Box(
                    Modifier
                        .horizontalScrollWithScrollbar(
                            rememberScrollState(),
                            rememberScrollbarState(),
                            scrollbarConfig = ScrollbarConfig(
                                indicatorThickness = 16.dp,
                                indicatorColor = ColorType.Solid(Color.Gray),
                            ),
                        ).size(width = 10000.dp, height = 200.dp),
                )
            }

            // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
            val node = onNodeWithTag("scrollbar")
            node.assertExists()
        }
}
