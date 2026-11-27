# Changelog

## 1.3.0

- Remove the unused window-state accessibility event subscription; the lock service requests no accessibility events and cannot retrieve window content.
- Reset stale lock-tile state when the accessibility service reconnects.
- Keep accessibility limited to the explicit Android lock-screen global action.

- Add a persistent in-app mode selector for Black Screen (AMOLED) and Screen Off / Lock.
- Save the selected mode and route the Quick Settings tile through it.
- Defer special-access setup until the user chooses to start the selected mode; do not request it during installation.
- Explain why the selected mode needs Display over other apps or Accessibility access.
- Allow users to change modes later; check only the selected mode's access when it is started.
- Keep Screen Off / Lock available only on Android 9 (API 28)+.
- Keep Accessibility window-content retrieval disabled.
- Add `WAKE_LOCK` and a bounded `PARTIAL_WAKE_LOCK` for explicit Screen Off / Lock actions; release it on screen-on, service teardown, or after a 2-hour safety timeout.\n- Document payment-app compatibility limitations of enabled Accessibility services; do not attempt to bypass app security checks.
- Preserve the AMOLED overlay and double-tap restoration.
- Increment version to 1.3.0 (version code 4).

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
