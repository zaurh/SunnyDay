package com.zaurh.sunnyday.domain.repository

import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val selectedPlace: Flow<Place>
    val recentPlaces: Flow<List<Place>>
    val temperatureUnit: Flow<TemperatureUnit>

    /** Selects [place] and moves it to the top of the recent list. */
    suspend fun selectPlace(place: Place)
    suspend fun removeRecentPlace(place: Place)
    suspend fun setTemperatureUnit(unit: TemperatureUnit)
}
