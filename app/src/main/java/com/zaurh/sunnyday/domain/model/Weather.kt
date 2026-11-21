package com.zaurh.sunnyday.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Weather(
    val location: WeatherLocation,
    val current: CurrentWeather,
    /** Hourly forecast, starting at the current local hour (usually 24 entries). */
    val hourly: List<HourForecast>,
    val daily: List<DayForecast>,
)

data class WeatherLocation(
    val name: String,
    val region: String,
    val country: String,
    val localTime: LocalDateTime,
)

data class CurrentWeather(
    val temperature: Temperature,
    val feelsLike: Temperature,
    val condition: WeatherCondition,
    val conditionText: String,
    val isDay: Boolean,
    val humidityPercent: Int,
    val windKph: Double,
    val windMph: Double,
    val windDirection: String,
    val pressureMb: Double,
    val visibilityKm: Double,
    val visibilityMiles: Double,
    val uvIndex: Double,
    val lastUpdated: LocalDateTime,
)

data class HourForecast(
    val time: LocalDateTime,
    val temperature: Temperature,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val chanceOfPrecipitation: Int,
)

data class DayForecast(
    val date: LocalDate,
    val min: Temperature,
    val max: Temperature,
    val condition: WeatherCondition,
    val chanceOfPrecipitation: Int,
    val sunrise: LocalTime?,
    val sunset: LocalTime?,
    val moonIllumination: Int,
)

data class Temperature(val celsius: Double, val fahrenheit: Double) {
    fun valueIn(unit: TemperatureUnit): Double =
        if (unit == TemperatureUnit.CELSIUS) celsius else fahrenheit
}

enum class TemperatureUnit { CELSIUS, FAHRENHEIT }
