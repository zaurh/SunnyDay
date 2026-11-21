package com.zaurh.sunnyday.presentation.weather

import androidx.lifecycle.viewModelScope
import com.zaurh.sunnyday.core.mvi.MviViewModel
import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.domain.repository.UserPreferencesRepository
import com.zaurh.sunnyday.domain.usecase.GetForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getForecast: GetForecastUseCase,
    private val preferences: UserPreferencesRepository,
) : MviViewModel<WeatherState, WeatherIntent, WeatherEffect>(WeatherState()) {

    private var loadJob: Job? = null
    private var lastLoadedAt = 0L

    init {
        preferences.selectedPlace
            .onEach { place -> load(place, mode = LoadMode.NEW_PLACE) }
            .launchIn(viewModelScope)

        preferences.temperatureUnit
            .onEach { unit -> setState { copy(unit = unit) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: WeatherIntent) {
        when (intent) {
            WeatherIntent.Refresh -> currentState.place?.let { load(it, LoadMode.REFRESH) }
            WeatherIntent.Retry -> currentState.place?.let { load(it, LoadMode.INITIAL) }
            WeatherIntent.ScreenResumed -> refreshIfStale()
            WeatherIntent.ToggleUnit -> toggleUnit()
            WeatherIntent.OpenSearch -> sendEffect(WeatherEffect.NavigateToSearch)
        }
    }

    private fun refreshIfStale() {
        val place = currentState.place ?: return
        val isBusy = currentState.isLoading || currentState.isRefreshing
        if (!isBusy && currentState.weather != null && System.currentTimeMillis() - lastLoadedAt > STALE_AFTER_MS) {
            load(place, LoadMode.REFRESH)
        }
    }

    private fun toggleUnit() {
        viewModelScope.launch {
            val next = when (preferences.temperatureUnit.first()) {
                TemperatureUnit.CELSIUS -> TemperatureUnit.FAHRENHEIT
                TemperatureUnit.FAHRENHEIT -> TemperatureUnit.CELSIUS
            }
            preferences.setTemperatureUnit(next)
        }
    }

    private fun load(place: Place, mode: LoadMode) {
        loadJob?.cancel()
        setState {
            when (mode) {
                LoadMode.REFRESH -> copy(isRefreshing = true)
                LoadMode.INITIAL -> copy(place = place, isLoading = true, error = null)
                LoadMode.NEW_PLACE -> copy(
                    place = place,
                    weather = null,
                    isLoading = true,
                    isRefreshing = false,
                    error = null,
                )
            }
        }
        loadJob = viewModelScope.launch {
            when (val result = getForecast(place)) {
                is DataResult.Success -> {
                    lastLoadedAt = System.currentTimeMillis()
                    setState {
                        copy(
                            weather = result.data,
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                        )
                    }
                }
                is DataResult.Error -> {
                    if (currentState.weather != null) {
                        setState { copy(isLoading = false, isRefreshing = false) }
                        sendEffect(WeatherEffect.ShowError(result.error))
                    } else {
                        setState {
                            copy(isLoading = false, isRefreshing = false, error = result.error)
                        }
                    }
                }
            }
        }
    }

    private enum class LoadMode { INITIAL, NEW_PLACE, REFRESH }

    private companion object {
        const val STALE_AFTER_MS = 15 * 60 * 1000L
    }
}
