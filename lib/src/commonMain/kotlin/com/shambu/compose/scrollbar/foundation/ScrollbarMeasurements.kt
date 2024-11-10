package com.shambu.compose.scrollbar.foundation

import androidx.compose.ui.geometry.Rect

data class ScrollbarMeasurements(
    val barBounds: Rect,
    val indicatorBounds: Rect,
    val alpha: Float,
)
