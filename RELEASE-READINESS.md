# Public release readiness and compatibility

## Project status

ScreenOffPlay 1.3.0 uses Android AccessibilityService to request a device lock through GLOBAL_ACTION_LOCK_SCREEN. **The app does not acquire PARTIAL_WAKE_LOCK and does not request WAKE_LOCK permission.** Android retains normal control of CPU sleep, Doze, and background execution. This is intentional to avoid unnecessary battery use.

The previous v1.2.0 AMOLED blackout overlay remains available as a separate legacy mode. A successful source change or CI build does not prove physical display behavior, media playback continuity, or OEM compatibility.

## Current declared Android range

- App version: 1.3.0 (version code 4).
- Minimum Android version: Android 8.0 (API 26), as declared by minSdk.
- New Accessibility lock mode: Android 9 (API 28) or later, because GLOBAL_ACTION_LOCK_SCREEN is not available on API 26–27.
- Compile SDK: Android 14 (API 34).
- Target SDK: Android 14 (API 34).
- Java: 17.
- External runtime dependencies: none declared in the app module.

The minimum SDK declaration does not mean the new lock mode is supported on API 26–27. The legacy overlay remains the compatibility path on those versions.

## v1.3.0 implementation ledger

- [x] Add AccessibilityService declaration protected by BIND_ACCESSIBILITY_SERVICE.
- [x] Configure the service without window-content retrieval.
- [x] Invoke GLOBAL_ACTION_LOCK_SCREEN from the user-initiated Quick Settings action.
- [x] Remove PARTIAL_WAKE_LOCK acquisition, wake-lock lifecycle code, timeout logic, and WAKE_LOCK manifest permission.
- [x] Keep CPU power management under Android's normal policy; no claim is made that CPU stays awake after locking.
- [x] Route the Quick Settings tile through the accessibility lock controller.
- [x] Add an in-app link to Android Accessibility settings.
- [x] Preserve the old AMOLED overlay as a separate legacy mode.
- [x] Update README and changelog with setup, permission, privacy, power-management, and compatibility details.
- [x] Exact-head GitHub Actions workflow #39 passed on commit 25dd602967ad5bf4a6728213b371b9a7a05c83c3. Android SDK setup, Android lint, unit tests, debug APK build, unsigned release APK validation, artifact uploads, and ephemeral CI signing smoke test all passed.
- [ ] Verify API 28+ lock action and screen-off behavior on physical devices from multiple OEMs.
- [ ] Verify normal wake/unlock flow and Quick Settings tile state recovery.
- [ ] Test media playback separately for each target media app; do not assume background playback is allowed.
- [ ] Verify API 26–27 gracefully use only the legacy overlay and do not claim new lock-mode support.

**Completion status:** Source implementation, documentation, and exact-head CI are complete. Physical-device validation remains outstanding and cannot be inferred from CI. Keep PR #3 in Draft and do not merge or publish v1.3.0 until the required device checks are recorded.

## v1.2.0 implementation and verification history

- [x] Add FLAG_KEEP_SCREEN_ON to the full-screen overlay window flags.
- [x] Preserve the pure-black overlay and minimum window brightness.
- [x] Preserve double-tap restoration and the Quick Settings tile.
- [x] Update in-app instructions, README, and changelog.
- [x] Previous v1.2.0 CI workflow #33 passed on commit dac5643fabca405d3f63275c877a316e4fb3abec.
- [ ] Physical-device verification is not documented in this ledger.

## Permissions and privacy

- BIND_ACCESSIBILITY_SERVICE: Android-enforced service binding permission; the user explicitly enables the service in Settings.
- SYSTEM_ALERT_WINDOW: used only by the optional legacy overlay.
- BIND_QUICK_SETTINGS_TILE: protects the Quick Settings tile service.
- WAKE_LOCK: not requested or used.

The accessibility service sets canRetrieveWindowContent=false and does not inspect or collect screen content. Re-audit this behavior and the manifest on every release.

Accessibility access is powerful and must be explained clearly to users. Do not describe it as harmless or request it without explaining that it is used to lock the device.

## Functional limitations and power behavior

- GLOBAL_ACTION_LOCK_SCREEN requests Android to lock the device. Android/OEM policy determines the precise lock-screen and physical-panel power-down timing; this API is not a direct privileged panel power command.
- The new lock action is available on Android 9 (API 28) and later.
- No partial wake lock is held. Android may suspend CPU activity according to normal power-management policy after the screen turns off.
- Background playback is not guaranteed. Android and the media app control playback and may impose their own restrictions.
- OEM power management can affect service lifetime and playback.
- The legacy black overlay does not physically power off the panel; it only displays black pixels and holds the display awake.

## Before calling a build a public stable release

- [ ] Build debug and release variants from the exact tagged commit.
- [x] Run Android lint and available automated tests on exact implementation commit 25dd602967ad5bf4a6728213b371b9a7a05c83c3; all workflow #39 checks passed.
- [ ] Verify accessibility enable/disable and lock action behavior on physical devices.
- [ ] Test media behavior separately for each supported app and device.
- [ ] Test legacy overlay permission, double-tap restoration, rotation, and lock/unlock.
- [ ] Test at least one physical device from each Android/OEM family claimed as supported.
- [ ] Confirm no crash, ANR, or unexpected permission prompt in tested scenarios.
- [ ] Sign the APK with the maintainer's long-term release key. Never commit the keystore or passwords.
- [ ] Verify the APK signature and record its SHA-256 checksum.
- [ ] Publish release notes, supported-device test results, known limitations, and source tag.
- [ ] Preserve the same signing key for all future updates distributed under the same application ID.

## Release signing

Public users must receive a signed APK. The maintainer should create and securely back up a long-term Android release keystore, then store its contents and passwords only as protected GitHub Actions secrets. Never generate a different signing key for each release: users generally cannot install an update signed with a different key over the existing installation.

Unsigned APKs are for build validation only and must not be advertised as the public installable release.

The repository includes .github/workflows/release.yml. Pushing a version tag matching v* starts a clean release build, runs lint and unit-test tasks, signs the APK, verifies the signature, computes SHA-256, and publishes a GitHub Release. The workflow requires the maintainer's protected signing secrets. Never paste the keystore or passwords into chat, source control, workflow logs, or issue comments.

## Compatibility reporting template

For each tested device, record manufacturer/model, Android version, security patch level, app version/commit, Accessibility service enable result, lock action result, panel-off behavior, media behavior, crashes/ANRs, and test date. Do not claim a device family is supported based on testing a different model.

## Open-source licensing

ScreenOffPlay is distributed under the MIT License; see LICENSE. Reusers must retain the copyright and permission notice as required by the license.
