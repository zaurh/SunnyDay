SunnyDay is a weather app: search any city and see current conditions, a 24-hour forecast,
a 3-day forecast and details like UV, wind, pressure, sunrise and sunset.

Weather data: [WeatherAPI.com](https://www.weatherapi.com/)

# Preview

![App Screenshot](https://i.hizliresim.com/bt8cwqf.png)

# Features

- Illustrated backgrounds that follow the time of day at the searched city (morning, afternoon, dusk, night)
- City search with autocomplete and recent searches
- Hourly (24h) and 3-day forecast with a temperature range bar
- Details grid: feels like, humidity, wind, UV index, pressure, visibility, sunrise, sunset
- °C / °F toggle (remembered)
- Pull to refresh, auto-refresh when data is stale, offline and error states
- Edge-to-edge Material 3 UI, Android 12+ splash screen

# Architecture: MVI + Clean Architecture

```
UI ──Intent──▶ ViewModel ──reduce──▶ State ──▶ UI
                   └──────Effect (one-shot: navigation, snackbar)──▶ UI
```

- `core/mvi/MviViewModel` – a small base class: one `StateFlow<State>`, one `Flow<Effect>`, and a single `onIntent()` entry point
- Each screen has a `*Contract.kt` file (`State`, `Intent`, `Effect`), a `*ViewModel`, and a stateless `*Screen` composable
- `domain` – models, repository interfaces, use cases (pure Kotlin)
- `data` – Retrofit API + DTOs, mappers, DataStore preferences, repository implementations
- `di` – Hilt modules

```
com.example.sunnyday
├── core/mvi           MviViewModel, UiState/UiIntent/UiEffect
├── data               remote (Retrofit), local (DataStore), mapper, repository
├── domain             model, repository, usecase
├── di                 NetworkModule, DataModule
├── presentation
│   ├── weather        WeatherContract, WeatherViewModel, WeatherScreen, components/
│   ├── search         SearchContract, SearchViewModel, SearchScreen
│   ├── navigation     type-safe Navigation Compose
│   └── common         visuals (icons/backgrounds/palette), formatters, helpers
└── ui/theme           Material 3 theme
```

# Tech stack

Kotlin 2.2 · Jetpack Compose (Material 3) · Coroutines & Flow · Hilt (KSP) · Retrofit + OkHttp ·
kotlinx.serialization · DataStore · Navigation Compose (type-safe) · SplashScreen API · JUnit + coroutines-test

# Building

1. Get a free API key from [weatherapi.com](https://www.weatherapi.com/).
2. Add it to `local.properties` (this file is git-ignored):
   ```
   WEATHER_API_KEY=your_key_here
   ```
   or set the `WEATHER_API_KEY` environment variable (useful on CI).
3. `./gradlew assembleDebug`, or `./gradlew bundleRelease` for a Play Store bundle.

Run unit tests with `./gradlew testDebugUnitTest`.
