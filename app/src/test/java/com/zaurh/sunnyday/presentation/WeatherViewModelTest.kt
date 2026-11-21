package com.zaurh.sunnyday.presentation

import com.zaurh.sunnyday.MainDispatcherRule
import com.zaurh.sunnyday.domain.model.DataError
import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.model.TemperatureUnit
import com.zaurh.sunnyday.domain.usecase.GetForecastUseCase
import com.zaurh.sunnyday.fakes.FakeUserPreferencesRepository
import com.zaurh.sunnyday.fakes.FakeWeatherRepository
import com.zaurh.sunnyday.presentation.weather.WeatherEffect
import com.zaurh.sunnyday.presentation.weather.WeatherIntent
import com.zaurh.sunnyday.presentation.weather.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repository = FakeWeatherRepository()
    private val preferences = FakeUserPreferencesRepository()
    private fun viewModel() = WeatherViewModel(GetForecastUseCase(repository), preferences)

    @Test
    fun `loads forecast for the selected place on start`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertEquals("Cracow", state.weather?.location?.name)
        assertEquals(listOf("Cracow"), repository.forecastQueries)
    }

    @Test
    fun `shows blocking error when first load fails, and recovers on retry`() = runTest {
        repository.forecastResult = { DataResult.Error(DataError.NO_INTERNET) }
        val vm = viewModel()
        advanceUntilIdle()
        assertEquals(DataError.NO_INTERNET, vm.state.value.error)
        assertNull(vm.state.value.weather)

        repository.forecastResult = { DataResult.Success(com.zaurh.sunnyday.fakes.sampleWeather()) }
        vm.onIntent(WeatherIntent.Retry)
        advanceUntilIdle()
        assertNull(vm.state.value.error)
        assertNotNull(vm.state.value.weather)
    }

    @Test
    fun `failed refresh keeps data and emits a snackbar effect`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        repository.forecastResult = { DataResult.Error(DataError.SERVER) }
        vm.onIntent(WeatherIntent.Refresh)
        advanceUntilIdle()

        assertNotNull(vm.state.value.weather)
        assertFalse(vm.state.value.isRefreshing)
        assertEquals(WeatherEffect.ShowError(DataError.SERVER), vm.effects.first())
    }

    @Test
    fun `selecting a new place reloads`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        preferences.selectPlace(Place("London", query = "id:1"))
        advanceUntilIdle()

        assertEquals(listOf("Cracow", "id:1"), repository.forecastQueries)
        assertEquals("id:1", vm.state.value.place?.query)
    }

    @Test
    fun `toggle unit persists the opposite unit`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        vm.onIntent(WeatherIntent.ToggleUnit)
        advanceUntilIdle()

        assertEquals(TemperatureUnit.FAHRENHEIT, vm.state.value.unit)
    }

    @Test
    fun `open search emits navigation effect`() = runTest {
        val vm = viewModel()
        vm.onIntent(WeatherIntent.OpenSearch)
        assertEquals(WeatherEffect.NavigateToSearch, vm.effects.first())
    }
}
