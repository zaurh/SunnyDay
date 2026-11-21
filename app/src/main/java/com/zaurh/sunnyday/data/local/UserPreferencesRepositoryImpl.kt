package com.zaurh.sunnyday.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) : UserPreferencesRepository {

    private val preferences: Flow<Preferences> = dataStore.data.catch { e ->
        if (e is IOException) emit(emptyPreferences()) else throw e
    }

    override val selectedPlace: Flow<Place> = preferences
        .map { prefs -> prefs[KEY_SELECTED]?.let(::decodePlace) ?: Place.Default }
        .distinctUntilChanged()

    override val recentPlaces: Flow<List<Place>> = preferences
        .map { prefs -> prefs[KEY_RECENT]?.let(::decodePlaces).orEmpty() }
        .distinctUntilChanged()

    override val temperatureUnit: Flow<TemperatureUnit> = preferences
        .map { prefs ->
            prefs[KEY_UNIT]?.let { runCatching { TemperatureUnit.valueOf(it) }.getOrNull() }
                ?: TemperatureUnit.CELSIUS
        }
        .distinctUntilChanged()

    override suspend fun selectPlace(place: Place) {
        dataStore.edit { prefs ->
            val recent = prefs[KEY_RECENT]?.let(::decodePlaces).orEmpty()
            val updated = (listOf(place) + recent.filterNot { it.query == place.query })
                .take(MAX_RECENT)
            prefs[KEY_SELECTED] = json.encodeToString(place)
            prefs[KEY_RECENT] = json.encodeToString(updated)
        }
    }

    override suspend fun removeRecentPlace(place: Place) {
        dataStore.edit { prefs ->
            val recent = prefs[KEY_RECENT]?.let(::decodePlaces).orEmpty()
            prefs[KEY_RECENT] = json.encodeToString(recent.filterNot { it.query == place.query })
        }
    }

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { it[KEY_UNIT] = unit.name }
    }

    private fun decodePlace(raw: String): Place? =
        runCatching { json.decodeFromString<Place>(raw) }.getOrNull()

    private fun decodePlaces(raw: String): List<Place> =
        runCatching { json.decodeFromString<List<Place>>(raw) }.getOrDefault(emptyList())

    private companion object {
        const val MAX_RECENT = 8
        val KEY_SELECTED = stringPreferencesKey("selected_place")
        val KEY_RECENT = stringPreferencesKey("recent_places")
        val KEY_UNIT = stringPreferencesKey("temperature_unit")
    }
}
