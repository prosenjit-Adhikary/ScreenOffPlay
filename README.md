# Screen Off Play

A minimal AMOLED blackout utility with a Quick Settings tile.

## AMOLED Blackout mode

- Places a full-screen pure black (`#000000`) overlay above the current app.
- Sets the overlay window brightness to its minimum (`0.0f`).
- Uses `FLAG_KEEP_SCREEN_ON` while the overlay is visible, preventing Android's normal screen timeout from automatically turning off the display and locking the phone.
- Double-tap the black overlay to remove it. The device power button can still lock the phone manually.
- No external libraries are used.

On OLED/AMOLED panels, black pixels emit no light. This mode is intended to keep the media app in the foreground without the screen-timeout lock interrupting playback. The display hardware remains powered; this is not a physical panel power-off and does not promise a particular battery saving.

Audio playback is still controlled by Android and the media app. This app does not bypass YouTube or another app's background-playback restrictions. If the user manually locks the phone or switches away from the media app, that app's own behavior applies.

## Use

1. Grant **Display over other apps** permission.
2. Add **Screen Off** to Quick Settings.
3. Start media playback in the app you want to use.
4. Open Quick Settings and tap **Screen Off**.
5. The black overlay stays visible and holds the display awake, so the normal screen timeout should not automatically lock the phone.
6. Double-tap the black screen to restore the display. The power button remains available for manual locking.

To stop blackout mode, double-tap anywhere on the black overlay.

## Permissions

The only app permission requested is `SYSTEM_ALERT_WINDOW` (Display over other apps), granted manually in Android Settings. The Quick Settings tile is protected by Android's system `BIND_QUICK_SETTINGS_TILE` permission. The app does not request Internet, audio, storage, notification, accessibility, device-admin, or battery-optimization permissions.

## Build

Requirements: JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.2, Gradle 8.2.

Run:

    gradle assembleDebug

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

Release builds enable R8 code shrinking and resource shrinking. Release APKs must be signed before distribution.


## Public release status

See [RELEASE-READINESS.md](RELEASE-READINESS.md) for the supported Android API range, device compatibility expectations, privacy/permission audit points, release-signing requirements, and the checklist required before a stable public release. CI runs Android lint, unit-test tasks, and debug/unsigned-release builds. Signed tag-based publishing is configured in `.github/workflows/release.yml`; it requires the maintainer's protected signing secrets and a pushed version tag. The unsigned CI APK is for validation only, not the signed public install package. The project is distributed under the [MIT License](LICENSE).

## Development roadmap

### v1.2.0 — Awake AMOLED blackout

- Keep the full-screen black overlay visible with minimum window brightness.
- Add `FLAG_KEEP_SCREEN_ON` so the normal screen timeout does not automatically lock the phone while blackout mode is active.
- Retain double-tap restoration and the Quick Settings tile.
- Update in-app guidance and release documentation to distinguish black-screen mode from physical display power-off.

### v1.3.0 — True screen-off playback (research and implementation target)

- Investigate and implement a safe way to turn the physical display off while supported audio playback continues.
- Preserve the existing AMOLED black-overlay mode as an alternative.
- Provide a safe and reliable way to wake or restore the display.
- Validate behavior across Android versions and device manufacturers.
- Document any required permissions, platform restrictions, and unsupported scenarios.

**Important:** Android does not provide ordinary third-party apps with unrestricted control over powering off the physical display. Background audio behavior is controlled by Android and the media app. Therefore, true display power-off with continued playback is a development target, not a guaranteed capability. The implementation will only be described as supported after real-device testing confirms it. The project will aim to retain its privacy-first approach and avoid adding permissions that are not essential.
