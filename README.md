# SofP

SofP has two functions: request Android's **Lock Screen** global action and hold a bounded **PARTIAL_WAKE_LOCK** so the CPU can continue running temporarily after the screen locks.

## Installation and first run
- Installing SofP does not display a permission dialog or enable special access.
- On first launch, no access is enabled automatically. The user chooses whether to tap **Enable SofP Accessibility**.
- Only after that user action does Android Accessibility Settings open. The user must manually find SofP and switch it on.
- The Accessibility service explicitly requests zero events (accessibilityEventTypes=0) and cannot retrieve window content (canRetrieveWindowContent=false).
- WAKE_LOCK is a normal Android permission required for CPU wake-lock behavior. Android grants it automatically at installation; it has no runtime prompt and cannot be manually granted through a first-run dialog.
- No Device Admin, overlay, Internet, storage, contacts, SMS, or dangerous runtime permission is requested.

## Function and privacy
- Accessibility is used only for Android's GLOBAL_ACTION_LOCK_SCREEN.
- SofP does not read screen text, inspect app windows, access node trees, click controls, or monitor other apps.
- Android 9 (API 28)+ is required for the lock-screen action.
- On an explicit lock request, SofP acquires a two-hour maximum partial wake lock. It releases on screen-on, service shutdown, or timeout.
- This requests CPU uptime but cannot override Android/OEM power management or guarantee every media app's playback.

## Payment and banking apps
SofP contains no package list, app-blocking rules, interception, app hiding, overlay, click automation, or payment-specific logic. Payment apps remain launchable by design. Some payment apps may independently warn about any enabled Accessibility service, even one that requests zero events and cannot retrieve window content. SofP cannot control another app's security policy; users can disable the service in Android Settings.

## Build
JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.2, Gradle 8.2.

```sh
gradle lint test assembleDebug assembleRelease
```

Release builds must be signed with the maintainer's long-term release key.
