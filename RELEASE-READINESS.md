# Public release readiness and compatibility

## Project status

ScreenOffPlay 1.3.0 provides two user-selectable behaviors: **Black Screen (AMOLED)** and **Screen Off / Lock**. The selection is saved locally and used by the Quick Settings tile. Users can change it at any time.

**Permission onboarding policy:** installation must not request or trigger runtime/special app access. On first launch the user chooses a mode. Only when the user taps Continue for that selected mode does the app check its required access and, if missing, open the relevant Android Settings page. Declining one mode's access must not block installation or the other mode. No special access is enabled silently.

Screen Off / Lock acquires a bounded PARTIAL_WAKE_LOCK only after the user explicitly requests a lock. It is released on ACTION_SCREEN_ON, service interruption/unbind/destruction, or after a 2-hour safety timeout. This reduces the chance of CPU suspension during the requested interval but does not override Doze, OEM restrictions, or media-app playback policy. A successful source change or CI build does not prove physical display behavior, media playback continuity, OEM compatibility, or that Play Protect will accept every distributed APK.

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
- [x] Add WAKE_LOCK and acquire PARTIAL_WAKE_LOCK only for an explicit Screen Off / Lock action, with screen-on/lifecycle release and a 2-hour safety timeout.
- [x] Update MainActivity so the user chooses a mode before any mode-specific special access is requested.
- [x] Check only the selected mode's access after the user taps Continue; explain why access is needed and open Android Settings only if it is missing.
- [x] Remove the always-visible buttons for unrelated permission settings.
- [x] Update README and CHANGELOG with the install-first/permission-later flow.
- [ ] Verify merged APK permission allowlist contains SYSTEM_ALERT_WINDOW and WAKE_LOCK only; service binding protections remain on their respective services.
- [x] Earlier baseline GitHub Actions run #54 passed on commit `cd842abf7239a0f278114e79257ff9a82c2a777e`; this predates the current wake-lock changes and does not verify them. The repository currently has no app-specific test source files, so the successful `gradle test` task is not evidence of physical-device behavior or broad automated functional coverage.
- [x] Earlier run #54 APK manifests were inspected before the current WAKE_LOCK addition; the new APK permission list still requires CI verification.
- [x] Record run #54 APK SHA-256: debug APK `d74cc1c2a3a6bfd04118bcebc9311c796c664597a5a49430dfed584f955031df`; unsigned release APK `860309cef7219ea09e0b20c801dcde126d7a8c22ea33b56111a8caf7d9c10bb8`. The unsigned release APK is validation-only and must not be installed/distributed as a release package. The debug APK is a development build, not the signed public release.
- [x] Update the CI guard to allow exactly SYSTEM_ALERT_WINDOW and WAKE_LOCK while continuing to reject accessibility event subscriptions/window-content retrieval; updated workflow still requires a passing CI run.
- [ ] Execute the updated CI permission allowlist guard on a GitHub Actions run.
- [ ] Verify clean install has no permission dialog and first launch displays mode selection on physical devices.
- [ ] Verify Black Screen asks for overlay special access only after Continue, and resumes correctly after granting/denying it.
- [ ] Verify Screen Off / Lock opens Accessibility settings only after Continue, requires explicit user enablement, and handles returning to the app.
- [ ] Verify mode changes, saved selection, Quick Settings routing, Android 8 fallback, and no-permission-denied crash on physical devices.
- [ ] Test media behavior separately for each supported app/device; do not assume background playback is allowed.

**Completion status:** Source and documentation hardening are committed. Source hardening passed exact-head CI run #54, and both generated APK manifests were inspected. A follow-up CI allowlist guard has since been added but still needs an Actions run. Physical-device verification remains pending, and the reported Play Protect warning has not been conclusively resolved. Keep PR #3 in Draft. Do not merge or publish until physical-device verification, final release signing, and the warning investigation are complete.

## Permissions and privacy audit

### Accessibility least-privilege hardening (PR follow-up)

