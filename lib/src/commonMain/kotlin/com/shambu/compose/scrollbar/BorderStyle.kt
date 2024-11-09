package com.shambu.compose.scrollbar

import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.DefaultCap
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.DefaultJoin
import androidx.compose.ui.unit.Dp

data class BorderStyle(
    val color: ColorType,
    val width: Dp,
    val miter: Dp? = null,
    val cap: StrokeCap = DefaultCap,
    val join: StrokeJoin = DefaultJoin,
    val pathEffect: PathEffect? = null,
)
