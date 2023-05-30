package com.example.sunnyday.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sunnyday.R
import com.example.sunnyday.presentation.MainViewModel
import com.example.sunnyday.presentation.components.ConditionImage
import com.example.sunnyday.presentation.components.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val forecast = viewModel.hour.value
    val weather = viewModel.state.value
    val hour = weather.weather?.current?.last_updated?.substring(11, 13)?.toInt()
    val conditionText = weather.weather?.current?.condition?.text
    val focus = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current


    Box(modifier = Modifier.fillMaxSize()) {

        when (hour) {
            in 0..4 -> {
                ConditionImage(condition = R.drawable.night, modifier = Modifier.fillMaxSize())
            }

            in 5..11 -> {
                ConditionImage(condition = R.drawable.morning, modifier = Modifier.fillMaxSize())
            }

            in 12..16 -> {
                ConditionImage(condition = R.drawable.afternoon, modifier = Modifier.fillMaxSize())
            }

            in 17..23 -> {
                ConditionImage(condition = R.drawable.evening, modifier = Modifier.fillMaxSize())
            }

            else ->
                ConditionImage(condition = R.drawable.evening, modifier = Modifier.fillMaxSize())
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextField(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        tint = if (hour in 5..16) {
                            Color.Black
                        } else
                            Color.White
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    placeholderColor = if (hour in 5..16) {
                        Color.Black
                    } else
                        Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color.Transparent,
                    textColor = if (hour in 5..16) {
                        Color.Black
                    } else
                        Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focus),
                value = viewModel.searchQuery.value,
                onValueChange = viewModel::onSearch,
                placeholder = { Text(text = "Enter city...") }
            )
            Spacer(modifier = Modifier.size(10.dp))

            weather.weather?.let {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row() {
                        Text(
                            text = it.location.country, color = if (hour in 5..16) {
                                Color.Gray
                            } else
                                Color.LightGray
                        )
                        Text(
                            text = " - ${it.location.localtime.substring(11)}",
                            color = if (hour in 5..16) {
                                Color.Gray
                            } else
                                Color.LightGray
                        )
                    }


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = it.location.name,
                            fontSize = 30.sp,
                            color = if (hour in 5..16) {
                                Color.Black
                            } else
                                Color.White
                        )

                        Spacer(modifier = Modifier.size(10.dp))
                        ConditionImage(
                            condition = when (conditionText) {
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
                    }
                }


                Text(
                    text = "${it.current.temp_c.toInt()}°",
                    fontSize = 80.sp,
                    color =
                    if (hour in 5..16) {
                        Color.Black
                    } else
                        Color.White
                )
                Text(
                    text = it.current.condition.text,
                    color = if (hour in 5..16) {
                        Color.Gray
                    } else
                        Color.LightGray
                )

                Text(
                    text = "Wind : ${it.current.wind_kph} km/h",
                    color = if (hour in 5..16) {
                        Color.Gray
                    } else
                        Color.LightGray
                )
                Text(
                    text = "Humidity : ${it.current.humidity} %",
                    color = if (hour in 5..16) {
                        Color.Gray
                    } else
                        Color.LightGray
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = formatDate(it.current.last_updated),
                    fontSize = 20.sp,
                    color = if (hour in 5..16) {
                        Color.Black
                    } else
                        Color.White
                )
                LazyRow(modifier = Modifier.size(500.dp)) {
                    items(forecast) { hour ->
                        ForecastItem(hourModel = hour)
                    }
                }
            }


        }




        FloatingActionButton(
            backgroundColor = when (hour) {
                in 0..4 -> {
                    colorResource(id = R.color.night)
                }

                in 5..11 -> {
                    colorResource(id = R.color.morning)
                }

                in 12..16 -> {
                    colorResource(id = R.color.afternoon)
                }

                in 17..23 -> {
                    colorResource(id = R.color.evening)
                }

                else -> {
                    colorResource(id = R.color.statusBar)
                }
            },
            onClick = {
                focus.requestFocus()
                keyboardController?.show()
                viewModel.onSearch("")
            },
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(40.dp)
                .size(70.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "",
                tint = Color.White

            )
        }
    }
}


@Composable
fun formatDate(date: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMMM d", Locale.getDefault())

    val parsedDate = inputFormat.parse(date)
    return outputFormat.format(parsedDate!!)
}