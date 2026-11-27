# Changelog

## 1.3.0

- Add an AccessibilityService using GLOBAL_ACTION_LOCK_SCREEN for device lock.
- Add a separate PARTIAL_WAKE_LOCK system-running control, bounded to 30 minutes and released when the screen turns on or the service stops.
- Route the Quick Settings tile through the new split system/display controller.
- Add an in-app Accessibility setup flow.
- Preserve the previous AMOLED overlay as an optional legacy mode.
- Document accessibility scope, privacy, battery impact, Android API requirements, and real-device verification gates.
- Increment the Android version to 1.3.0 (version code 4).

## 1.2.0

- Keep the AMOLED blackout overlay visible with FLAG_KEEP_SCREEN_ON.
- Prevent the normal screen timeout from automatically locking the phone while blackout mode is active.
- Keep the black overlay at minimum window brightness and preserve double-tap restoration.
- Update the in-app instructions and documentation to clarify that this mode does not physically power off the display.
- Clarify that YouTube and other media-app playback restrictions remain controlled by Android and the media app.
- Increment the Android version to 1.2.0 (version code 3).

## 1.1.0

- Add a custom ScreenOffPlay launcher identity with an adaptive icon.
- Add a monochrome launcher layer for supported themed-icon launchers.
- Use the app's own icon for its Quick Settings tile.
- Increment the Android version to 1.1.0 (version code 2).
