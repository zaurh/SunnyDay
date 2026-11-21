package com.zaurh.sunnyday.domain.usecase

import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.Weather
import com.zaurh.sunnyday.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository,
) {
    suspend operator fun invoke(place: Place): DataResult<Weather> =
        repository.getForecast(place.query)
}
