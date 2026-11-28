# Changelog

## 1.3.0
- Replace Device Admin with a user-enabled Accessibility service for Android's global Lock Screen action.
- Use Accessibility only for the lock action; do not retrieve window content or inspect app UI.
- Acquire a bounded two-hour PARTIAL_WAKE_LOCK for explicit lock requests; release on screen-on, service shutdown, or timeout.
- Do not request runtime/special access during installation; user manually enables Accessibility in Android Settings.
- Remove Device Admin, overlay mode, and overlay permission.
- Keep payment/banking apps launchable; add no blocking, interception, hiding, or payment-specific logic.
- Keep app label SofP and version 1.3.0 (version code 4).

## 1.2.0
- Keep the AMOLED blackout overlay visible with FLAG_KEEP_SCREEN_ON.
- Preserve double-tap restoration and clarify this is not physical display power-off.
- Increment version to 1.2.0 (version code 3).

## 1.1.0
- Add custom launcher identity, adaptive icon, and monochrome tile icon.
- Increment version to 1.1.0 (version code 2).
