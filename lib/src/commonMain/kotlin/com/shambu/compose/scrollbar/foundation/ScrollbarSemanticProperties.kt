package com.shambu.compose.scrollbar.foundation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.semantics.SemanticsPropertyKey

object ScrollbarSemanticProperties {
    object Keys {
        const val BAR_BOUNDS: String = "barBounds"
        const val INDICATOR_BOUNDS: String = "indicatorBounds"
        const val INDICATOR_OFFSET: String = "indicatorOffset"
        const val DIRECTION: String = "direction"
        const val SHOW_ALWAYS: String = "showAlways"
        const val IS_DRAG_ENABLED: String = "isDragEnabled"
        const val IS_DRAGGING: String = "isDragging"
        const val STATE: String = "state"
    }

    val State = SemanticsPropertyKey<ScrollbarState>(Keys.STATE)
    val BarBounds = SemanticsPropertyKey<Rect>(Keys.BAR_BOUNDS)
    val IndicatorBounds = SemanticsPropertyKey<Rect>(Keys.INDICATOR_BOUNDS)
    val IndicatorOffset = SemanticsPropertyKey<Float>(Keys.INDICATOR_OFFSET)
    val Direction = SemanticsPropertyKey<Orientation>(Keys.INDICATOR_OFFSET)
    val IsDragEnabled = SemanticsPropertyKey<Boolean>(Keys.IS_DRAG_ENABLED)
    val ShowAlways = SemanticsPropertyKey<Boolean>(Keys.SHOW_ALWAYS)
    val IsDragging = SemanticsPropertyKey<Boolean>(Keys.IS_DRAGGING)
}
