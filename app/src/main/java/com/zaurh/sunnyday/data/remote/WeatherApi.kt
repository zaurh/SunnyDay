package com.zaurh.sunnyday.data.remote

import com.zaurh.sunnyday.data.remote.dto.ForecastResponseDto
import com.zaurh.sunnyday.data.remote.dto.SearchResultDto
import retrofit2.http.GET
import retrofit2.http.Query

/** https://www.weatherapi.com/docs/ — the API key is attached by an OkHttp interceptor. */
interface WeatherApi {

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") query: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no",
    ): ForecastResponseDto

    @GET("search.json")
    suspend fun search(@Query("q") query: String): List<SearchResultDto>

    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
    }
}
