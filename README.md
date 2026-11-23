# Screen Off Play

A minimal AMOLED blackout utility with a Quick Settings tile.

## AMOLED Blackout mode

- Places a full-screen pure black (`#000000`) overlay above the current app.
- Sets the overlay window brightness to its minimum (`0.0f`).
- Does **not** use `FLAG_KEEP_SCREEN_ON`; Android's configured screen timeout remains active and can turn the display panel off normally.
- Double-tap the black overlay to remove it. If the display has timed out, wake the phone first, then double-tap.
- No external libraries are used.

On OLED/AMOLED panels, black pixels emit no light. This can substantially reduce display light output, but it is not equivalent to an immediate hardware power-off: display electronics remain active until Android's normal timeout, and actual battery savings vary by device, brightness, refresh rate, and settings.

Audio playback is controlled by the media app and Android. This app does not guarantee that YouTube or another app will continue playback after screen timeout and does not bypass media-app background-playback restrictions.

## Use

1. Grant **Display over other apps** permission.
2. Add **Screen Off** to Quick Settings.
3. Start media playback in the app you want to use.
4. Open Quick Settings and tap **Screen Off**.
5. Leave the phone untouched until its configured screen timeout turns the display off.
6. Wake the phone and double-tap the black overlay to restore the screen.

For a quicker display-off, set a shorter screen timeout in Android Settings before using the tile.

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

The following items are planned for future versions. They are **not included in v1.0.0** unless explicitly listed in the sections above. The roadmap is subject to Android platform limitations and device compatibility testing.

### v1.2.0 — Usability and visual improvements

- Add a new, polished application logo.
- Make double-tap detection customizable.
- Add an optional auto-dismiss timer.
- Improve the Quick Settings tile and its state handling.
- Fix screen-rotation, overlay lifecycle, and related reliability issues.

### v1.3.0 — True screen-off playback (research and implementation target)

- Investigate and implement a safe way to turn the physical display off while supported audio playback continues.
- Preserve the existing AMOLED black-overlay mode as an alternative.
- Provide a safe and reliable way to wake or restore the display.
- Validate behavior across Android versions and device manufacturers.
- Document any required permissions, platform restrictions, and unsupported scenarios.

**Important:** Android does not provide ordinary third-party apps with unrestricted control over powering off the physical display. Background audio behavior is controlled by Android and the media app. Therefore, true display power-off with continued playback is a development target, not a guaranteed capability. The implementation will only be described as supported after real-device testing confirms it. The project will aim to retain its privacy-first approach and avoid adding permissions that are not essential.

