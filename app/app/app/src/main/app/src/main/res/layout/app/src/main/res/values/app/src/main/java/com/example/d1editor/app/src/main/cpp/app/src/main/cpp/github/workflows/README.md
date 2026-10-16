# Android build instructions

This project adds a minimal Android Studio app skeleton that compiles the current C++ character editor sources for `arm64-v8a` and `armeabi-v7a`.

## Build locally

1. Open Android Studio and choose `Open`.
2. Select this repository root.
3. Let Gradle sync.
4. Build: `Build > Build APK` or run from terminal:

```bash
./gradlew assembleDebug
