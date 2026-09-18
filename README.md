# ComposeRemote

A tryout of Google's [`androidx.compose.remote`](https://developer.android.com/jetpack/androidx/releases/compose-remote) ("RemoteCompose") library: a plain Kotlin/JVM **server** builds a UI as a serialized binary document, and an **Android app** fetches it over HTTP and renders it natively with `RemoteComposePlayer`.

```
server/  (Ktor, plain JVM, no Android)          android/ (Jetpack Compose app)
┌─────────────────────────────┐   HTTP GET      ┌─────────────────────────────┐
│ builds a RemoteCompose doc   │  /doc → bytes   │ fetches bytes (OkHttp),      │
│ via androidx.compose.remote  │ ──────────────► │ renders with                 │
│ .creation.dsl.createRcBuffer │                 │ RemoteComposePlayer           │
└─────────────────────────────┘                 └─────────────────────────────┘
```

## Why this is a bit unusual

`androidx.compose.remote` is alpha software (`1.0.0-alpha19` as of writing) and everything used to *build* a document on a plain JVM (`server/`), plus the `RemoteComposePlayer` view class used to *render* one (`android/`), is marked `@RestrictTo(LIBRARY_GROUP)` by Google — i.e. not a supported public API, no stability guarantees, could change or disappear in a future alpha. It works today and is the same machinery Google's own AOSP demos use, but treat this repo as a tryout, not production code.

## Prerequisites

- JDK 17+
- Android SDK with `compileSdk 36` / `platforms;android-36` installed
- An Android emulator or physical device (**minSdk 26** / Android 8.0+)

## 1. Run the server

```bash
cd server
./gradlew run
```

Starts a Ktor server on `http://localhost:8080`. `GET /doc` returns the RemoteCompose document (raw bytes) built in `server/src/main/kotlin/.../Document.kt`. Edit that file and restart to change the UI.

## 2. Run the Android app

```bash
cd android
./gradlew :app:installDebug
```

The app is hardcoded to fetch from `http://10.0.2.2:8080/doc` (`MainActivity.kt`) — `10.0.2.2` is the **Android Emulator's** alias for the host machine's `localhost`, so this works out of the box if you run the app on an emulator on the same machine as the server.

**On a physical device**, either:
- Change `SERVER_URL` in `MainActivity.kt` to your dev machine's LAN IP (`ipconfig getifaddr en0` on macOS), and make sure the device is on the same network, **or**
- Keep `localhost` semantics over USB: `adb reverse tcp:8080 tcp:8080`, then temporarily point `SERVER_URL` at `http://127.0.0.1:8080/doc`.

The client declares `usesCleartextTraffic="true"` for this local-dev HTTP setup — not something you'd ship as-is to production.

## 3. Verify it end-to-end

1. Launch the app — it fetches `/doc` on start and renders it with `RemoteComposePlayer`, filling the real device screen (the layout adapts live to whatever size the View is given; see `Modifier.fillMaxSize()` on the `AndroidView` in `RemoteUiScreen.kt`).
2. Edit `Document.kt` on the server, restart `./gradlew run`, relaunch (or pull-to-refresh, if you add that) the app to see the updated UI.

## Project layout

- `server/src/main/kotlin/.../Document.kt` — builds the document. Also defines `jvmAndroidxProfile`, a hand-assembled JVM-only stand-in for the Android-only `RcPlatformProfiles.ANDROIDX` profile (same API level / operations bitmask, so the wire format matches what the Android player expects).
- `server/src/main/kotlin/.../Main.kt` — the Ktor HTTP server.
- `android/app/src/main/java/.../ui/RemoteUiScreen.kt` — fetches the bytes and renders them via `AndroidView { RemoteComposePlayer(it) }`.
- `android/app/src/main/java/.../MainActivity.kt` — the server URL constant lives here.
