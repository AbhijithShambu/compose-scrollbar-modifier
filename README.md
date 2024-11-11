# Compose Scrollbar Modifier

A Compose multiplatform library for adding customizable scrollbars to scrollable composables.

## Table of Contents

1. [Features](#features)
2. [Installation](#installation)
3. [Basic Usage](#basic-usage)
   - [Horizontal Scrollbar](#horizontal-scrollbar)
   - [Vertical Scrollbar](#vertical-scrollbar)
4. [Customization Options](#customization-options)
5. [Advanced Usage](#advanced-usage)
7. [Troubleshooting](#troubleshooting)
8. [Contributing](#contributing)

## Features

- Horizontal and Vertical scroll support
- Customizable scrollbar appearance and behavior
- Draggable scrollbar indicator
- Auto-hide scrollbar option
- Multiplatform support (Android, iOS, Desktop)

## Installation

To use the Compose Scrollbar Modifier library in your project, add the following dependency to your `build.gradle` file:

```gradle
// To be published soon ;)
```

## Basic Usage

### Horizontal Scrollbar

To add horizontal scrolling with a scrollbar, use the `horizontalScrollWithScrollbar` function:

```kotlin
@Composable
fun HorizontalScrollExample() {
    val scrollState = rememberScrollState()
    val scrollbarState = rememberScrollbarState()

    Row(
        modifier = Modifier
            .horizontalScrollWithScrollbar(scrollState, scrollbarState)
    ) {
        // Your content here
    }
}
```

### Vertical Scrollbar

Similarly, to add horizontal scrolling with a scrollbar, use the `horizontalScrollWithScrollbar` function:

```kotlin
@Composable
fun VerticalScrollExample() {
    val scrollState = rememberScrollState()
    val scrollbarState = rememberScrollbarState()

    Column(
        modifier = Modifier
            .verticalScrollWithScrollbar(scrollState, scrollbarState)
    ) {
        // Your content here
    }
}
```
## Customization Options

The `ScrollbarConfig` class provides various options to customize the scrollbar appearance and behavior:


### Available Configuration Options

- `indicatorThickness`: Thickness of the scrollbar indicator (thumb).
- `indicatorCornerRadius`: Corner radius of the indicator.
- `indicatorColor`: The color of the scrollbar indicator. Can be a solid color or a gradient defined by ColorType.
- `indicatorBorder`: Border styling for the indicator.
- `minimumIndicatorLength`: Minimum length of the scrollbar indicator.
- `maximumIndicatorLength`: Maximum length of the scrollbar indicator. Prevents it from  growing too large on short content. Default is [Dp.Infinity], allowing the indicator to resize as needed.
- `barThickness`: Thickness of the scrollbar track.
- `barCornerRadius`: Corner radius of the track.
- `barColor`: Color of the scrollbar track.  Can be a solid color or a gradient defined by ColorType.
- `barBorder`: Border styling for the bar.
- `showAlways`: Whether the scrollbar should always be visible. 
     * If true, the scrollbar is always visible, even when not actively scrolling.
     * Default is false, meaning the scrollbar will auto-hide when not in use.
- `autoHideAnimationSpec`: Animation specification for auto-hiding the scrollbar. 
     *  If null, a default auto-hide animation is used. Defines timing and easing for fading out the scrollbar.
- `padding`: Padding around the scrollbar.
- `indicatorPadding`: Padding around the indicator.
- `isDragEnabled`: Determines if the scrollbar indicator is draggable. Default is true

### Usage Example

```kotlin 
@Composable 
fun CustomizedScrollbarExample() {
    val scrollState = rememberScrollState()
    val scrollbarState = rememberScrollbarState()

    val dynamicIndicatorThickness = 
        if (scrollbarState.isScrollbarDragActive) 16.dp else 20.dp

    val scrollbarConfig = 
        ScrollbarConfig(
            indicatorThickness = dynamicIndicatorThickness,
            indicatorColor = ColorType.Solid(Color.Red),
            indicatorBorder = BorderStyle(
                color = ColorType.Gradient { bounds ->
                    Brush.linearGradient(
                        0f to Color(0xFFEDE6F2),
                        1f to Color(0xFF8C4843),
                        start = bounds.topLeft,
                        end = bounds.bottomRight
                    )
                },
                width = 2.dp
            ),
            barThickness = 16.dp,
            barColor = ColorType.Solid(Color.White),
            showAlways = false,
            autoHideAnimationSpec = tween(300),
            padding = PaddingValues(all = 8.dp),
            indicatorPadding = PaddingValues(end = 4.dp),
            isDragEnabled = true
        )
            

    Box(
        modifier = Modifier
            .verticalScrollWithScrollbar(
                scrollState,
                scrollbarState,
                scrollbarConfig = scrollbarConfig,
            )
            .fillMaxWidth()
            .requiredHeight(10000.dp)
    ) {
        // Your content here
    }
}
```

These customization options allow you to fine-tune the appearance and behavior of your scrollbars to match your app's design and requirements.


## Advanced Usage
To custom draw scrollbar, 

```kotlin
@Composable
fun CustomScrollbarExample() {
    val scrollState = rememberScrollState()
    val scrollbarState = rememberScrollbarState()

    Row(
        modifier = Modifier
            .horizontalScrollWithScrollbar(
               scrollState, scrollbarState,
               onDraw = { measurements ->
                  // Draw scrollbar based on measurements provided
               }
            )
    ) {
        // Your content here
    }
}
```

To make both custom measurements and drawing,

```kotlin
@Composable
fun CustomScrollbarExample() {
    val scrollState = rememberScrollState()
    val scrollbarState = rememberScrollbarState()

    Row(
        modifier = Modifier
            .horizontalScrollWithScrollbar(
               scrollState, scrollbarState,
               onMeasureAndDrawScrollbar = { layout ->
                  // Make measurements using layout and config
                  val barBounds = ...
                  val indicatorBounds = ...
                  val measurements = ScrollbarMeasurements(barBounds, indicatorBounds, layout.scrollbarAlpha)

                  drawWithMeasurements(measurements) {
                     // DrawScope to draw scrollbar based on measurements provided
                  }
               }
            )
    ) {
        // Your content here
    }
}
```

## Usage Images
### Images from Sample App

<div style="display: flex; gap: 28px;">
    <img src="readme_images/Simulator%20Screenshot%20-%20iPhone%2016%20-%202024-11-11%20at%2002.30.28.png" alt="Simulator Screenshot" width="250" height="auto">
    <img src="readme_images/Simulator%20Screenshot%20-%20iPhone%2016%20-%202024-11-11%20at%2002.32.00.png" alt="Simulator Screenshot" width="250" height="auto">
</div>


## Troubleshooting

If you encounter any issues with the scrollbar not appearing or behaving as expected:

1. Ensure that you've properly initialized both `scrollState` and `scrollbarState`.
2. Check that the content inside the scrollable container has sufficient size to trigger scrolling.
3. Verify that there are no conflicting modifiers affecting the scrollability of the content.
4. Make sure the `ScrollbarConfig` is properly applied if you're using custom settings.

## Contributing

Contributions to the project are welcome! Please feel free to submit pull requests or open issues on our GitHub repository.
