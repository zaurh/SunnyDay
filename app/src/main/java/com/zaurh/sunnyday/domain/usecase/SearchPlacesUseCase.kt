package com.zaurh.sunnyday.domain.usecase

import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.repository.WeatherRepository
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(
    private val repository: WeatherRepository,
) {
    suspend operator fun invoke(query: String): DataResult<List<Place>> {
        val trimmed = query.trim()
        if (trimmed.length < MIN_QUERY_LENGTH) return DataResult.Success(emptyList())
        return repository.searchPlaces(trimmed)
    }

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}
