package com.example.sunnyday.presentation

import com.example.sunnyday.domain.model.WeatherModel

data class MainState(
    val weather: WeatherModel? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)
