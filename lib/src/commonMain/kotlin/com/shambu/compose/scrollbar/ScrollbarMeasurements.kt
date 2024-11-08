package com.shambu.compose.scrollbar

import androidx.compose.ui.geometry.Rect

data class ScrollbarMeasurements(
    val barBounds: Rect,
    val indicatorBounds: Rect,
    val alpha: Float,
)
