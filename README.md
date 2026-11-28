# SofP

A minimal Android display-mode utility with a Quick Settings tile. The app label shown by Android is **SofP**. Users choose a display behavior; the choice is saved locally and can be changed later.

## Choose your mode

### Black Screen (AMOLED)
- Displays a pure-black overlay at minimum window brightness and keeps the display awake.
- Double-tap the overlay to restore the screen.
- Does not lock the device or physically power off the display panel.
- Requires Android **Display over other apps** special access, requested only when the user starts this mode.
- Close the overlay before opening banking or payment apps.

### Screen Off / Lock
- Uses Android Device Admin's **force-lock** policy to request a device lock.
- Available on Android 9 (API 28)+.
- Device Admin activation is requested only after the user chooses this mode and taps Continue.
- The admin receiver requests only force-lock; it does not request device wipe, password reset, or other administrative policies.
- SofP does **not** register an Accessibility service or request accessibility-event/window-content access.
- A bounded `PARTIAL_WAKE_LOCK` is acquired for an explicit lock request and released when the screen turns on or after the 2-hour safety timeout. Android, OEM power management, and the media app still control playback; background playback is not guaranteed.
- Device Admin approval is separate from Accessibility access. Payment-app compatibility still needs real-device testing; SofP does not bypass app security checks.

## Installation and first launch

1. Install the app. Installation does not request runtime or special app access.
2. Open SofP and choose **Black Screen (AMOLED)** or **Screen Off / Lock**. The default is Black Screen.
3. Tap **Continue with selected mode**.
4. Android Settings opens only if the selected mode needs access that has not yet been granted:
   - Black Screen: Display over other apps.
   - Screen Off / Lock: Android's Device Admin confirmation.
5. Return to the app after approving access and tap Continue again if needed.
6. Add the **Screen Off** Quick Settings tile if desired. The tile follows the saved mode.
7. Reopen SofP at any time to change modes.

Declining one mode's access does not prevent use of the other mode. SofP does not silently enable special access. If a mode is not configured, the tile opens the app instead of silently changing behavior.

On Android 8 (API 26–27), Screen Off / Lock is unavailable and Black Screen is the only mode.

## Permissions and privacy

- `SYSTEM_ALERT_WINDOW`: optional Display over other apps special access for Black Screen.
- `WAKE_LOCK`: bounded CPU wake lock after an explicit Screen Off / Lock action.
- `BIND_DEVICE_ADMIN`: protects the Device Admin receiver; activation is explicitly approved by the user.
- `BIND_QUICK_SETTINGS_TILE`: protects the Quick Settings tile.
- No Accessibility service, Internet, storage, audio-recording, or dangerous runtime permission is requested.
- Device Admin policy is limited to `force-lock`.

## Build

Requirements: JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.2, Gradle 8.2.

```sh
gradle assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. Release builds must be signed before distribution.

## Public release status

See [RELEASE-READINESS.md](RELEASE-READINESS.md) for implementation and verification status. CI validates source/build tasks, not OEM-specific behavior. Unsigned CI APKs are for validation only, not public distribution. The project is distributed under the MIT License.

## Development history

### v1.3.0 — Selectable display behavior
- Add persistent Black Screen (AMOLED) and Screen Off / Lock choices.
- Route the Quick Settings tile through the saved selection.
- Use Device Admin force-lock instead of an Accessibility service.
- Rename the Android system/launcher app label to SofP.
- Preserve the legacy AMOLED overlay and double-tap restoration.

### v1.2.0 — Awake AMOLED blackout
- Keep the full-screen black overlay visible with minimum window brightness.
- Use FLAG_KEEP_SCREEN_ON while blackout mode is active.
- Preserve double-tap restoration and the Quick Settings tile.
