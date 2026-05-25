# Moment Journal (随时记)

A local-first Android journal app that lets you capture every moment as it happens.

## Philosophy

Unlike a traditional one-entry-per-day diary, **Moment Journal** supports multiple entries per day. Combine text, images, video, and voice into a single record — each with precise timestamps, organized by date.

## Features

- **Calendar Browsing** — Monthly calendar view with dots marking recorded days; tap a date to see its timeline
- **Free-form Editor** — Insert text, images, video, and voice blocks freely; long-press drag to reorder
- **Media Capture** — Shoot/record instantly or pick from gallery/files
- **Tag System** — 6 preset tags + custom tags; assign at submission
- **Bubble Editor** — Pinch-to-resize, long-press drag to merge into rows, auto-arrange on release
- **5 Themes** — Cute (default), Tough, Sunshine, Abstract, Quirky
- **Internationalization** — Supports Chinese and English; follows system language

## Screenshots

| Home | Editor | Detail | Themes |
|------|--------|--------|--------|
| ![Home](screenshots/01-home-calendar.jpg) | ![Editor](screenshots/02-editor.jpg) | ![Detail](screenshots/03-detail.jpg) | ![Themes](screenshots/04-themes.jpg) |

## Download

[![Download APK](https://img.shields.io/badge/Download-APK_v1.0-FF8FA3)](https://github.com/hjm041226-ops/MomentJournal/raw/master/MomentJournal-v1.0-debug.apk)

> Click the button above to download the latest APK and install directly on your Android device.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Database | Room (SQLite) |
| Camera | CameraX |
| Audio | MediaRecorder |
| Image Loading | Coil |
| Navigation | Navigation Compose |

## Build

```bash
# Environment
export JAVA_HOME="<JDK path>"
export ANDROID_SDK_ROOT="<Android SDK path>"

# Build
./gradlew assembleDebug

# Install to device
./gradlew installDebug
```

Min SDK: Android 8.0 (API 26)  
Target SDK: Android 14 (API 34)

## License

MIT License
