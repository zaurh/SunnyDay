package com.zaurh.sunnyday.fakes

import com.zaurh.sunnyday.domain.model.CurrentWeather
import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.Temperature
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.domain.model.Weather
import com.zaurh.sunnyday.domain.model.WeatherCondition
import com.zaurh.sunnyday.domain.model.WeatherLocation
import com.zaurh.sunnyday.domain.repository.UserPreferencesRepository
import com.zaurh.sunnyday.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

class FakeWeatherRepository : WeatherRepository {
    var forecastResult: (String) -> DataResult<Weather> = { DataResult.Success(sampleWeather(it)) }
    var searchResult: (String) -> DataResult<List<Place>> = { DataResult.Success(emptyList()) }
    val forecastQueries = mutableListOf<String>()
    val searchQueries = mutableListOf<String>()

    override suspend fun getForecast(query: String): DataResult<Weather> {
        forecastQueries += query
        return forecastResult(query)
    }

    override suspend fun searchPlaces(query: String): DataResult<List<Place>> {
        searchQueries += query
        return searchResult(query)
    }
}

class FakeUserPreferencesRepository(initial: Place = Place.Default) : UserPreferencesRepository {
    override val selectedPlace: StateFlow<Place> get() = _selected
    override val recentPlaces: StateFlow<List<Place>> get() = _recent
    override val temperatureUnit: StateFlow<TemperatureUnit> get() = _unit

    private val _selected = MutableStateFlow(initial)
    private val _recent = MutableStateFlow(emptyList<Place>())
    private val _unit = MutableStateFlow(TemperatureUnit.CELSIUS)

    override suspend fun selectPlace(place: Place) {
        _selected.value = place
        _recent.value = listOf(place) + _recent.value.filterNot { it.query == place.query }
    }

    override suspend fun removeRecentPlace(place: Place) {
        _recent.value = _recent.value.filterNot { it.query == place.query }
    }

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        _unit.value = unit
    }
}

fun sampleWeather(name: String = "Cracow") = Weather(
    location = WeatherLocation(name, "", "Poland", LocalDateTime.of(2026, 11, 21, 15, 6)),
    current = CurrentWeather(
        temperature = Temperature(4.8, 40.6),
        feelsLike = Temperature(0.5, 32.9),
        condition = WeatherCondition.PARTLY_CLOUDY,
        conditionText = "Partly Cloudy",
        isDay = true,
        humidityPercent = 61,
        windKph = 22.0,
        windMph = 13.6,
        windDirection = "W",
        pressureMb = 1014.0,
        visibilityKm = 10.0,
        visibilityMiles = 6.0,
        uvIndex = 0.4,
        lastUpdated = LocalDateTime.of(2026, 11, 21, 15, 0),
    ),
    hourly = emptyList(),
    daily = emptyList(),
)
