package com.shambu.compose.scrollbar.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.shambu.compose.scrollbar.ColorType
import com.shambu.compose.scrollbar.ScrollbarConfig
import com.shambu.compose.scrollbar.rememberScrollbarState
import com.shambu.compose.scrollbar.verticalScrollWithScrollbar

@Suppress("ktlint:standard:argument-list-wrapping")
@Composable
fun VerticalScrollExample() {
    val scrollState = rememberScrollState()
    val scrollbarState = rememberScrollbarState()
    val gradient = Brush.verticalGradient(
        listOf(Color.Red, Color.Blue, Color.Green), 0.0f, 10000.0f, TileMode.Repeated,
    )
    Box(
        Modifier
            .verticalScrollWithScrollbar(
                scrollState,
                scrollbarState,
                scrollbarConfig = ScrollbarConfig(
                    indicatorThickness = 16.dp,
                    indicatorColor = ColorType.Solid(Color.Gray),
                ),
            ).fillMaxWidth()
            .requiredHeight(10000.dp)
            .background(brush = gradient),
    )
}
