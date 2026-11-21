package com.zaurh.sunnyday.domain.repository

import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.Weather

interface WeatherRepository {
    suspend fun getForecast(query: String): DataResult<Weather>
    suspend fun searchPlaces(query: String): DataResult<List<Place>>
}
