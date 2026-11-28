# SofP

SofP has two functions: request Android's **Lock Screen** global action and hold a bounded **PARTIAL_WAKE_LOCK** so the CPU can continue running temporarily after the screen locks.

## Installation and permissions
- Installation itself shows no permission prompt and enables no special access.
- On first use, the user can choose **Enable SofP Accessibility**. Android Accessibility Settings opens; the user must manually enable SofP.
- No Device Admin receiver or policy exists.
- `WAKE_LOCK` is a normal permission automatically granted by Android; it does not display a runtime prompt.
- Accessibility is used only for `GLOBAL_ACTION_LOCK_SCREEN`.
- `canRetrieveWindowContent=false`; SofP does not read text, window contents, node trees, passwords, or other app UI.
- Android 9 (API 28)+ is required for the lock-screen action.

## CPU behavior
On an explicit lock request, SofP acquires a two-hour maximum partial wake lock. It releases on screen-on, service shutdown, or timeout. This requests CPU uptime but cannot override Android/OEM power management or guarantee every media app's playback.

## Payment and banking apps
SofP contains no package list, app-blocking rules, interception, app hiding, overlay, click automation, or payment-specific logic. Payment apps remain launchable by design. Some payment apps may independently warn about any enabled Accessibility service, even one that cannot retrieve window content. SofP cannot control another app's security policy; users may disable the service in Android Settings.

## Build
JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.2, Gradle 8.2.

```sh
gradle lint test assembleDebug assembleRelease
```

Release builds must be signed with the maintainer's long-term release key.
