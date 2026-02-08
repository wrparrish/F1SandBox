# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture Rules

**Reference patterns in `.claude/rules/`** - These documents contain enterprise architecture patterns that inform this project's design:

| Document | Purpose | This Project's Implementation |
|----------|---------|------------------------------|
| `ARCHITECTURE_BLUEPRINT` | Master reference for all patterns | Applied with Gradle instead of Bazel |
| `01-module-organization` | Module structure and naming | Domain-driven: race/, driver/, settings/ |
| `02-network-layer` | Store pattern, Endpoint/Query abstractions | Simplified Store with Room + Retrofit |
| `03-navigation` | Resolver-based navigation | Type-safe @Serializable routes (Nav 2.8+) |
| `04-udf-pattern` | DataState → StateProvider → ViewState | Fully implemented in core/common |
| `05-testing-patterns` | StoreTest, StateViewModelTest, BaseDaoTest | ViewModel tests with TestLifecycleOwner |
| `06-dependency-injection` | Anvil + Hilt, provisions modules | Hilt only (no Anvil) |
| `07-file-templates` | Copy-paste templates for new features | Adapted for this project's structure |
| `08-common-recipes` | Loading states, forms, pagination, dialogs | Pull-to-refresh, error handling |
| `09-dependency-cheatsheet` | Bazel dependencies quick reference | N/A - using Gradle version catalog |
| `10-preflight-checklist` | PR verification checklist | Build + test verification |

**For interview preparation, see `INTERVIEW_PREP_GUIDE.md`** - Comprehensive guide covering all architectural decisions with trade-off analysis.

**When developing features:**
- Check `.claude/feature-docs/` for in-progress work and context
- Add new documents outlining execution plans and progress


## Build Commands

```bash
./gradlew build                  # Full build
./gradlew assembleDebug          # Debug APK
./gradlew test                   # Unit tests
./gradlew connectedAndroidTest   # Instrumentation tests
```

## Current Project Structure

F1 Sandbox is a multi-module Android app using Jetpack Compose, Hilt, and domain-driven architecture.

```
app/                           # Entry point, MainActivity, bottom navigation

core/
  ├── common/                  # UDF framework (BaseStateViewModel, StateProvider, ViewModelBundle)
  ├── common-test/             # Test utilities (TestLifecycleOwner, DataStateHolder)
  ├── database/                # Centralized Room database, DAOs provided via Hilt
  ├── navigation/              # SharedViewModel for cross-feature state
  └── ui/                      # Theme, shared composables (Loading, Error, singleClickable)

race/                          # Race domain
  ├── contracts/               # Navigation routes (@Serializable RaceGraph, RaceHomeScreen, etc.)
  ├── lib-models-race/         # Domain models (Race, RaceResult, RaceWithResults)
  ├── lib-models-race-api/     # Network DTOs (RaceDto, ResultDto) with Moshi annotations
  ├── lib-models-race-fixtures/# Test fixtures (createRace(), createRaceList())
  ├── lib-api-race/            # Retrofit API interface + Hilt module
  ├── lib-db-race/             # Room entities (RaceEntity) and DAOs
  ├── lib-store-race/          # Store interface (RaceStore)
  ├── lib-store-race-impl/     # Store implementation + mappers + Hilt binding
  ├── feature-home/            # Home screen (race list) with UDF ViewModel
  └── feature-results/         # Race results detail screen

driver/                        # Driver domain
  ├── contracts/               # Navigation routes (@Serializable DriverGraph, etc.)
  ├── lib-models-driver/       # Domain models (Driver)
  ├── lib-models-driver-api/   # Network DTOs (DriverDto)
  ├── lib-models-driver-fixtures/# Test fixtures
  ├── lib-api-driver/          # Retrofit API interface
  ├── lib-db-driver/           # Room entities and DAOs
  ├── lib-store-driver/        # Store interface
  ├── lib-store-driver-impl/   # Store implementation
  └── feature-drivers/         # Drivers list screen

settings/                      # Settings domain
  ├── contracts/               # Navigation routes
  └── feature-settings/        # Settings screen
```

## Key Patterns (Summary)

### UDF State Management

Features should follow Unidirectional Data Flow:
- **DataState**: Internal state with raw data, nullable for loading, derived properties for business logic
- **ViewState**: Pure UI-ready data, no derived properties
- **StateProvider**: Transforms DataState → ViewState via `reduce()`
- **applyMutation { copy(...) }**: State updates
- **repeatOnLifecycle(LifecycleState.STARTED)**: Lifecycle-aware store observation

### Data Flow

```
Network API → Store (Endpoint) → Database (DAO) → Query → ViewModel → UI
```

Single source of truth: data flows Network → DB → UI. Store methods:
- `stream*()` - Reactive database queries (Flow)
- `refresh*()` - Background network fetch
- `fetch*()` - Direct network call

### Module Dependencies

UI depends on interfaces, never implementations:
```
feature-* → lib-store-* (interface)
lib-store-*-impl → lib-api-*, lib-db
lib-store-provisions binds impl → interface
```

## External APIs

- OpenF1: `https://api.openf1.org/v1/` (drivers)
- Ergast: `https://api.jolpi.ca/ergast/f1/` (race results)

## Testing

Uses Mockk, Turbine, and `UnconfinedTestDispatcher`. See `05-testing-patterns` in rules for base classes and patterns.
