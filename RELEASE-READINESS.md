# Public release readiness and compatibility

## Project status

ScreenOffPlay 1.3.0 provides two user-selectable behaviors: **Black Screen (AMOLED)** and **Screen Off / Lock**. The user's selection is persisted locally and the Quick Settings tile follows that selection. Users can change the mode at any time in the app.

No PARTIAL_WAKE_LOCK is acquired and WAKE_LOCK permission is not requested. Android retains normal control of CPU sleep, Doze, and background execution. A successful source change or CI build does not prove physical display behavior, media playback continuity, or OEM compatibility.

## Current declared Android range

- App version: 1.3.0 (version code 4).
- Minimum Android version: Android 8.0 (API 26), as declared by minSdk.
- Black Screen (AMOLED): Android 8.0+; requires Display over other apps permission.
- Screen Off / Lock: Android 9 (API 28)+; requires the user-enabled AccessibilityService.
- Compile SDK: Android 14 (API 34).
- Target SDK: Android 14 (API 34).
- Java: 17.
- External runtime dependencies: none declared in the app module.

On API 26–27, Screen Off / Lock is disabled in the selector and the Quick Settings tile can use Black Screen only.

## v1.3.0 implementation ledger

- [x] Add persistent local storage for the selected display mode; default to Black Screen (AMOLED).
- [x] Add in-app radio selection for Black Screen (AMOLED) and Screen Off / Lock.
- [x] Explain each mode's behavior, permission requirements, and Android version support in the UI.
- [x] Allow users to change the mode later; persist selection across app restarts.
- [x] Route the Quick Settings tile through the saved selection.
- [x] Keep Screen Off / Lock unavailable on API 26–27.
- [x] Preserve the legacy AMOLED overlay and double-tap restoration.
- [x] Add AccessibilityService declaration protected by BIND_ACCESSIBILITY_SERVICE and configure it without window-content retrieval.
- [x] Invoke GLOBAL_ACTION_LOCK_SCREEN from the user-initiated Quick Settings action.
- [x] Remove PARTIAL_WAKE_LOCK acquisition, wake-lock lifecycle code, timeout logic, and WAKE_LOCK manifest permission.
- [x] Update README and changelog with selectable-mode behavior and setup.
- [ ] Run exact-final-head CI after the selectable-mode implementation.
- [ ] Verify both modes, changing the selection, app restart persistence, and Quick Settings routing on physical devices.
- [ ] Verify API 26–27 shows only Black Screen and never attempts the unavailable lock action.
- [ ] Verify API 28+ Accessibility enable/disable and lock action behavior on physical devices.
- [ ] Test media behavior separately for each supported app and device; do not assume background playback is allowed.

**Completion status:** Selectable-mode source changes and documentation are committed to the PR branch. Automated verification for the final head and all physical-device checks remain outstanding. Keep PR #3 in Draft and do not merge or publish v1.3.0 until exact-head CI and required device checks pass.

## v1.2.0 implementation and verification history

- [x] Add FLAG_KEEP_SCREEN_ON to the full-screen overlay window flags.
- [x] Preserve the pure-black overlay and minimum window brightness.
- [x] Preserve double-tap restoration and the Quick Settings tile.
- [x] Update in-app instructions, README, and changelog.
- [x] Previous v1.2.0 CI workflow #33 passed on commit dac5643fabca405d3f63275c877a316e4fb3abec.
- [ ] Physical-device verification is not documented in this ledger.

## Permissions and privacy

- BIND_ACCESSIBILITY_SERVICE: Android-enforced service binding permission; the user explicitly enables the service in Settings.
- SYSTEM_ALERT_WINDOW: used only by Black Screen mode.
- BIND_QUICK_SETTINGS_TILE: protects the Quick Settings tile.
- WAKE_LOCK: not requested or used.

The accessibility service sets canRetrieveWindowContent=false and does not inspect or collect screen content. Re-audit this behavior and the manifest on every release. Accessibility access is powerful and must be clearly explained.

## Functional limitations and power behavior

- Black Screen shows black pixels and holds the display awake; it does not lock the device or physically power off the panel.
- Screen Off / Lock requests Android to lock the device. Android/OEM policy determines precise lock-screen and physical-panel power-down timing; this is not a direct privileged panel-power command.
- Screen Off / Lock is available only on Android 9 (API 28)+.
- No partial wake lock is held. Android may suspend CPU activity according to normal power-management policy after the screen turns off.
- Background playback is not guaranteed. Android and the media app control playback and may impose their own restrictions.
- OEM power management can affect service lifetime and playback.

## Before calling a build a public stable release

- [ ] Build debug and release variants from the exact tagged commit.
- [ ] Run Android lint and available automated tests on the exact final PR head.
- [ ] Verify both selectable modes and Quick Settings behavior on physical devices.
- [ ] Verify saved selection survives app restart and can be changed repeatedly.
- [ ] Test at least one physical device from each Android/OEM family claimed as supported.
- [ ] Confirm no crash, ANR, or unexpected permission prompt in tested scenarios.
- [ ] Sign the APK with the maintainer's long-term release key. Never commit the keystore or passwords.
- [ ] Verify the APK signature and record its SHA-256 checksum.
- [ ] Publish release notes, supported-device test results, known limitations, and source tag.
- [ ] Preserve the same signing key for all future updates distributed under the same application ID.

## Release signing

Public users must receive a signed APK. The maintainer should create and securely back up a long-term Android release keystore, then store its contents and passwords only as protected GitHub Actions secrets. Never paste the keystore or passwords into chat, source control, workflow logs, or issue comments.

Unsigned APKs are for build validation only and must not be advertised as the public installable release. The repository's tag-based release workflow requires the maintainer's protected signing secrets.

## Compatibility reporting template

For each tested device, record manufacturer/model, Android version, security patch level, app version/commit, selected mode, permission/service enable result, lock action result, panel-off behavior, media behavior, crashes/ANRs, and test date.

## Open-source licensing

ScreenOffPlay is distributed under the MIT License; see LICENSE. Reusers must retain the copyright and permission notice as required by the license.
