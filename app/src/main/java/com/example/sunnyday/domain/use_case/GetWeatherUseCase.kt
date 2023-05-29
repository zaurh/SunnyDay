package com.example.sunnyday.domain.use_case

import com.example.sunnyday.common.Resource
import com.example.sunnyday.domain.model.WeatherModel
import com.example.sunnyday.domain.repository.GeoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: GeoRepository
) {

    suspend operator fun invoke(q: String): Flow<Resource<WeatherModel>>{
        return repository.getWeather(q)
    }
}