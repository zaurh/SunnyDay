package com.zaurh.sunnyday.presentation.weather

import com.zaurh.sunnyday.core.mvi.UiEffect
import com.zaurh.sunnyday.core.mvi.UiIntent
import com.zaurh.sunnyday.core.mvi.UiState
import com.zaurh.sunnyday.domain.model.DataError
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.domain.model.Weather

data class WeatherState(
    val place: Place? = null,
    val weather: Weather? = null,
    val unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    /** Blocking error, shown full-screen only when there is no data to display. */
    val error: DataError? = null,
) : UiState

sealed interface WeatherIntent : UiIntent {
    data object Refresh : WeatherIntent
    data object Retry : WeatherIntent
    data object ScreenResumed : WeatherIntent
    data object ToggleUnit : WeatherIntent
    data object OpenSearch : WeatherIntent
}

sealed interface WeatherEffect : UiEffect {
    data object NavigateToSearch : WeatherEffect
    /** Non-blocking error (e.g. refresh failed while older data is still on screen). */
    data class ShowError(val error: DataError) : WeatherEffect
}
