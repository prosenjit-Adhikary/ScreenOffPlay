# Public release readiness and compatibility

## Project status

ScreenOffPlay is an Android utility that places a pure-black overlay over other apps and requests minimum window brightness. It does not switch off the display hardware immediately. Android's configured screen timeout remains responsible for turning the panel off.

This project is not certified as compatible with every Android device. Device makers customize overlay behavior, Quick Settings, power management, lock-screen behavior, and display timeouts. Compatibility must be reported from real-device testing, not inferred from a successful build.

## Current declared Android range

- Minimum Android version: Android 8.0 (API 26), as declared by `minSdk`.
- Compile SDK: Android 14 (API 34).
- Target SDK: Android 14 (API 34).
- Java: 17.
- Build variants: debug and release.
- External runtime dependencies: none declared in the app module.

The minimum SDK is a build declaration, not proof that every device on that version works correctly.

## Before calling a build a public stable release

- [ ] Build debug and release variants from the exact tagged commit.
- [ ] Run Android lint and available automated tests; resolve release-blocking findings.
- [ ] Verify overlay permission flow and Quick Settings tile behavior.
- [ ] Verify single/double tap behavior, rotation, lock/unlock, process death, and screen timeout.
- [ ] Test at least one physical device from each Android/OEM family claimed as supported.
- [ ] Confirm no crash, ANR, or unexpected permission prompt in tested scenarios.
- [ ] Sign the APK with the maintainer's long-term release key. Never commit the keystore or passwords.
- [ ] Verify the APK signature and record its SHA-256 checksum.
- [ ] Publish release notes, supported-device test results, known limitations, and source tag.
- [ ] Preserve the same signing key for all future updates distributed under the same application ID.

## Release signing

Public users must receive a signed APK. The maintainer should create and securely back up a long-term Android release keystore, then store its contents and passwords only as protected GitHub Actions secrets. Never generate a different signing key for each release: users generally cannot install an update signed with a different key over the existing installation.

Unsigned APKs are for build validation only and must not be advertised as the public installable release.

## Permissions and privacy

The manifest currently declares `SYSTEM_ALERT_WINDOW`, which the user grants manually in Android Settings. The Quick Settings service is protected by Android's `BIND_QUICK_SETTINGS_TILE` permission. The current manifest does not declare Internet, storage, audio, notification, accessibility, device-admin, or battery-optimization permissions.

Re-check the manifest and dependency tree for every release. A permission audit is not a substitute for reviewing all runtime behavior.

## Functional limitations

- Black pixels can reduce emitted light on OLED/AMOLED panels, but the app does not guarantee a specific battery saving.
- The display panel turns off only when Android's configured timeout or another system action turns it off.
- Media playback and background playback policy are controlled by Android and the media app; ScreenOffPlay cannot guarantee continued playback or bypass app restrictions.
- Overlay and Quick Settings behavior can vary by Android version and device manufacturer.
- Users must be able to restore the overlay; test the restore gesture on every supported device.

## Compatibility reporting template

For each tested device, record: manufacturer/model, Android version, security patch level, app version/commit, overlay permission result, tile result, timeout result, restore result, media behavior, crashes/ANRs, and test date. Do not claim a device family is supported based on testing a different model.

## Open-source licensing

ScreenOffPlay is distributed under the MIT License; see [LICENSE](LICENSE). Reusers must retain the copyright and permission notice as required by that license.
