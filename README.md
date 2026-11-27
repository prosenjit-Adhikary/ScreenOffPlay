# Screen Off Play

A minimal Android screen-lock and media-listening utility with a Quick Settings tile.

## v1.3.0 — Accessibility screen lock with separate system/display control

When the user taps the **Screen Off** Quick Settings tile, the app sends two independent Android control requests:

1. **System-running control:** acquires PowerManager.PARTIAL_WAKE_LOCK, keeping the CPU awake while allowing the display to turn off.
2. **Display/lock control:** invokes AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN to lock the device through Android's supported accessibility action.

The partial wake lock is automatically released when Android reports that the screen turns on, if the accessibility service is interrupted/unbound/destroyed, or after a 30-minute safety timeout. The timeout prevents an abandoned session from holding the CPU awake indefinitely. A partial wake lock can increase battery consumption.

The action locks the device; Android and the device manufacturer control the exact timing of panel power-down and lock-screen behavior. This is not a privileged direct panel-power command and behavior must be validated on physical devices. The new lock mode requires Android 9 (API 28) or later because GLOBAL_ACTION_LOCK_SCREEN is not available on earlier versions.

## Setup and use

1. Open Screen Off Play.
2. Tap **Enable Screen Off Play Accessibility**.
3. In Android Accessibility settings, enable the Screen Off Play service. The service does not retrieve window content or collect what is displayed.
4. Add the **Screen Off** tile to Quick Settings.
5. Start media playback in the app you want to use.
6. Tap the Screen Off tile. Screen Off Play acquires the bounded system wake lock and requests Android to lock the screen.
7. Wake and unlock the phone normally. The screen-on event releases the wake lock and ends the active session.

If the accessibility service is disabled, the tile opens the app so the permission can be enabled again.

## Legacy AMOLED blackout mode

The previous v1.2.0 overlay remains available from the app's **Start legacy AMOLED blackout** button. It displays a pure-black overlay at minimum window brightness and uses FLAG_KEEP_SCREEN_ON; it does not lock the device or physically power off the display. Double-tap the overlay to restore it. The overlay still requires **Display over other apps** permission.

## Media playback and battery notes

Android and each media app control background playback. Screen Off Play does not bypass YouTube or another app's background-playback restrictions, and continued playback is not guaranteed.

The partial wake lock intentionally prevents CPU suspend during the active lock session. This can use more battery than allowing the device to enter deep sleep. The 30-minute cap and screen-on release limit this behavior.

## Permissions and privacy

- BIND_ACCESSIBILITY_SERVICE is enforced by Android for binding the declared accessibility service; the user must explicitly enable it in Settings.
- WAKE_LOCK allows the app to hold the CPU awake for the bounded session.
- SYSTEM_ALERT_WINDOW is used only by the optional legacy overlay.
- The Quick Settings tile is protected by Android's BIND_QUICK_SETTINGS_TILE permission.
- The accessibility service declares no window-content retrieval and does not inspect or collect screen content.
- No Internet, storage, audio-recording, or device-admin permission is requested.

Accessibility access is powerful. Users should enable this service only if they trust the app. The service is used solely to invoke Android's global lock-screen action.

## Build

Requirements: JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.2, Gradle 8.2.

Run:

    gradle assembleDebug

The debug APK is generated at app/build/outputs/apk/debug/app-debug.apk.

Release builds enable R8 code shrinking and resource shrinking. Release APKs must be signed before distribution.

## Public release status

See RELEASE-READINESS.md for the implementation/verification ledger, supported Android API range, physical-device test requirements, privacy and permission audit, and release-signing requirements. CI runs Android lint, unit-test tasks, and debug/unsigned-release builds. Signed tag-based publishing is configured in .github/workflows/release.yml; it requires the maintainer's protected signing secrets and a pushed version tag. The unsigned CI APK is for validation only, not the signed public install package. The project is distributed under the MIT License.

## Development history

### v1.3.0 — Accessibility lock and split control

- Add an AccessibilityService that invokes GLOBAL_ACTION_LOCK_SCREEN.
- Add a separate bounded PARTIAL_WAKE_LOCK to keep the CPU running while the display is locked.
- Release the wake lock on screen-on, service interruption/unbinding/destruction, or after 30 minutes.
- Route the Quick Settings tile through the new lock controller.
- Preserve v1.2.0 AMOLED blackout as a separately accessible legacy mode.
- Add setup, privacy, battery, compatibility, and verification documentation.

### v1.2.0 — Awake AMOLED blackout

- Keep the full-screen black overlay visible with minimum window brightness.
- Add FLAG_KEEP_SCREEN_ON so the normal screen timeout does not automatically lock the phone while blackout mode is active.
- Retain double-tap restoration and the Quick Settings tile.
