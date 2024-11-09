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
 * @property indicatorCornerRadius Corner radius of the indicator.
 * @property indicatorColor Color of the scrollbar indicator.
 * @property indicatorBorder Border for the indicator. See [BorderStyle] for details.
 * @property minimumIndicatorLength Minimum length of the indicator.
 * @property maximumIndicatorLength Maximum length of the indicator allowed.
 * @property barThickness Thickness of the scrollbar track. Default matches [indicatorThickness].
 * @property barCornerRadius Corner radius of the track.
 * @property barColor Color of the scrollbar track.
 * @property barBorder Border for the bar. See [BorderStyle] for details.
 * @property showAlways If true, the scrollbar is always visible. Default is false (auto-hide).
 * @property autoHideAnimationSpec Animation for auto-hiding the scrollbar. Default auto-hide animation if not specified.
 * @property padding Padding around the scrollbar.
 * @property indicatorPadding Padding around the indicator.
 * @property isDragEnabled If true, the scrollbar is draggable. Default is true.
 *
 * @see ColorType
 */
data class ScrollbarConfig(
    /**
     * Specifies the thickness of the scrollbar indicator (also known as the thumb),
     * which is the part of the scrollbar that moves to indicate the current scroll position.
     * Default is 8.dp.
     */
    val indicatorThickness: Dp = 8.dp,
    /**
     * Defines the corner radius of the scrollbar indicator, giving it a rounded
     * appearance if desired. By default, this is set to half the [indicatorThickness],
     * making it fully rounded if [indicatorThickness] is greater than 0.
     */
    val indicatorCornerRadius: Dp = indicatorThickness / 2,
    /**
     * The color of the scrollbar indicator. Can be a solid color or a gradient defined by
     * [ColorType]. By default, this is set to a semi-transparent dark gray.
     */
    val indicatorColor: ColorType = ColorType.Solid(Color.DarkGray.copy(alpha = 0.75f)),
    /**
     * The border styling for the scrollbar indicator, such as color and thickness.
     * See [BorderStyle] for details. Default is no border.
     */
    val indicatorBorder: BorderStyle = BorderStyle(ColorType.Solid(Color.Transparent), 0.dp),
    /**
     * Specifies the minimum length of the scrollbar indicator. This ensures the
     * indicator remains visible even when content is long. Default is 24.dp.
     */
    val minimumIndicatorLength: Dp = 24.dp,
    /**
     * Specifies the maximum length of the scrollbar indicator, preventing it from
     * growing too large on short content. Default is [Dp.Infinity], allowing the indicator to resize as needed.
     */
    val maximumIndicatorLength: Dp = Dp.Infinity,
    /**
     * Defines the thickness of the scrollbar track, which is the stationary part along which
     * the indicator moves. By default, this matches [indicatorThickness] for a consistent appearance.
     */
    val barThickness: Dp = indicatorThickness,
    /**
     * Specifies the corner radius of the scrollbar track, allowing rounded edges if desired.
     * Default is half of [barThickness], making it fully rounded if [barThickness] is greater than 0.
     */
    val barCornerRadius: Dp = barThickness / 2,
    /**
     * The color of the scrollbar track. Can be a solid color or a gradient, specified by [ColorType].
     * Default is a semi-transparent light gray.
     */
    val barColor: ColorType = ColorType.Solid(Color.LightGray.copy(alpha = 0.75f)),
    /**
     * The border styling for the scrollbar track, such as color and thickness.
     * See [BorderStyle] for details. Default is no border.
     */
    val barBorder: BorderStyle = BorderStyle(ColorType.Solid(Color.Transparent), 0.dp),
    /**
     * If true, the scrollbar is always visible, even when not actively scrolling.
     * Default is false, meaning the scrollbar will auto-hide when not in use.
     */
    val showAlways: Boolean = false,
    /**
     * The animation specification for auto-hiding the scrollbar.
     * If null, a default auto-hide animation is used. Defines timing and easing for fading out the scrollbar.
     */
    val autoHideAnimationSpec: AnimationSpec<Float>? = null,
    /**
     * Padding around the scrollbar as a whole. Allows adjusting spacing between the scrollbar and
     * surrounding UI elements. Default is 0.dp for no additional padding.
     */
    val padding: PaddingValues = PaddingValues(all = 0.dp),
    /**
     * Padding around the scrollbar indicator, providing space between the indicator
     * and the track edges. Default is 0.dp.
     */
    val indicatorPadding: PaddingValues = PaddingValues(all = 0.dp),
    /**
     * Determines if the scrollbar indicator is draggable, allowing users to interact
     * with it directly. Default is true.
     */
    val isDragEnabled: Boolean = true,
)
