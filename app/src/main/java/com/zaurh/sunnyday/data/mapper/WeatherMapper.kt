package com.zaurh.sunnyday.data.mapper

import com.zaurh.sunnyday.data.remote.dto.ForecastResponseDto
import com.zaurh.sunnyday.data.remote.dto.HourDto
import com.zaurh.sunnyday.data.remote.dto.SearchResultDto
import com.zaurh.sunnyday.domain.model.CurrentWeather
import com.zaurh.sunnyday.domain.model.DayForecast
import com.zaurh.sunnyday.domain.model.HourForecast
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.Temperature
import com.zaurh.sunnyday.domain.model.Weather
import com.zaurh.sunnyday.domain.model.WeatherCondition
import com.zaurh.sunnyday.domain.model.WeatherLocation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// WeatherAPI uses "2026-11-21 9:05" (hour is not zero-padded), so parse leniently.
private val apiDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm", Locale.US)
private val apiTime = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

private const val HOURS_TO_SHOW = 24

internal fun parseApiDateTime(value: String): LocalDateTime =
    LocalDateTime.parse(value.trim(), apiDateTime)

/** Returns null for values like "No sunrise" (polar day/night). */
internal fun parseApiTime(value: String): LocalTime? =
    runCatching { LocalTime.parse(value.trim().uppercase(Locale.US), apiTime) }.getOrNull()

fun ForecastResponseDto.toWeather(): Weather {
    val localTime = parseApiDateTime(location.localTime)
    val currentHour = localTime.truncatedTo(ChronoUnit.HOURS)

    return Weather(
        location = WeatherLocation(
            name = location.name,
            region = location.region,
            country = location.country,
            localTime = localTime,
        ),
        current = CurrentWeather(
            temperature = Temperature(current.tempC, current.tempF),
            feelsLike = Temperature(current.feelsLikeC, current.feelsLikeF),
            condition = WeatherCondition.fromCode(current.condition.code),
            conditionText = current.condition.text,
            isDay = current.isDay == 1,
            humidityPercent = current.humidity,
            windKph = current.windKph,
            windMph = current.windMph,
            windDirection = current.windDir,
            pressureMb = current.pressureMb,
            visibilityKm = current.visKm,
            visibilityMiles = current.visMiles,
            uvIndex = current.uv,
            lastUpdated = parseApiDateTime(current.lastUpdated),
        ),
        hourly = forecast.forecastDays
            .flatMap { it.hour }
            .map { it.toHourForecast() }
            .filter { !it.time.isBefore(currentHour) }
            .take(HOURS_TO_SHOW),
        daily = forecast.forecastDays.map { day ->
            DayForecast(
                date = LocalDate.parse(day.date),
                min = Temperature(day.day.minTempC, day.day.minTempF),
                max = Temperature(day.day.maxTempC, day.day.maxTempF),
                condition = WeatherCondition.fromCode(day.day.condition.code),
                chanceOfPrecipitation = maxOf(day.day.chanceOfRain, day.day.chanceOfSnow),
                sunrise = day.astro?.sunrise?.let(::parseApiTime),
                sunset = day.astro?.sunset?.let(::parseApiTime),
                moonIllumination = day.astro?.moonIllumination ?: 0,
            )
        },
    )
}

private fun HourDto.toHourForecast() = HourForecast(
    time = parseApiDateTime(time),
    temperature = Temperature(tempC, tempF),
    condition = WeatherCondition.fromCode(condition.code),
    isDay = isDay == 1,
    chanceOfPrecipitation = maxOf(chanceOfRain, chanceOfSnow),
)

fun SearchResultDto.toPlace() = Place(
    name = name,
    region = region,
    country = country,
    query = if (id > 0) "id:$id" else name,
)
