package com.shambu.compose.scrollbar

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density

interface ScrollbarLayoutScope : Density {
    fun drawWithMeasurements(
        measurements: ScrollbarMeasurements,
        drawScrollbarAndIndicator: DrawScope.() -> Unit,
    )
}
