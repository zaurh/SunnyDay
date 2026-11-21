package com.zaurh.sunnyday.presentation.common

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.zaurh.sunnyday.R
import com.zaurh.sunnyday.domain.model.Weather
import com.zaurh.sunnyday.domain.model.WeatherCondition
import java.time.LocalTime

/** Time of day at the *searched* location, which drives the background illustration and palette. */
enum class DayPeriod { MORNING, AFTERNOON, EVENING, NIGHT }

fun Weather.dayPeriod(): DayPeriod {
    val now = location.localTime.toLocalTime()
    val today = daily.firstOrNull()
    val sunrise = today?.sunrise ?: LocalTime.of(6, 0)
    val sunset = today?.sunset ?: LocalTime.of(19, 0)
    val noon = LocalTime.NOON
    val duskStart = sunset.minusMinutes(45)
    val duskEnd = sunset.plusMinutes(75)
    return when {
        !now.isBefore(duskStart) && now.isBefore(duskEnd) -> DayPeriod.EVENING
        now.isBefore(sunrise) || !now.isBefore(duskEnd) -> DayPeriod.NIGHT
        now.isBefore(noon) -> DayPeriod.MORNING
        else -> DayPeriod.AFTERNOON
    }
}

@DrawableRes
fun DayPeriod.backgroundRes(): Int = when (this) {
    DayPeriod.MORNING -> R.drawable.morning
    DayPeriod.AFTERNOON -> R.drawable.afternoon
    DayPeriod.EVENING -> R.drawable.evening
    DayPeriod.NIGHT -> R.drawable.night
}

/** Colors tuned to sit on top of each background illustration. */
data class WeatherPalette(
    val sky: Color,
    val content: Color,
    val contentSecondary: Color,
    val card: Color,
    val cardBorder: Color,
    val accent: Color,
    val useDarkSystemIcons: Boolean,
)

fun DayPeriod.palette(): WeatherPalette = when (this) {
    DayPeriod.MORNING, DayPeriod.AFTERNOON -> WeatherPalette(
        sky = if (this == DayPeriod.MORNING) Color(0xFFB6E7FF) else Color(0xFF84D3FF),
        content = Color(0xFF0B2233),
        contentSecondary = Color(0xFF0B2233).copy(alpha = 0.68f),
        card = Color.White.copy(alpha = 0.7f),
        cardBorder = Color.White.copy(alpha = 0.7f),
        accent = Color(0xFFF59E0B),
        useDarkSystemIcons = true,
    )
    DayPeriod.EVENING -> WeatherPalette(
        sky = Color(0xFF9493CA),
        content = Color.White,
        contentSecondary = Color.White.copy(alpha = 0.75f),
        card = Color(0xFF26245A).copy(alpha = 0.62f),
        cardBorder = Color.White.copy(alpha = 0.16f),
        accent = Color(0xFFFFC46B),
        useDarkSystemIcons = false,
    )
    DayPeriod.NIGHT -> WeatherPalette(
        sky = Color(0xFF272734),
        content = Color.White,
        contentSecondary = Color.White.copy(alpha = 0.7f),
        card = Color(0xFF15151E).copy(alpha = 0.62f),
        cardBorder = Color.White.copy(alpha = 0.1f),
        accent = Color(0xFFF4D35E),
        useDarkSystemIcons = false,
    )
}

@DrawableRes
fun WeatherCondition.iconRes(isDay: Boolean, fullMoon: Boolean = false): Int {
    fun dayNight(day: Int, halfMoon: Int, fullMoonRes: Int) =
        when {
            isDay -> day
            fullMoon -> fullMoonRes
            else -> halfMoon
        }
    return when (this) {
        WeatherCondition.CLEAR -> dayNight(
            R.drawable.day_clear, R.drawable.night_half_moon_clear, R.drawable.night_full_moon_clear,
        )
        WeatherCondition.PARTLY_CLOUDY -> dayNight(
            R.drawable.day_partial_cloud,
            R.drawable.night_half_moon_partial_cloud,
            R.drawable.night_full_moon_partial_cloud,
        )
        WeatherCondition.CLOUDY -> R.drawable.cloudy
        WeatherCondition.OVERCAST -> R.drawable.overcast
        WeatherCondition.MIST -> R.drawable.mist
        WeatherCondition.FOG -> R.drawable.fog
        WeatherCondition.PATCHY_RAIN -> dayNight(
            R.drawable.day_rain, R.drawable.night_half_moon_rain, R.drawable.night_full_moon_rain,
        )
        WeatherCondition.RAIN -> R.drawable.rain
        WeatherCondition.PATCHY_SNOW -> dayNight(
            R.drawable.day_snow, R.drawable.night_half_moon_snow, R.drawable.night_full_moon_snow,
        )
        WeatherCondition.SNOW -> R.drawable.snow
        WeatherCondition.PATCHY_SLEET -> dayNight(
            R.drawable.day_sleet, R.drawable.night_half_moon_sleet, R.drawable.night_full_moon_sleet,
        )
        WeatherCondition.SLEET -> R.drawable.sleet
        WeatherCondition.THUNDER -> R.drawable.thunder
        WeatherCondition.PATCHY_RAIN_THUNDER -> dayNight(
            R.drawable.day_rain_thunder,
            R.drawable.night_half_moon_rain_thunder,
            R.drawable.night_full_moon_rain_thunder,
        )
        WeatherCondition.RAIN_THUNDER -> R.drawable.rain_thunder
        WeatherCondition.PATCHY_SNOW_THUNDER -> dayNight(
            R.drawable.day_snow_thunder,
            R.drawable.night_half_moon_snow_thunder,
            R.drawable.night_full_moon_snow_thunder,
        )
        WeatherCondition.SNOW_THUNDER -> R.drawable.snow_thunder
    }
}
