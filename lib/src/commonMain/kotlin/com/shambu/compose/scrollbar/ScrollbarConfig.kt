package com.shambu.compose.scrollbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration for customizing the appearance and behavior of a scrollbar.
 *
 * @property indicatorThickness Thickness of the scrollbar indicator (thumb).
 * @property indicatorColor Color of the scrollbar indicator.
 * @property indicatorCornerRadius Corner radius of the indicator.
 * @property minimumIndicatorLength Minimum length of the indicator.
 * @property barThickness Thickness of the scrollbar track. Default matches [indicatorThickness].
 * @property barColor Color of the scrollbar track.
 * @property barCornerRadius Corner radius of the track.
 * @property showAlways If true, the scrollbar is always visible. Default is false (auto-hide).
 * @property autoHideAnimationSpec Animation for auto-hiding the scrollbar. Default auto-hide animation if not specified.
 * @property padding Padding around the scrollbar.
 * @property isDragEnabled If true, the scrollbar is draggable. Default is true.
 */
data class ScrollbarConfig(
    val indicatorThickness: Dp = 8.dp,
    val indicatorColor: Color = Color.Gray.copy(alpha = 0.7f),
    val indicatorCornerRadius: Dp = indicatorThickness / 2,
    val minimumIndicatorLength: Dp = 24.dp,
    val barThickness: Dp = indicatorThickness,
    val barColor: Color = Color.LightGray.copy(alpha = 0.7f),
    val barCornerRadius: Dp = barThickness / 2,
    val showAlways: Boolean = false,
    val autoHideAnimationSpec: AnimationSpec<Float>? = null,
    val padding: PaddingValues = PaddingValues(all = 0.dp),
    val isDragEnabled: Boolean = true,
)
