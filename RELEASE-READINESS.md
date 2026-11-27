# Public release readiness and compatibility

## Project status

ScreenOffPlay 1.3.0 provides two user-selectable behaviors: **Black Screen (AMOLED)** and **Screen Off / Lock**. The selection is saved locally and used by the Quick Settings tile. Users can change it at any time.

**Permission onboarding policy:** installation must not request or trigger runtime/special app access. On first launch the user chooses a mode. Only when the user taps Continue for that selected mode does the app check its required access and, if missing, open the relevant Android Settings page. Declining one mode's access must not block installation or the other mode. No special access is enabled silently.

No PARTIAL_WAKE_LOCK is acquired and WAKE_LOCK permission is not requested. Android retains normal control of CPU sleep, Doze, and background execution. A successful source change or CI build does not prove physical display behavior, media playback continuity, OEM compatibility, or that Play Protect will accept every distributed APK.

## Current declared Android range

- App version: 1.3.0 (version code 4).
- Minimum Android version: Android 8.0 (API 26), as declared by minSdk.
- Black Screen (AMOLED): Android 8.0+; requires Display over other apps special access, requested only when the user starts this mode.
- Screen Off / Lock: Android 9 (API 28)+; requires the user-enabled AccessibilityService, configured only when the user starts this mode.
- Compile SDK: Android 14 (API 34).
- Target SDK: Android 14 (API 34).
- Java: 17.
- External runtime dependencies: none declared in the app module.

On API 26–27, Screen Off / Lock is disabled and Black Screen is the only available mode.

## v1.3.0 implementation ledger

- [x] Persist the selected display mode locally; default to Black Screen (AMOLED).
- [x] Allow users to change the selected mode at any time.
- [x] Route the Quick Settings tile through the saved selection.
- [x] Keep Screen Off / Lock unavailable on API 26–27.
- [x] Preserve the legacy AMOLED overlay and double-tap restoration.
- [x] Keep Accessibility window-content retrieval disabled.
- [x] Remove PARTIAL_WAKE_LOCK acquisition and WAKE_LOCK manifest permission.
- [x] Update MainActivity so the user chooses a mode before any mode-specific special access is requested.
- [x] Check only the selected mode's access after the user taps Continue; explain why access is needed and open Android Settings only if it is missing.
- [x] Remove the always-visible buttons for unrelated permission settings.
- [x] Update README and CHANGELOG with the install-first/permission-later flow.
- [x] Source-level manifest audit: only SYSTEM_ALERT_WINDOW is declared as a uses-permission; service binding protections are declared on their respective services.
- [x] Exact-head GitHub Actions verification: Android Build run #49 passed on commit 6ded987a747bb0bbce55866ce964e83080aa76f3. Android lint, unit tests, debug APK, unsigned release APK, artifact uploads, and ephemeral-key release-signing smoke test all completed successfully.
- [x] Inspect the merged manifest and APK permission list in the CI debug APK from run #49 (source commit 6ded987a747bb0bbce55866ce964e83080aa76f3): API 26 / target 34, only one uses-permission entry (SYSTEM_ALERT_WINDOW), and no WAKE_LOCK, Internet, storage, audio, notification, or device-admin permission. Service binding protections are attached to the corresponding services.
- [ ] Verify clean install has no permission dialog and first launch displays mode selection on physical devices.
- [ ] Verify Black Screen asks for overlay special access only after Continue, and resumes correctly after granting/denying it.
- [ ] Verify Screen Off / Lock opens Accessibility settings only after Continue, requires explicit user enablement, and handles returning to the app.
- [ ] Verify mode changes, saved selection, Quick Settings routing, Android 8 fallback, and no-permission-denied crash on physical devices.
- [ ] Test media behavior separately for each supported app/device; do not assume background playback is allowed.

**Completion status:** Source and documentation changes are committed. Exact-head CI is passing (run #49). Physical-device testing and APK-level merged-manifest/permission inspection remain pending. Keep PR #3 in Draft. Do not merge or publish until physical-device verification and APK permission inspection pass.

## Permissions and privacy audit

The source manifest intentionally declares only Android special/system service permissions needed by implemented features:

- `SYSTEM_ALERT_WINDOW`: required by Android for the optional Display over other apps special-access flow used by Black Screen. It is not a dangerous runtime permission and does not grant access during installation. The user grants it manually in Android Settings.
- `BIND_ACCESSIBILITY_SERVICE`: protects the app's AccessibilityService binding. Android does not grant it to the app at install; the user explicitly enables the service in Accessibility settings.
- `BIND_QUICK_SETTINGS_TILE`: system protection for the Quick Settings tile.

No Internet, storage, audio-recording, device-admin, notification, WAKE_LOCK, or dangerous runtime permission is requested by the app's source manifest. Recheck the merged manifest and final APK permission list on every release. Do not claim Play Protect approval solely from this manifest audit; investigate any reported block using the exact Play Protect warning and APK build/signature.

The accessibility service sets `canRetrieveWindowContent=false` and does not inspect or collect screen content. Accessibility access is powerful and must be clearly explained.

## Functional limitations and power behavior

- Black Screen shows black pixels and holds the display awake; it does not lock the device or physically power off the panel.
- Screen Off / Lock requests Android to lock the device. Android/OEM policy determines precise lock-screen and physical-panel power-down timing; this is not a direct privileged panel-power command.
- Screen Off / Lock is available only on Android 9 (API 28)+.
- No partial wake lock is held. Android may suspend CPU activity according to normal power-management policy after the screen turns off.
- Background playback is not guaranteed. Android and the media app control playback and may impose their own restrictions.
- OEM power management can affect service lifetime and playback.

## Before calling a build a public stable release

- [x] Latest PR-head CI passes Android lint, unit tests, debug APK, and unsigned release validation APK (run #49, exact head recorded above).
- [x] Inspect the merged manifest and APK permission list; confirm no unexpected permission was introduced (run #49 debug APK).
- [ ] Verify clean install, first-launch mode selection, and deferred permission onboarding on physical devices.
- [ ] Verify both selectable modes and Quick Settings behavior on physical devices.
- [ ] Verify saved selection survives app restart and can be changed repeatedly.
- [ ] Test at least one physical device from each Android/OEM family claimed as supported.
- [ ] Confirm no crash, ANR, or unexpected permission prompt in tested scenarios.
- [ ] Build the release variant from the exact tagged commit and sign with maintainer's long-term release key.
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
