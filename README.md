# Screen Off Play

A minimal Android blackout overlay with a Quick Settings tile.

## What it does

- Shows a full-screen black overlay above the current app.
- Keeps the display awake at very low brightness so the current app can remain foregrounded.
- Tap the Screen Off Quick Settings tile to toggle blackout mode.
- Double-tap the black overlay to restore the display.

**Important:** This is a blackout/dimming workaround, not a true hardware screen-off feature. The display remains powered. It cannot guarantee YouTube background playback or bypass YouTube Premium restrictions. Actual background playback is controlled by the media app and Android.

## Permissions

The only app permission requested is SYSTEM_ALERT_WINDOW (Display over other apps), granted manually in Android Settings. The Quick Settings tile is protected by Android's system BIND_QUICK_SETTINGS_TILE permission. The app does not request Internet, audio, storage, notification, accessibility, or battery-optimization permissions.

## Build

Requirements: JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.2, Gradle 8.2.

Run:

    gradle assembleDebug

The debug APK is generated at app/build/outputs/apk/debug/app-debug.apk.

Release builds enable R8 code shrinking and resource shrinking. Release APKs must be signed before distribution.
