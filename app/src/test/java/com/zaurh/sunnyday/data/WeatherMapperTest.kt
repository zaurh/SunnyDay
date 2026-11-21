package com.zaurh.sunnyday.data

import com.zaurh.sunnyday.data.mapper.toPlace
import com.zaurh.sunnyday.data.mapper.toWeather
import com.zaurh.sunnyday.data.remote.dto.AstroDto
import com.zaurh.sunnyday.data.remote.dto.ConditionDto
import com.zaurh.sunnyday.data.remote.dto.CurrentDto
import com.zaurh.sunnyday.data.remote.dto.DayDto
import com.zaurh.sunnyday.data.remote.dto.ForecastDayDto
import com.zaurh.sunnyday.data.remote.dto.ForecastDto
import com.zaurh.sunnyday.data.remote.dto.ForecastResponseDto
import com.zaurh.sunnyday.data.remote.dto.HourDto
import com.zaurh.sunnyday.data.remote.dto.LocationDto
import com.zaurh.sunnyday.data.remote.dto.SearchResultDto
import com.zaurh.sunnyday.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class WeatherMapperTest {

    private fun hours(date: String) = (0..23).map { h ->
        HourDto(
            time = "$date ${"%02d".format(h)}:00",
            tempC = h.toDouble(), tempF = 0.0, isDay = 1,
            condition = ConditionDto("Clear", 1000),
            chanceOfRain = 10, chanceOfSnow = 40,
        )
    }

    private fun day(date: String, sunrise: String = "07:03 AM") = ForecastDayDto(
        date = date,
        day = DayDto(6.0, 42.0, 1.0, 34.0, chanceOfRain = 30, chanceOfSnow = 0, condition = ConditionDto("Sunny", 1000)),
        astro = AstroDto(sunrise = sunrise, sunset = "03:49 PM", moonIllumination = 95),
        hour = hours(date),
    )

    private val dto = ForecastResponseDto(
        // Non-zero-padded hour, exactly as WeatherAPI sends it.
        location = LocationDto("Cracow", "", "Poland", "2026-11-21 9:06"),
        current = CurrentDto(
            lastUpdated = "2026-11-21 09:00", tempC = 4.8, tempF = 40.6, feelsLikeC = 0.5,
            feelsLikeF = 32.9, isDay = 1, condition = ConditionDto("Partly Cloudy", 1003),
            windKph = 22.0, windMph = 13.6, windDir = "W", pressureMb = 1014.0,
            humidity = 61, visKm = 10.0, visMiles = 6.0, uv = 0.4,
        ),
        forecast = ForecastDto(listOf(day("2026-11-21"), day("2026-11-22", sunrise = "No sunrise"))),
    )

    @Test
    fun `hourly forecast starts at the current local hour and spans 24 hours`() {
        val weather = dto.toWeather()
        assertEquals(24, weather.hourly.size)
        assertEquals(LocalDateTime.of(2026, 11, 21, 9, 0), weather.hourly.first().time)
        assertEquals(LocalDateTime.of(2026, 11, 22, 8, 0), weather.hourly.last().time)
    }

    @Test
    fun `condition is mapped from code, not localized text`() {
        assertEquals(WeatherCondition.PARTLY_CLOUDY, dto.toWeather().current.condition)
    }

    @Test
    fun `astro times are parsed and invalid values become null`() {
        val daily = dto.toWeather().daily
        assertEquals(LocalTime.of(7, 3), daily[0].sunrise)
        assertEquals(LocalTime.of(15, 49), daily[0].sunset)
        assertNull(daily[1].sunrise)
    }

    @Test
    fun `chance of precipitation takes the max of rain and snow`() {
        val weather = dto.toWeather()
        assertEquals(40, weather.hourly.first().chanceOfPrecipitation)
        assertEquals(30, weather.daily.first().chanceOfPrecipitation)
    }

    @Test
    fun `search results use id query when available`() {
        assertEquals("id:42", SearchResultDto(42, "London", "", "UK").toPlace().query)
        assertEquals("London", SearchResultDto(0, "London", "", "UK").toPlace().query)
    }

    @Test
    fun `unknown condition codes fall back to cloudy`() {
        assertEquals(WeatherCondition.CLOUDY, WeatherCondition.fromCode(9999))
        assertEquals(WeatherCondition.SNOW_THUNDER, WeatherCondition.fromCode(1282))
    }
}
