# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

## [1.0.0] - 2026-05-XX

### Added
- Initial public release
- `verticalScrollWithScrollbar` and `horizontalScrollWithScrollbar` modifier functions
- Low-level `Modifier.scrollbar` for custom measurement and draw control
- `ScrollbarConfig` for full appearance customization (colors, thickness, corner radius, padding, borders, auto-hide animation, drag enable/disable)
- `ScrollbarState` with programmatic `dragTo` and `dragBy` control
- Draggable scrollbar indicator with gesture handling
- `LazyListState` support via the low-level `scrollbar` modifier
- Multiplatform: Android (minSdk 24), iOS (iosX64, iosArm64, iosSimulatorArm64), Desktop JVM
