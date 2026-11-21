package com.zaurh.sunnyday.presentation

import com.zaurh.sunnyday.MainDispatcherRule
import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.usecase.SearchPlacesUseCase
import com.zaurh.sunnyday.fakes.FakeUserPreferencesRepository
import com.zaurh.sunnyday.fakes.FakeWeatherRepository
import com.zaurh.sunnyday.presentation.search.SearchEffect
import com.zaurh.sunnyday.presentation.search.SearchIntent
import com.zaurh.sunnyday.presentation.search.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val london = Place("London", "Greater London", "UK", "id:1")
    private val repository = FakeWeatherRepository().apply {
        searchResult = { DataResult.Success(listOf(london)) }
    }
    private val preferences = FakeUserPreferencesRepository()
    private fun viewModel() = SearchViewModel(SearchPlacesUseCase(repository), preferences)

    @Test
    fun `typing is debounced into a single search`() = runTest {
        val vm = viewModel()
        vm.onIntent(SearchIntent.QueryChanged("Lo"))
        advanceTimeBy(100)
        vm.onIntent(SearchIntent.QueryChanged("Lon"))
        advanceTimeBy(100)
        vm.onIntent(SearchIntent.QueryChanged("Lond"))
        advanceUntilIdle()

        assertEquals(listOf("Lond"), repository.searchQueries)
        assertEquals(listOf(london), vm.state.value.suggestions)
    }

    @Test
    fun `empty results show no-results state`() = runTest {
        repository.searchResult = { DataResult.Success(emptyList()) }
        val vm = viewModel()
        vm.onIntent(SearchIntent.QueryChanged("zzzz"))
        advanceUntilIdle()

        assertTrue(vm.state.value.showNoResults)
    }

    @Test
    fun `selecting a place saves it and closes`() = runTest {
        val vm = viewModel()
        vm.onIntent(SearchIntent.PlaceSelected(london))
        advanceUntilIdle()

        assertEquals(london, preferences.selectedPlace.value)
        assertEquals(listOf(london), vm.state.value.recentPlaces)
        assertEquals(SearchEffect.Close, vm.effects.first())
    }

    @Test
    fun `submit selects the first suggestion`() = runTest {
        val vm = viewModel()
        vm.onIntent(SearchIntent.QueryChanged("Lon"))
        advanceUntilIdle()
        vm.onIntent(SearchIntent.Submit)
        advanceUntilIdle()

        assertEquals(london, preferences.selectedPlace.value)
    }
}
