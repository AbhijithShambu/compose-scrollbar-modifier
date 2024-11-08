# Compose Scrollbar Modifier

A Compose multiplatform library for adding customizable scrollbars to scrollable composables.

## Table of Contents

1. [Features](#features)
2. [Installation](#installation)
3. [Basic Usage](#basic-usage)
   - [Horizontal Scrollbar](#horizontal-scrollbar)
   - [Vertical Scrollbar](#vertical-scrollbar)
4. [Customization Options](#customization-options)
5. [Examples](#examples)
6. [Troubleshooting](#troubleshooting)
7. [Contributing](#contributing)
8. [License](#license)

## Features

- Horizontal and Vertical scroll support
- Customizable scrollbar appearance and behavior
- Draggable scrollbar indicator
- Auto-hide scrollbar option
- Multiplatform support (Android, iOS, Desktop)

## Installation

To use the Compose Scrollbar Modifier library in your project, add the following dependency to your `build.gradle` file:

```gradle
dependencies {
    implementation("com.shambu.compose.scrollbar:scrollbar-modifier:x.x.x")
}
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

```kotlin
val scrollbarConfig = ScrollbarConfig(
    indicatorThickness = 8.dp,
    indicatorColor = Color.Gray.copy(alpha = 0.7f),
    barThickness = 8.dp,
    barColor = Color.LightGray.copy(alpha = 0.7f),
    showAlways = true,
    autoHideAnimationSpec = null,
    padding = PaddingValues(all = 16.dp),
    isDragEnabled = true
)
```


### Customized Scrollbar Example
You can then pass this configuration when creating the scrollbar:

```kotlin
@Composable
fun CustomizedScrollbarExample() {
    val scrollState = rememberScrollState()
    val scrollbarState = rememberScrollbarState()

    val scrollbarConfig = ScrollbarConfig(
        indicatorThickness = 16.dp,
        indicatorColor = Color.Blue,
        barThickness = 12.dp,
        barColor = Color.LightBlue,
        showAlways = false,
        autoHideAnimationSpec = tween(300),
        padding = PaddingValues(all = 8.dp),
        isDragEnabled = true
    )

    Box(
        modifier = Modifier
            .verticalScrollWithScrollbar(
               scrollState, 
               scrollbarState, 
               scrollbarConfig = scrollbarConfig
            )
            .fillMaxWidth()
            .requiredHeight(10000.dp)
    ) {
        // Your content here
    }
}
```

## Usage GIFs

[Insert usage GIFs here to demonstrate the library's functionality in action]

### Horizontal Scroll GIF

[Insert horizontal scroll GIF]

### Vertical Scroll GIF

[Insert vertical scroll GIF]

### Customized Scrollbar GIF

[Insert customized scrollbar GIF]

## Troubleshooting

If you encounter any issues with the scrollbar not appearing or behaving as expected:

1. Ensure that you've properly initialized both `scrollState` and `scrollbarState`.
2. Check that the content inside the scrollable container has sufficient size to trigger scrolling.
3. Verify that there are no conflicting modifiers affecting the scrollability of the content.
4. Make sure the `ScrollbarConfig` is properly applied if you're using custom settings.

## Contributing

Contributions to the project are welcome! Please feel free to submit pull requests or open issues on our GitHub repository.

## License

[Insert license text here]
```

This improved documentation includes:

1. More detailed feature list, including multiplatform support.
2. Expanded customization options with explanations.
3. Additional example for customized scrollbar.
4. Placeholders for usage GIFs to demonstrate the library's functionality.
5. More comprehensive troubleshooting section.
