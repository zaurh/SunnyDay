package com.example.sunnyday.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sunnyday.R
import com.example.sunnyday.data.remote.dto.Hour

@Composable
fun ForecastItem(
    hourModel: Hour,
) {
    val hour = hourModel.time.substring(11, 13).toInt()

    Card(modifier = Modifier.padding(10.dp)) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = hourModel.time.substring(10))
            Spacer(modifier = Modifier.size(10.dp))

            ConditionImage(
                condition = when (hourModel.condition.text) {
                    "Clear" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_clear
                    } else
                        R.drawable.day_clear

                    "Sunny" -> R.drawable.day_clear
                    "Partly cloudy" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_partial_cloud
                    } else
                        R.drawable.day_partial_cloud

                    "Cloudy" -> R.drawable.cloudy
                    "Overcast" -> R.drawable.angry_clouds
                    "Mist" -> R.drawable.mist
                    "Patchy rain possible" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_rain
                    } else
                        R.drawable.day_rain

                    "Patchy snow possible" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_snow
                    } else
                        R.drawable.day_snow

                    "Patchy sleet possible" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_sleet
                    } else
                        R.drawable.day_sleet

                    "Patchy freezing drizzle possible" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_snow
                    } else
                        R.drawable.day_snow

                    "Thundery outbreaks possible" -> R.drawable.thunder
                    "Blowing snow" -> R.drawable.snow
                    "Blizzard" -> R.drawable.snow
                    "Fog" -> R.drawable.fog
                    "Freezing fog" -> R.drawable.fog
                    "Patchy light drizzle" -> R.drawable.rain
                    "Light drizzle" -> R.drawable.rain
                    "Freezing drizzle" -> R.drawable.snow
                    "Heavy freezing drizzle" -> R.drawable.snow
                    "Patchy light rain" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_rain
                    } else
                        R.drawable.day_rain

                    "Light rain" -> R.drawable.rain
                    "Moderate rain at times" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_rain
                    } else
                        R.drawable.day_rain

                    "Moderate rain" -> R.drawable.rain
                    "Heavy rain at times" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_rain
                    } else
                        R.drawable.day_rain

                    "Heavy rain" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_rain
                    } else
                        R.drawable.day_rain

                    "Light freezing rain" -> R.drawable.snow
                    "Moderate or heavy freezing rain" -> R.drawable.snow
                    "Light sleet" -> R.drawable.day_sleet
                    "Moderate or heavy sleet" -> R.drawable.day_sleet
                    "Patchy light snow" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_snow
                    } else
                        R.drawable.day_snow

                    "Light snow" -> R.drawable.snow
                    "Patchy moderate snow" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_snow
                    } else
                        R.drawable.day_snow

                    "Moderate snow" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_snow
                    } else
                        R.drawable.day_snow

                    "Patchy heavy snow" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_snow
                    } else
                        R.drawable.day_snow

                    "Heavy snow" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_snow
                    } else
                        R.drawable.day_snow

                    "Ice pellets" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_snow
                    } else
                        R.drawable.day_snow

                    "Light rain shower" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_rain
                    } else
                        R.drawable.day_rain

                    "Moderate or heavy rain shower" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_rain
                    } else
                        R.drawable.day_rain

                    "Torrential rain shower" -> R.drawable.rain_thunder
                    "Light sleet showers" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_sleet
                    } else
                        R.drawable.day_sleet

                    "Moderate or heavy sleet showers" -> if (hour in 0..5 || hour in 21..23) {
                        R.drawable.night_half_moon_sleet
                    } else
                        R.drawable.day_sleet

                    "Light snow showers" -> R.drawable.snow
                    "Moderate or heavy snow showers" -> R.drawable.snow
                    "Light showers of ice pellets" -> R.drawable.snow
                    "Moderate or heavy showers of ice pellets" -> R.drawable.snow
                    "Patchy light rain with thunder" -> R.drawable.rain_thunder
                    "Moderate or heavy rain with thunder" -> R.drawable.thunder
                    "Patchy light snow with thunder" -> R.drawable.snow_thunder
                    "Moderate or heavy snow with thunder" -> R.drawable.snow_thunder
                    else -> {
                        R.drawable.day_clear
                    }
                }, modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "${hourModel.temp_c.toInt()}°")
        }
    }

}


