# Screen Off Play

A minimal Android display-mode utility with a Quick Settings tile. Users can choose the behavior they prefer and change it later in the app.

## Choose your mode

Screen Off Play provides two separate modes. The selected mode is saved on the device and is also used by the Quick Settings tile. Open the app at any time to change the selection.

### Black Screen (AMOLED)

- Shows a pure-black overlay at minimum window brightness.
- Keeps the display awake; it does not lock the device or physically power off the panel.
- Double-tap the overlay to restore the screen.
- Requires the Android **Display over other apps** permission.
- On Android 8 (API 26–27), this is the only available mode.

### Screen Off / Lock

- Uses AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN to ask Android to lock the device.
- Available on Android 9 (API 28) or later and requires the user to enable Screen Off Play in Android Accessibility settings.
- Android/OEM controls the exact physical-panel power-down timing.
- No PARTIAL_WAKE_LOCK is used. Android retains normal CPU sleep, Doze, and background power management.
- Background media playback is not guaranteed and remains controlled by Android and the media app.

## Setup and use

1. Open Screen Off Play.
2. Select **Black Screen (AMOLED)** or **Screen Off / Lock**. The selection is saved automatically.
3. Grant the permission requested by the chosen mode: Display over other apps for Black Screen, or Accessibility for Screen Off / Lock.
4. Add the **Screen Off** tile to Quick Settings.
5. Start media playback if desired, then tap the tile. It follows the currently selected mode.
6. To change behavior later, reopen Screen Off Play and select the other mode.

If the selected mode is not configured, the tile opens the app so the required permission can be enabled.

## Permissions and privacy

- BIND_ACCESSIBILITY_SERVICE is enforced by Android for binding the service; the user explicitly enables it in Settings.
- SYSTEM_ALERT_WINDOW is used only by Black Screen mode.
- The Quick Settings tile is protected by Android's BIND_QUICK_SETTINGS_TILE permission.
- No WAKE_LOCK permission is requested.
- The accessibility service declares no window-content retrieval and does not inspect or collect screen content.
- No Internet, storage, audio-recording, or device-admin permission is requested.

Accessibility access is powerful. Users should enable this service only if they trust the app. It is used solely to invoke Android's global lock-screen action.

## Build

Requirements: JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.2, Gradle 8.2.

Run:

    gradle assembleDebug

The debug APK is generated at app/build/outputs/apk/debug/app-debug.apk. Release builds must be signed before distribution.

## Public release status

See RELEASE-READINESS.md for the implementation/verification ledger, supported Android API range, physical-device test requirements, privacy audit, and release-signing requirements. CI validates source/build tasks, not OEM-specific behavior. The unsigned CI APK is for validation only, not the signed public install package. The project is distributed under the MIT License.

## Development history

### v1.3.0 — Selectable display behavior

- Add a persistent in-app choice between Black Screen (AMOLED) and Screen Off / Lock.
- Let the Quick Settings tile follow the saved mode.
- Allow users to change the selected mode at any time in the app.
- Keep Screen Off / Lock limited to Android 9+ and retain Black Screen on Android 8.
- Do not acquire PARTIAL_WAKE_LOCK; leave CPU sleep and background scheduling to Android.

### v1.2.0 — Awake AMOLED blackout

- Keep the full-screen black overlay visible with minimum window brightness.
- Add FLAG_KEEP_SCREEN_ON so the normal screen timeout does not automatically lock the phone while blackout mode is active.
- Retain double-tap restoration and the Quick Settings tile.
