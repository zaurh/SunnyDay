package com.example.sunnyday.domain.repository

import com.example.sunnyday.common.Resource
import com.example.sunnyday.domain.model.WeatherModel
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeather(q: String): Flow<Resource<WeatherModel>>
}