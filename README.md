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
