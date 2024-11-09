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
 *
 * @see ColorType
 */
data class ScrollbarConfig(
    val indicatorThickness: Dp = 8.dp,
    val indicatorCornerRadius: Dp = indicatorThickness / 2,
    val indicatorColor: ColorType = ColorType.Solid(Color.DarkGray.copy(alpha = 0.75f)),
    val indicatorBorder: BorderStyle = BorderStyle(ColorType.Solid(Color.Transparent), 0.dp),
    val minimumIndicatorLength: Dp = 24.dp,
    val maximumIndicatorLength: Dp = Dp.Infinity,
    val barThickness: Dp = indicatorThickness,
    val barCornerRadius: Dp = barThickness / 2,
    val barColor: ColorType = ColorType.Solid(Color.LightGray.copy(alpha = 0.75f)),
    val barBorder: BorderStyle = BorderStyle(ColorType.Solid(Color.Transparent), 0.dp),
    val showAlways: Boolean = false,
    val autoHideAnimationSpec: AnimationSpec<Float>? = null,
    val padding: PaddingValues = PaddingValues(all = 0.dp),
    val indicatorPadding: PaddingValues = PaddingValues(all = 0.dp),
    val isDragEnabled: Boolean = true,
)
