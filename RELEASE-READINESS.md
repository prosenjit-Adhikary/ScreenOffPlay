# Public release readiness and compatibility

## Project status

SofP 1.3.0 provides two user-selectable behaviors: **Black Screen (AMOLED)** and **Screen Off / Lock**. The selection is saved locally and used by the Quick Settings tile.

Installation does not request special access. The user chooses a mode first; only after tapping Continue does SofP request the access needed by that selected mode. No access is silently enabled.

## Current Android range

- App version: 1.3.0 (version code 4).
- Minimum Android: Android 8.0 (API 26).
- Black Screen: Android 8.0+; requires Display over other apps special access.
- Screen Off / Lock: Android 9 (API 28)+; requires explicit Device Admin approval.
- Compile/target SDK: Android 14 (API 34).
- Java: 17.
- External runtime dependencies: none.

## v1.3.0 implementation ledger

- [x] Persist selected mode locally; default to Black Screen.
- [x] Allow users to change the selected mode.
- [x] Route Quick Settings through the saved mode.
- [x] Keep Screen Off / Lock unavailable on Android 8 (API 26–27).
- [x] Preserve the legacy AMOLED overlay and double-tap restoration.
- [x] Replace AccessibilityService with a DeviceAdminReceiver using only the `force-lock` policy.
- [x] Do not declare/register an Accessibility service.
- [x] Request Device Admin approval only after the user selects Screen Off / Lock and taps Continue.
- [x] Keep WAKE_LOCK bounded to 2 hours and release on ACTION_SCREEN_ON.
- [x] Rename the Android system/launcher label and in-app title to SofP.
- [x] Update README and changelog.
- [x] Exact-head GitHub Actions run #78 passed on `90da75a618ac3c53430014b5cc96fa2c1df4a5b4`: Android lint, Gradle test task, debug/release APK builds, APK permission allowlist, Device Admin policy guard, artifact uploads, and ephemeral signing smoke test. This does not replace physical-device tests.
- [ ] Verify Device Admin activation, lock action, wake-lock release, and tile behavior on physical Android 9+ devices.
- [ ] Verify clean install and deferred overlay permission on physical devices.
- [ ] Test payment apps (UPI and banking) with SofP installed and with the overlay closed. SofP has no Accessibility service; app-specific security policy and Device Admin detection still require physical testing.
- [ ] Test media behavior separately for each supported app/device; background playback is not guaranteed.
- [ ] Investigate any Play Protect warning using the exact warning text, APK hash, signing certificate, and scan result. Never ask users to disable Play Protect or bypass a warning.

**Verification status:** Current Device Admin implementation CI run #78 passed on commit `90da75a618ac3c53430014b5cc96fa2c1df4a5b4`. Debug and unsigned release APK artifacts were generated. Physical-device verification remains pending, including payment-app compatibility, Device Admin activation/lock behavior, wake-lock release, overlay restoration, and media behavior. Keep PR #3 in Draft; do not merge or publish until physical-device checks and release signing are complete.

## Permissions and privacy

- `SYSTEM_ALERT_WINDOW`: optional overlay special access for Black Screen.
- `WAKE_LOCK`: bounded CPU wake lock after an explicit Screen Off / Lock action.
- `BIND_DEVICE_ADMIN`: protects the Device Admin receiver; activation is explicitly user-approved.
- `BIND_QUICK_SETTINGS_TILE`: protects the Quick Settings tile.
- Device Admin requests only `force-lock`; no wipe, password-reset, or other device-management policies.
- SofP does not register an Accessibility service and does not inspect screen content.
- No Internet, storage, audio-recording, or dangerous runtime permission is requested.

## Payment-app compatibility

SofP does not register or enable an Accessibility service, removing the common compatibility conflict caused by apps detecting a third-party Accessibility service. Close the Black Screen overlay with a double-tap before opening a payment app. Device Admin approval is used only for the explicit lock feature; some apps may apply their own policies, so UPI/banking compatibility must be verified on real devices. SofP must not conceal permissions or bypass payment-app security checks.

## Functional and power limitations

- Black Screen displays black pixels and keeps the display awake; it does not lock the device or physically power off the panel.
- Screen Off / Lock requests Android to lock the device using Device Admin force-lock. OEM policy controls exact lock-screen/panel behavior.
- The bounded PARTIAL_WAKE_LOCK requests CPU uptime for at most 2 hours and is released on ACTION_SCREEN_ON. It does not guarantee background playback or override Android/OEM/media-app policy.
- Android 8 (API 26–27) supports Black Screen only.

## Before public release

- [ ] Current-head CI passes all build, lint, tests, permission/policy checks, and signing smoke test.
- [ ] Verify clean install, first-launch mode selection, and deferred permission onboarding.
- [ ] Verify both modes and Quick Settings behavior on physical devices.
- [ ] Verify saved selection survives app restart and can be changed repeatedly.
- [ ] Test at least one physical device from each Android/OEM family claimed as supported.
- [ ] Confirm no crash, ANR, or unexpected permission prompt.
- [ ] Build from the exact tagged commit and sign with the maintainer's long-term release key.
- [ ] Verify APK signature and record SHA-256.
- [ ] Publish release notes and physical-device test results.
- [ ] Preserve the same signing key for all future updates under the same application ID.

## Release signing

Public users must receive a signed APK. Store the long-term release keystore and passwords only as protected GitHub Actions secrets. Never commit them or paste them into chat or logs. Unsigned CI APKs are validation-only and must not be distributed as public releases.

## Licensing

SofP is distributed under the MIT License; see LICENSE.
