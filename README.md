# Screen Off Play (Minimal AMOLED Screen-Off Overlay)

## Build APK in 1 Minute (No Android Studio Needed)

### Option A: GitHub Actions (Cloud Build on Phone or PC)
1. Create a free private/public GitHub repository and upload the contents of this ZIP.
2. Add a workflow file `.github/workflows/build.yml` (included in this ZIP) or run `./gradlew assembleDebug`.
3. Download the compiled `app-debug.apk` from the **Actions** tab.

### Option B: Local Command Line (Linux / Windows / macOS)
1. Install JDK 17 and Android SDK Build-Tools 34.
2. Run:
   ```bash
   gradle assembleRelease
   ```
3. Sign with `apksigner` and install via `adb install` or copy to your Redmi Note 12.
