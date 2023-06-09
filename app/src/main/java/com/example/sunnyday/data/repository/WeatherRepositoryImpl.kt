package com.example.sunnyday.data.repository

import com.example.sunnyday.common.Resource
import com.example.sunnyday.data.mapper.toWeatherModel
import com.example.sunnyday.data.remote.WeatherApi
import com.example.sunnyday.domain.model.WeatherModel
import com.example.sunnyday.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeather(q: String): Flow<Resource<WeatherModel>> = flow {
        try {
            emit(Resource.Loading())
            val remoteData = api.getGeo(q)
            emit(Resource.Success(remoteData.toWeatherModel()))
        } catch (e: HttpException) {
            emit(
                Resource.Error(
                    message = "Oops, something went wrong!"
                )
            )
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    message = "Couldn't reach server, check your internet connection"
                )
            )
        }
    }

}