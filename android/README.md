# Ruddarr TV (Android TV / Google TV)

Kotlin + Jetpack Compose for TV port of Ruddarr, targeting the Onn 4K streaming device. D-pad only.

Milestone 1: add a Radarr instance, browse the movie library, view movie details.

Build: `gradle :app:assembleDebug` (needs Android SDK, `ANDROID_HOME`), then
`adb connect <tv-ip>` and `adb install app/build/outputs/apk/debug/app-debug.apk`.
