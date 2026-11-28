# Public release readiness

## Status
SofP 1.3.0 contains Android's global Lock Screen action and a temporary CPU partial wake lock. Installation does not display a permission prompt or enable special access. The user manually opts into Accessibility from the first-run screen.

## Platform
- Version 1.3.0, versionCode 4.
- Minimum installable Android 8.0 (API 26); screen-lock action requires Android 9 (API 28)+.
- Compile/target SDK 34; Java 17; no external runtime dependencies.

## Implementation
- [x] App label and Quick Settings tile label are SofP.
- [x] Remove Device Admin receiver and policy.
- [x] Add Accessibility service using only GLOBAL_ACTION_LOCK_SCREEN.
- [x] Do not declare an Accessibility event subscription; platform default is zero events.
- [x] Set canRetrieveWindowContent=false; do not inspect event contents.
- [x] User manually opens Accessibility Settings from an explicit first-run button and enables SofP.
- [x] No special access is enabled automatically.
- [x] Two-hour bounded PARTIAL_WAKE_LOCK; release on screen-on, service interruption/shutdown, or timeout.
- [x] Remove overlay mode and SYSTEM_ALERT_WINDOW.
- [x] No payment-app blocking, interception, hiding, or security bypass.
- [ ] Exact-head CI lint, tests, debug/release builds, merged-manifest permission and privacy checks.
- [ ] Physical Android 9+ screen-lock and Accessibility enable/disable tests.
- [ ] Verify wake-lock release paths and media behavior on target devices.
- [ ] Test UPI/banking apps with service enabled and disabled.

## Permissions and privacy
- WAKE_LOCK is the only ordinary requested permission. It is a normal permission automatically granted by Android at installation, with no runtime dialog. It is required for PARTIAL_WAKE_LOCK.
- BIND_ACCESSIBILITY_SERVICE protects system binding; the user manually enables the service in Settings.
- No Device Admin, overlay, Internet, storage, contacts, SMS, audio recording, or dangerous runtime permission.
- Accessibility does not declare an event subscription (zero events by default) and sets canRetrieveWindowContent=false; it does not read app text, windows, or node trees.

## Payment compatibility
SofP does not block, hide, intercept, automate, or inspect payment/banking apps. Some apps may independently warn about any enabled Accessibility service, even one subscribed to no events and unable to retrieve window content. SofP cannot guarantee another app's policy; physical UPI/banking tests remain required.

## CPU/playback limitations
The partial wake lock requests CPU uptime for at most two hours after an explicit lock request. Android, OEM power management, and each media app still control background playback; uninterrupted playback is not guaranteed.

## Release gates
- [ ] Current-head CI passes all checks.
- [ ] Verify clean install has no permission prompt.
- [ ] Verify first-run screen asks the user to opt in; Settings opens only after the user taps.
- [ ] Verify Accessibility is not enabled automatically and can be disabled again.
- [ ] Verify Android 9+ lock action works with the no-event service on physical devices.
- [ ] Verify wake-lock release on screen-on, service interruption/shutdown, and timeout.
- [ ] Test UPI/banking apps and record actual warnings.
- [ ] Investigate and record Play Protect's exact verdict, APK SHA-256, and signing certificate fingerprint. Do not ask users to disable Play Protect or bypass a block.
- [ ] Sign release with maintainer's long-term key and verify APK signature/hash.
