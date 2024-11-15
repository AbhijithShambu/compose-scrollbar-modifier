package com.shambu.compose.scrollbar.foundation

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.DefaultMiter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

fun Rect.applyPadding(
    density: Density,
    paddingValues: PaddingValues,
    layoutDirection: LayoutDirection,
): Rect =
    with(density) {
        val rect = this@applyPadding
        Rect(
            left = rect.left + paddingValues.calculateLeftPadding(layoutDirection).toPx(),
            top = rect.top + paddingValues.calculateTopPadding().toPx(),
            right = rect.right - paddingValues.calculateRightPadding(layoutDirection).toPx(),
            bottom = rect.bottom - paddingValues.calculateBottomPadding().toPx(),
        )
    }

val ColorType.isTransparent get() = this is ColorType.Solid && color.alpha == 0f

internal fun BorderStyle.toStroke(density: Density) =
    with(density) {
        Stroke(
            width.toPx(),
            miter?.toPx() ?: DefaultMiter,
            cap,
            join,
            pathEffect,
        )
    }

internal fun DrawScope.drawRoundRect(
    paint: ColorType,
    topLeft: Offset = Offset.Zero,
    size: Size = Size.Zero,
    cornerRadius: CornerRadius = CornerRadius.Zero,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode,
) {
    when (paint) {
        is ColorType.Solid -> {
            drawRoundRect(
                paint.color,
                cornerRadius = cornerRadius,
                topLeft = topLeft,
                size = size,
                alpha = alpha,
                style = style,
                colorFilter = colorFilter,
                blendMode = blendMode,
            )
        }
        is ColorType.Gradient -> {
            drawRoundRect(
                paint.brush(Rect(topLeft, size)),
                cornerRadius = cornerRadius,
                topLeft = topLeft,
                size = size,
                alpha = alpha,
                style = style,
                colorFilter = colorFilter,
                blendMode = blendMode,
            )
        }
    }
}
