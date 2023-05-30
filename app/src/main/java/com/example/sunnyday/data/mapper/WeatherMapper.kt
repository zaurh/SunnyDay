package com.example.sunnyday.data.mapper

import com.example.sunnyday.data.remote.dto.WeatherDto
import com.example.sunnyday.domain.model.WeatherModel


fun WeatherDto.toWeatherModel() = WeatherModel(current, location, forecast)