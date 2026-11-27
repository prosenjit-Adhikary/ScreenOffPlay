# Screen Off Play

A minimal Android display-mode utility with a Quick Settings tile. Users choose the behavior they prefer; the choice is saved locally and can be changed later.

## Choose your mode

### Black Screen (AMOLED)
- Displays a pure-black overlay at minimum window brightness and keeps the display awake.
- Double-tap the overlay to restore the screen.
- Does not lock the device or physically power off the display panel.
- Requires Android **Display over other apps** special access, requested only when the user starts this mode.

### Screen Off / Lock
- Uses Android's AccessibilityService global lock-screen action.
- Available on Android 9 (API 28)+.
- The user must explicitly enable Screen Off Play in Android Accessibility settings when starting this mode.
- The service does not subscribe to accessibility events and cannot retrieve or inspect window content.
- Accessibility is used only for the explicit Android lock-screen action.
- Android/OEM controls exact screen power-down timing. Background media playback is not guaranteed.

## Installation and first launch

1. Install the app. Installation does not request runtime or special app access.
2. Open Screen Off Play and choose **Black Screen (AMOLED)** or **Screen Off / Lock**. The default is Black Screen.
3. Tap **Continue with selected mode**.
4. Android Settings opens only if the selected mode needs access that has not yet been granted:
   - Black Screen: Display over other apps.
   - Screen Off / Lock: Accessibility service.
5. Return to the app and tap Continue again to start the selected mode.
6. Add the **Screen Off** Quick Settings tile if you want one-tap access. The tile follows the saved mode.
7. Reopen the app at any time to change modes. The newly selected mode's access is checked only when you start it.

Declining or not enabling one mode's access does not prevent installation or use of the other mode. The app does not silently enable special access. If a mode is not configured, the tile opens the app instead of silently changing behavior.

On Android 8 (API 26–27), Screen Off / Lock is unavailable and Black Screen is the only mode.

## Permissions and privacy

- No dangerous runtime permission is requested by the app.
- `SYSTEM_ALERT_WINDOW` is declared only because Android requires it for the optional Display over other apps special-access flow used by Black Screen. It is not an installer-time runtime permission and is not granted automatically.
- `BIND_ACCESSIBILITY_SERVICE` protects the service binding; the user explicitly enables the service in Android Settings. It is not granted during installation.
- `BIND_QUICK_SETTINGS_TILE` protects the Quick Settings tile.
- No Internet, storage, audio-recording, device-admin, or WAKE_LOCK permission is requested.
- Accessibility is used only to invoke Android's global lock-screen action; window-content retrieval is disabled.

Accessibility access is powerful. The app explains its purpose and requires the user to enable it explicitly.

## Build

Requirements: JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.2, Gradle 8.2.

Run:

    gradle assembleDebug

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. Release builds must be signed before distribution.

## Public release status

See [RELEASE-READINESS.md](RELEASE-READINESS.md) for the implementation/verification ledger, Android support range, physical-device test requirements, privacy audit, and release-signing requirements. CI validates source/build tasks, not OEM-specific behavior. Unsigned CI APKs are for validation only, not public distribution. The project is distributed under the MIT License.

## Development history

### v1.3.0 — Selectable display behavior
- Add persistent Black Screen (AMOLED) and Screen Off / Lock choices.
- Route the Quick Settings tile through the saved selection.
- Preserve the legacy AMOLED overlay.
- Keep Screen Off / Lock limited to Android 9+.

### v1.2.0 — Awake AMOLED blackout
- Keep the full-screen black overlay visible with minimum window brightness.
- Use FLAG_KEEP_SCREEN_ON while blackout mode is active.
- Preserve double-tap restoration and the Quick Settings tile.
