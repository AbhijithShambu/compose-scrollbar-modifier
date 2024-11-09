package com.shambu.compose.scrollbar

import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.DefaultCap
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.DefaultJoin
import androidx.compose.ui.unit.Dp

/**
 * BorderStyle provides information for drawing border
 *
 * @param color Configure the color of the border. Color can be solid color or gradient
 * @param width Configure the width of the stroke in Dp
 * @param miter Set the border stroke miter value. This is used to control the behavior of miter
 * joins when the joins angle is sharp. This value must be >= 0.dp
 * @param cap Return the paint's Cap, controlling how the start and end of stroked lines and paths
 * are treated. The default is [StrokeCap.Butt]
 * @param join Set's the treatment where lines and curve segments join on a stroked path. The
 * default is [StrokeJoin.Miter]
 * @param pathEffect Effect to apply to the stroke, null indicates a solid stroke line is to be
 * drawn
 *
 * @see [ColorType]
 * @see [StrokeCap]
 * @see [StrokeJoin]
 * @see [PathEffect]
 */
data class BorderStyle(
    val color: ColorType,
    /**
     *  To configure the width of the border in Dp
     */
    val width: Dp,
    /**
     * Set the border stroke miter value.
     */
    val miter: Dp? = null,
    /**
     * Styles to use for line endings.
     * See [StrokeCap].
     */
    val cap: StrokeCap = DefaultCap,
    /**
     * Styles to use for line joins.
     */
    val join: StrokeJoin = DefaultJoin,
    /**
     * Effect applied to the geometry of a drawing primitive. For example, this can be used
     * to draw shapes as a dashed or shaped pattern, or apply a treatment around line segment
     * intersections.
     * See [PathEffect]
     */
    val pathEffect: PathEffect? = null,
)
