# CatBreedsKMP (Android + Desktop) — Technical Challenge

Cross‑platform Cat Breeds app built with Kotlin Multiplatform (KMP) for **Android + JVM Desktop**.

## Tech stack (as requested)
- Kotlin Multiplatform (Android + JVM Desktop)
- Compose Multiplatform (UI)
- Ktor Client (HTTP)
- Koin (DI)
- SQLDelight (local persistence)
- kotlinx.serialization (JSON)
- Coroutines / Flow (async + reactive)

## Features
- **Login**: username/password validation, simulated auth, session persistence (token stored locally).
- **Breed List**: paginated list (page size 20), pull‑to‑refresh, offline-first (shows cached data if offline).
- **Details**: large image, description, metadata (origin, temperament, life span), favorite toggle.
- **Favorites**: dedicated screen, persisted between sessions.
- **Tests**: unit tests covering domain + data layers.

## TheCatAPI key (optional but recommended)
This project will attempt to call `https://api.thecatapi.com/v1/breeds`.
If you have an API key, set it as either:
- Environment variable: `CAT_API_KEY=...`
- JVM system property: `-DCAT_API_KEY=...`

If not provided, the request often still works but may be rate limited.

## How to run

### Android
1. Open the project in **Android Studio**.
2. Select the `androidApp` run configuration.
3. Run on emulator or device.

### Desktop (JVM)
From terminal at project root:
```bash
./gradlew :desktopApp:run
```

### Run tests
```bash
./gradlew :shared:test
```

## Architecture
Clean Architecture separation:
- `shared/domain`: entities, repository interfaces, use cases
- `shared/data`: Ktor API, SQLDelight DB, repository implementations (offline-first)
- `shared/presentation`: ViewModels (StateFlow)
- `shared/ui`: Compose UI shared across Android/Desktop
- `androidApp`, `desktopApp`: platform launchers + Koin init

## Notes / trade-offs
- Pagination is implemented as a simple `page + limit` strategy with "Load more" and pull-to-refresh.
- Offline mode: cached data is always shown first; if network refresh fails, UI shows an "Offline" banner.
- Session token is persisted in the local DB for portability across Android/Desktop.
