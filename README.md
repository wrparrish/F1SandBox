# F1 Sandbox

A Formula 1 Android app I built to explore modern Android architecture patterns. It pulls race schedules and driver info from the Ergast and OpenF1 APIs.

## What's in here

- **Race Schedule** - Browse the current F1 season's races, tap one to see results
- **Drivers** - Current driver roster with team info and headshots
- **Settings** - Theme toggle (because every app needs dark mode)

## Architecture

This was my playground for implementing patterns I've been reading about:

- **UDF (Unidirectional Data Flow)** - DataState → StateProvider → ViewState. Keeps the ViewModel logic testable and the UI dumb.
- **Domain-driven modules** - Organized by feature domain (`race/`, `driver/`, `settings/`) rather than layer. Each domain has its own API, database, store, and UI modules.
- **Store pattern** - Single source of truth with `stream*()` for reactive data and `refresh*()` for network fetches. Data always flows Network → DB → UI.
- **Room + Flow** - Local caching so the app doesn't hammer the API on every screen visit

## Tech Stack

- Kotlin
- Jetpack Compose
- Navigation Compose 2.8+ (type-safe routes)
- Hilt for DI
- Room for persistence
- Retrofit + Moshi for networking
- Coil for images

## Building

```bash
./gradlew assembleDebug
```

Or just open in Android Studio and hit run.

## APIs

- [Ergast F1 API](http://ergast.com/mrd/) - Race results and schedule data
- [OpenF1 API](https://openf1.org/) - Driver session data

## Notes

This is a learning project, not a production app. There are rough edges - error handling could be better, tests are minimal, and I'm sure there are race conditions lurking somewhere. But it works, and I learned a lot building it.
