# Changelog

## 1.3.0

- Add a persistent in-app mode selector for Black Screen (AMOLED) and Screen Off / Lock.
- Save the selected mode and route the Quick Settings tile through it.
- Replace the Accessibility lock service with Android Device Admin's force-lock-only policy.
- Do not register an Accessibility service or request accessibility-event/window-content access.
- Request Device Admin approval only after the user selects Screen Off / Lock and taps Continue.
- Rename the Android system/launcher app label and in-app heading to **SofP**.
- Keep Screen Off / Lock available only on Android 9 (API 28)+.
- Add a bounded `PARTIAL_WAKE_LOCK` for explicit Screen Off / Lock actions; release it on screen-on or after a 2-hour safety timeout.
- Preserve the AMOLED overlay and double-tap restoration.
- Keep version at 1.3.0 (version code 4).

## 1.2.0

- Keep the AMOLED blackout overlay visible with FLAG_KEEP_SCREEN_ON.
- Prevent the normal screen timeout from automatically locking the phone while blackout mode is active.
- Keep the black overlay at minimum window brightness and preserve double-tap restoration.
- Clarify that this is black-screen mode, not physical display power-off.
- Clarify that media-app playback restrictions remain controlled by Android and the media app.
- Increment version to 1.2.0 (version code 3).

## 1.1.0

- Add a custom ScreenOffPlay launcher identity with an adaptive icon.
- Add a monochrome launcher layer for supported themed-icon launchers.
- Use the app's own icon for its Quick Settings tile.
- Increment version to 1.1.0 (version code 2).