- [x] Remove `typeWindowStateChanged` subscription: the service performs no event-driven work, so it requests no accessibility events.
- [x] Keep `canRetrieveWindowContent=false`; the service cannot inspect the active window hierarchy.
- [x] Keep the service limited to `GLOBAL_ACTION_LOCK_SCREEN`; no gestures, screenshots, text extraction, keylogging, or content forwarding are implemented.
- [x] Reset the persisted tile-active indicator when the service reconnects, preventing stale state after a process/service restart.
- [x] Build and inspect both APK variants after this change; CI run #54 passed and the merged manifest contains no new requested permissions.
- [ ] Verify the service still binds and `GLOBAL_ACTION_LOCK_SCREEN` works on physical Android 9+ devices after removing the event subscription.

**Play Protect warning investigation:** The project previously received a user report of an Android financial/identity-theft warning during sideload installation. Google documents that certain warnings can be triggered when sideloaded apps request sensitive capabilities commonly abused for financial fraud, including accessibility-related capabilities. This source-level audit does not prove the warning is a false positive and does not establish malware. Preserve the exact warning screenshot/text, APK SHA-256, signing certificate fingerprint, download source, and Play Protect scan result before deciding whether to appeal. Do not ask users to disable Play Protect or bypass a block. See Google's [Play Protect warning guidance](https://developers.google.com/android/play-protect/warning-dev-guidance).


The source manifest intentionally declares only Android special/system service permissions needed by implemented features:

- `SYSTEM_ALERT_WINDOW`: required by Android for the optional Display over other apps special-access flow used by Black Screen. It is not a dangerous runtime permission and does not grant access during installation. The user grants it manually in Android Settings.
- `BIND_ACCESSIBILITY_SERVICE`: protects the app's AccessibilityService binding. Android does not grant it to the app at install; the user explicitly enables the service in Accessibility settings.
- `BIND_QUICK_SETTINGS_TILE`: system protection for the Quick Settings tile.

WAKE_LOCK is requested only for the bounded CPU wake lock used by the explicit Screen Off / Lock action. No Internet, storage, audio-recording, device-admin, notification, or dangerous runtime permission is requested by the app's source manifest. Recheck the merged manifest and final APK permission list on every release. Do not claim Play Protect approval solely from this manifest audit; investigate any reported block using the exact Play Protect warning and APK build/signature.

The accessibility service sets `canRetrieveWindowContent=false` and does not inspect or collect screen content. Accessibility access is powerful and must be clearly explained.

## Payment-app compatibility

Some financial apps restrict operation when any third-party Accessibility service is enabled, even when that service does not read screen content. This is an app/security-policy decision and cannot be safely overridden by ScreenOffPlay. To use an affected payment app, the user must turn off ScreenOffPlay under Android Settings → Accessibility → Installed/Downloaded apps, then reopen the payment app. If Black Screen overlay is active, restore it with a double-tap before opening the payment app.

## Functional limitations and power behavior

- Black Screen shows black pixels and holds the display awake; it does not lock the device or physically power off the panel.
- Screen Off / Lock requests Android to lock the device. Android/OEM policy determines precise lock-screen and physical-panel power-down timing; this is not a direct privileged panel-power command.
- Screen Off / Lock is available only on Android 9 (API 28)+.
- A PARTIAL_WAKE_LOCK is held only after an explicit Screen Off / Lock action. It is released when the screen turns on, the service is interrupted/unbound/destroyed, or the 2-hour timeout expires. Android/OEM power policy can still impose limits.
- Background playback is not guaranteed. The wake lock only requests CPU uptime; Android, OEM policy, and the media app control playback and may impose their own restrictions. Some banking/payment apps may refuse to run while an Accessibility service is enabled; the user must temporarily disable ScreenOffPlay in Android Accessibility settings. The app does not conceal the service or bypass payment-app security checks.
- OEM power management can affect service lifetime and playback.

## Before calling a build a public stable release

- [ ] Run Android lint, Gradle tests, debug/release APK builds, APK permission audit, and signing smoke test on the current PR head.
- [x] Inspect both merged manifests and confirm no unexpected requested permission was introduced (run #54 debug and unsigned release APKs).
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
