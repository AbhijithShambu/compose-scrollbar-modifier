package com.shambu.compose.scrollbar.foundation

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density

/**
 * A scope, which allows to report the [ScrollbarMeasurements] and
 * further draw the Scrollbar with the measured values inside the [DrawScope].
 */
interface ScrollbarLayoutScope : Density {
    fun drawWithMeasurements(
        measurements: ScrollbarMeasurements,
        drawScrollbarAndIndicator: DrawScope.() -> Unit,
    ): ScrollbarMeasurementResult
}

class ScrollbarMeasurementResult internal constructor()
