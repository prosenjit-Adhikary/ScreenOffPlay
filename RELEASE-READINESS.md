# Public release readiness and compatibility

## Project status

ScreenOffPlay provides an AMOLED blackout overlay over other apps. While the overlay is visible, it requests minimum window brightness and uses `FLAG_KEEP_SCREEN_ON` to prevent the normal screen timeout from automatically turning off the display and locking the phone. It does not switch off the display hardware.

This project is not certified as compatible with every Android device. Device makers customize overlay behavior, Quick Settings, power management, lock-screen behavior, and display timeouts. Compatibility must be reported from real-device testing, not inferred from a successful build.

## Current declared Android range

- App version: 1.2.0 (version code 3).
- Minimum Android version: Android 8.0 (API 26), as declared by `minSdk`.
- Compile SDK: Android 14 (API 34).
- Target SDK: Android 14 (API 34).
- Java: 17.
- Build variants: debug and release.
- External runtime dependencies: none declared in the app module.

The minimum SDK is a build declaration, not proof that every device on that version works correctly.

## v1.2.0 implementation and verification status

- [x] Add `FLAG_KEEP_SCREEN_ON` to the full-screen overlay window flags.
- [x] Preserve the pure-black overlay and minimum window brightness.
- [x] Preserve double-tap restoration and the existing Quick Settings toggle.
- [x] Update in-app instructions, README, and changelog to describe the awake-blackout behavior and its limitations.
- [x] GitHub Actions Android Build workflow #32 passed on the updated documentation PR head `ef17e0e532ab9b05c75feb7f06223a471a626264`: Android lint, unit tests, debug APK build, unsigned release APK build, artifact uploads, and ephemeral-key release-signing smoke test all succeeded. This validates CI build tasks, not physical-device behavior or the maintainer's long-term release key.
- [ ] Verify on physical devices that the screen timeout does not lock the phone while the overlay is active.
- [ ] Verify double-tap restoration after extended use and after opening Quick Settings.
- [ ] Test media playback with supported media apps; record each app's own background-playback policy separately.
- [ ] Test manual power-button locking and normal restoration after wake.
- [ ] Record manufacturer/model, Android version, app version/commit, and results before declaring device compatibility.

The source change and automated CI verification are complete for the recorded PR head. Physical-device behavior remains unverified. Do not mark the release stable until the remaining device-test gates pass.

## Before calling a build a public stable release

- [ ] Build debug and release variants from the exact tagged commit.
- [ ] Run Android lint and available automated tests; resolve release-blocking findings.
- [ ] Verify overlay permission flow and Quick Settings tile behavior.
- [ ] Verify single/double tap behavior, rotation, lock/unlock, process death, and screen-timeout suppression.
- [ ] Test at least one physical device from each Android/OEM family claimed as supported.
- [ ] Confirm no crash, ANR, or unexpected permission prompt in tested scenarios.
- [ ] Sign the APK with the maintainer's long-term release key. Never commit the keystore or passwords.
- [ ] Verify the APK signature and record its SHA-256 checksum.
- [ ] Publish release notes, supported-device test results, known limitations, and source tag.
- [ ] Preserve the same signing key for all future updates distributed under the same application ID.

## Release signing

Public users must receive a signed APK. The maintainer should create and securely back up a long-term Android release keystore, then store its contents and passwords only as protected GitHub Actions secrets. Never generate a different signing key for each release: users generally cannot install an update signed with a different key over the existing installation.

Unsigned APKs are for build validation only and must not be advertised as the public installable release.

## Automated release signing workflow

The repository includes `.github/workflows/release.yml`. Pushing a version tag matching `v*` starts a clean release build, runs lint and unit-test tasks, signs the APK, verifies the signature, computes SHA-256, and publishes a GitHub Release.

Before creating a version tag, the maintainer must configure these four repository Actions secrets under Settings → Secrets and variables → Actions:

- `ANDROID_KEYSTORE_BASE64`: Base64-encoded contents of the long-term release keystore.
- `ANDROID_STORE_PASSWORD`: keystore password.
- `ANDROID_KEY_ALIAS`: release key alias.
- `ANDROID_KEY_PASSWORD`: release key password.

The signing keystore must be generated and backed up securely by the maintainer. Never paste the keystore, passwords, or private key into chat, source control, workflow logs, or issue comments. Do not rotate the key between releases if users are expected to install updates over the existing app.

After configuring and independently checking those secrets, push a version tag such as `v1.2.0`. The tag workflow is deliberately not run against ordinary branch commits. A failed signing or verification step blocks publication.

The current unsigned CI artifact is not a public release. This workflow cannot produce a signed release until the maintainer configures the secrets and pushes a version tag.

## Permissions and privacy

The manifest currently declares `SYSTEM_ALERT_WINDOW`, which the user grants manually in Android Settings. The Quick Settings service is protected by Android's `BIND_QUICK_SETTINGS_TILE` permission. The current manifest does not declare Internet, storage, audio, notification, accessibility, device-admin, or battery-optimization permissions.

Re-check the manifest and dependency tree for every release. A permission audit is not a substitute for reviewing all runtime behavior.

## Functional limitations

- Black pixels can reduce emitted light on OLED/AMOLED panels, but the app does not guarantee a specific battery saving.
- The display remains awake while the overlay is visible; this is intentional so the normal screen timeout does not automatically lock the phone.
- The display panel is not physically powered off by this mode.
- Media playback and background playback policy are controlled by Android and the media app; ScreenOffPlay cannot guarantee continued playback or bypass app restrictions.
- Pressing the phone's physical power button can still lock the device.
- Overlay and Quick Settings behavior can vary by Android version and device manufacturer.
- Users must be able to restore the overlay; test the restore gesture on every supported device.

## Compatibility reporting template

For each tested device, record: manufacturer/model, Android version, security patch level, app version/commit, overlay permission result, tile result, screen-timeout suppression result, restore result, media behavior, crashes/ANRs, and test date. Do not claim a device family is supported based on testing a different model.

## Open-source licensing

ScreenOffPlay is distributed under the MIT License; see [LICENSE](LICENSE). Reusers must retain the copyright and permission notice as required by the license.
