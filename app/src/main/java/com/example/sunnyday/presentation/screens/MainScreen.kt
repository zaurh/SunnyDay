package com.example.sunnyday.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
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
                    Text(
                        text = it.location.country, color = if (hour in 5..16) {
                            Color.Gray
                        } else
                            Color.LightGray
                    )

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
                                "Clear" -> R.drawable.cloudy
                                "Sunny" -> R.drawable.day_clear
                                "Partly cloudy" -> R.drawable.day_partial_cloud
                                "Cloudy" -> R.drawable.cloudy
                                "Overcast" -> R.drawable.angry_clouds
                                "Mist" -> R.drawable.mist
                                "Patchy rain possible" -> R.drawable.day_rain
                                "Patchy snow possible" -> R.drawable.day_snow
                                "Patchy sleet possible" -> R.drawable.day_sleet
                                "Patchy freezing drizzle possible" -> R.drawable.day_snow
                                "Thundery outbreaks possible" -> R.drawable.thunder
                                "Blowing snow" -> R.drawable.snow
                                "Blizzard" -> R.drawable.snow
                                "Fog" -> R.drawable.fog
                                "Freezing fog" -> R.drawable.fog
                                "Patchy light drizzle" -> R.drawable.rain
                                "Light drizzle" -> R.drawable.rain
                                "Freezing drizzle" -> R.drawable.snow
                                "Heavy freezing drizzle" -> R.drawable.snow
                                "Patchy light rain" -> R.drawable.day_rain
                                "Light rain" -> R.drawable.rain
                                "Moderate rain at times" -> R.drawable.day_rain
                                "Moderate rain" -> R.drawable.rain
                                "Heavy rain at times" -> R.drawable.day_rain
                                "Heavy rain" -> R.drawable.day_rain
                                "Light freezing rain" -> R.drawable.snow
                                "Moderate or heavy freezing rain" -> R.drawable.snow
                                "Light sleet" -> R.drawable.day_sleet
                                "Moderate or heavy sleet" -> R.drawable.day_sleet
                                "Patchy light snow" -> R.drawable.day_snow
                                "Light snow" -> R.drawable.snow
                                "Patchy moderate snow" -> R.drawable.day_snow
                                "Moderate snow" -> R.drawable.snow
                                "Patchy heavy snow" -> R.drawable.day_snow
                                "Heavy snow" -> R.drawable.snow
                                "Ice pellets" -> R.drawable.snow
                                "Light rain shower" -> R.drawable.day_rain
                                "Moderate or heavy rain shower" -> R.drawable.day_rain
                                "Torrential rain shower" -> R.drawable.rain_thunder
                                "Light sleet showers" -> R.drawable.day_sleet
                                "Moderate or heavy sleet showers" -> R.drawable.day_sleet
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