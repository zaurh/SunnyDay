package com.zaurh.sunnyday.data.repository

import com.zaurh.sunnyday.data.mapper.toPlace
import com.zaurh.sunnyday.data.mapper.toWeather
import com.zaurh.sunnyday.data.remote.WeatherApi
import com.zaurh.sunnyday.domain.model.DataError
import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.Weather
import com.zaurh.sunnyday.domain.repository.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
) : WeatherRepository {

    override suspend fun getForecast(query: String): DataResult<Weather> =
        safeCall { api.getForecast(query).toWeather() }

    override suspend fun searchPlaces(query: String): DataResult<List<Place>> =
        safeCall { api.search(query).map { it.toPlace() } }

    private inline fun <T> safeCall(block: () -> T): DataResult<T> = try {
        DataResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        // WeatherAPI answers 400 for "No matching location found".
        DataResult.Error(if (e.code() == 400) DataError.LOCATION_NOT_FOUND else DataError.SERVER)
    } catch (e: IOException) {
        DataResult.Error(DataError.NO_INTERNET)
    } catch (e: SerializationException) {
        DataResult.Error(DataError.SERVER)
    } catch (e: Exception) {
        DataResult.Error(DataError.UNKNOWN)
    }
}
