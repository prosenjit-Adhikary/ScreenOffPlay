# Changelog

## 1.3.0
- Replace Device Admin with a user-enabled Accessibility service for Android's global Lock Screen action.
- Request zero Accessibility events and disable window-content retrieval; use Accessibility only for the explicit lock action.
- Show first-run instructions; open Android Accessibility Settings only after the user taps the opt-in button.
- Do not show a permission dialog during installation or enable special access automatically.
- Use the normal WAKE_LOCK permission for a bounded two-hour PARTIAL_WAKE_LOCK; Android auto-grants this permission without a runtime prompt.
- Release the wake lock on screen-on, service shutdown, or timeout.
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
