package com.example.sunnyday.data.remote

import com.example.sunnyday.common.Constants.API_KEY
import com.example.sunnyday.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApi {
    @GET("current.json")
    suspend fun getGeo(
        @Query("q") q: String,
        @Query("lang") lang: String = "en",
        @Query("key") key: String = API_KEY
    ): WeatherDto
}