# F1 Sandbox

An Android app for browsing Formula 1 race schedules, results, and driver information.

## Features

- **Race Schedule** - Current season races with tap-through to results
- **Drivers** - Driver roster with team info and headshots
- **Settings** - Theme toggle

## Architecture

- **UDF (Unidirectional Data Flow)** - DataState → StateProvider → ViewState
- **Domain-driven modules** - Organized by feature (`race/`, `driver/`, `settings/`), each with its own API, database, store, and UI layers
- **Store pattern** - Data flows Network → Room → UI with reactive streams
- **Lifecycle-aware observation** - `repeatOnLifecycle(STARTED)` for proper collection

## Tech Stack

- Kotlin
- Jetpack Compose
- Navigation Compose 2.8+ (type-safe routes)
- Hilt
- Room
- Retrofit + Moshi
- Coil

## Build

```bash
./gradlew assembleDebug
```

## APIs

- [Ergast F1 API](http://ergast.com/mrd/) - Race data
- [OpenF1 API](https://openf1.org/) - Driver data
