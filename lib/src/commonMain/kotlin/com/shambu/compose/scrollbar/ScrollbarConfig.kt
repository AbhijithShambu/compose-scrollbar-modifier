package com.shambu.compose.scrollbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
