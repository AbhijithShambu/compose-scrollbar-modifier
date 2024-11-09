package com.shambu.compose.scrollbar.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.ColorType
import com.shambu.compose.scrollbar.ScrollbarConfig
import com.shambu.compose.scrollbar.horizontalScrollWithScrollbar
import com.shambu.compose.scrollbar.rememberScrollbarState

@Suppress("ktlint:standard:argument-list-wrapping")
@Composable
fun HorizontalScrollWithScrollbarSample() {
    val scrollState = rememberScrollState()
    val scrollbarState = rememberScrollbarState()

    val gradient = Brush.horizontalGradient(
        listOf(Color.Red, Color.Blue, Color.Green), 0.0f, 10000.0f, TileMode.Repeated,
    )
    Box(
        Modifier
            .horizontalScrollWithScrollbar(
                scrollState,
                scrollbarState,
                scrollbarConfig = ScrollbarConfig(
                    indicatorThickness = 16.dp,
                    indicatorColor = ColorType.Solid(Color.Gray),
                ),
            ).size(width = 10000.dp, height = 200.dp)
            .background(brush = gradient),
    )
}
