package com.example.sunnyday.domain.model

import com.example.sunnyday.data.remote.dto.Current
import com.example.sunnyday.data.remote.dto.Location

data class WeatherModel(
    val current: Current,
    val location: Location
)
