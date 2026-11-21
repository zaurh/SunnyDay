package com.zaurh.sunnyday.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Only the fields the app uses are declared; the JSON parser ignores the rest.

@Serializable
data class ForecastResponseDto(
    val location: LocationDto,
    val current: CurrentDto,
    val forecast: ForecastDto,
)

@Serializable
data class LocationDto(
    val name: String,
    val region: String = "",
    val country: String = "",
    @SerialName("localtime") val localTime: String,
)

@Serializable
data class ConditionDto(
    val text: String = "",
    val code: Int = 1000,
)

@Serializable
data class CurrentDto(
    @SerialName("last_updated") val lastUpdated: String,
    @SerialName("temp_c") val tempC: Double,
    @SerialName("temp_f") val tempF: Double,
    @SerialName("feelslike_c") val feelsLikeC: Double,
    @SerialName("feelslike_f") val feelsLikeF: Double,
    @SerialName("is_day") val isDay: Int,
    val condition: ConditionDto,
    @SerialName("wind_kph") val windKph: Double,
    @SerialName("wind_mph") val windMph: Double,
    @SerialName("wind_dir") val windDir: String = "",
    @SerialName("pressure_mb") val pressureMb: Double,
    val humidity: Int,
    @SerialName("vis_km") val visKm: Double,
    @SerialName("vis_miles") val visMiles: Double,
    val uv: Double = 0.0,
)

@Serializable
data class ForecastDto(
    @SerialName("forecastday") val forecastDays: List<ForecastDayDto>,
)

@Serializable
data class ForecastDayDto(
    val date: String,
    val day: DayDto,
    val astro: AstroDto? = null,
    val hour: List<HourDto> = emptyList(),
)

@Serializable
data class DayDto(
    @SerialName("maxtemp_c") val maxTempC: Double,
    @SerialName("maxtemp_f") val maxTempF: Double,
    @SerialName("mintemp_c") val minTempC: Double,
    @SerialName("mintemp_f") val minTempF: Double,
    @SerialName("daily_chance_of_rain") val chanceOfRain: Int = 0,
    @SerialName("daily_chance_of_snow") val chanceOfSnow: Int = 0,
    val condition: ConditionDto,
)

@Serializable
data class AstroDto(
    val sunrise: String = "",
    val sunset: String = "",
    @SerialName("moon_illumination") val moonIllumination: Int = 0,
)

@Serializable
data class HourDto(
    val time: String,
    @SerialName("temp_c") val tempC: Double,
    @SerialName("temp_f") val tempF: Double,
    @SerialName("is_day") val isDay: Int,
    val condition: ConditionDto,
    @SerialName("chance_of_rain") val chanceOfRain: Int = 0,
    @SerialName("chance_of_snow") val chanceOfSnow: Int = 0,
)
