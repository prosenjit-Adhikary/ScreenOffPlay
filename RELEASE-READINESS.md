# Public release readiness

## Status
SofP 1.3.0 contains two behaviors: Android's global Lock Screen action and a temporary CPU partial wake lock. The user explicitly enables Accessibility in Android Settings; installation itself shows no permission prompt.

## Platform
- Version 1.3.0, versionCode 4.
- Minimum installable Android 8.0 (API 26); screen-lock action requires Android 9 (API 28)+.
- Compile/target SDK 34; Java 17; no external runtime dependencies.

## Implementation
- [x] App label SofP.
- [x] Remove Device Admin receiver and policy.
- [x] Add Accessibility service using only GLOBAL_ACTION_LOCK_SCREEN.
- [x] Set canRetrieveWindowContent=false; do not inspect event contents.
- [x] User manually enables Accessibility; no install-time permission prompt.
- [x] Two-hour bounded PARTIAL_WAKE_LOCK; release on screen-on, service shutdown, or timeout.
- [x] Remove overlay mode and SYSTEM_ALERT_WINDOW.
- [x] No payment-app blocking, interception, hiding, or security bypass.
- [ ] Exact-head CI lint, tests, debug/release builds, manifest and privacy checks.
- [ ] Physical Android 9+ screen-lock and Accessibility enable/disable tests.
- [ ] Verify wake-lock release paths and media playback on target devices.
- [ ] Test UPI/banking apps with service enabled and disabled.

## Permissions and privacy
- WAKE_LOCK is normal and automatically granted; no runtime dialog.
- BIND_ACCESSIBILITY_SERVICE protects system binding; user manually enables the service in Settings.
- No Device Admin, overlay, Internet, storage, audio recording, or dangerous runtime permission.
- Accessibility config sets canRetrieveWindowContent=false. It does not read app text, windows, or node trees.

## Payment compatibility
SofP does not block, hide, intercept, automate, or inspect payment/banking apps. Some apps may independently warn about any enabled Accessibility service, even when it cannot retrieve window content. SofP cannot guarantee another app's policy; physical UPI/banking tests remain required.

## CPU/playback limitations
The partial wake lock requests CPU uptime for at most two hours after an explicit lock request. Android, OEM power management, and each media app still control background playback; uninterrupted playback is not guaranteed.

## Release gates
- [ ] Current-head CI passes all checks.
- [ ] Verify clean install has no permission prompt.
- [ ] Verify explicit Accessibility opt-in and screen lock on real devices.
- [ ] Verify wake-lock release on screen-on, service shutdown, and timeout.
- [ ] Test UPI/banking apps and record actual warnings.
- [ ] Sign release with maintainer's long-term key and verify APK signature/hash.
