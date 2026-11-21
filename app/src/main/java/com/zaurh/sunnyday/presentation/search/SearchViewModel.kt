package com.zaurh.sunnyday.presentation.search

import androidx.lifecycle.viewModelScope
import com.zaurh.sunnyday.core.mvi.MviViewModel
import com.zaurh.sunnyday.domain.model.DataResult
import com.zaurh.sunnyday.domain.model.Place
import com.zaurh.sunnyday.domain.repository.UserPreferencesRepository
import com.zaurh.sunnyday.domain.usecase.SearchPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchPlaces: SearchPlacesUseCase,
    private val preferences: UserPreferencesRepository,
) : MviViewModel<SearchState, SearchIntent, SearchEffect>(SearchState()) {

    private val queries = MutableStateFlow("")

    init {
        preferences.recentPlaces
            .onEach { recent -> setState { copy(recentPlaces = recent) } }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            queries
                .map { it.trim() }
                .distinctUntilChanged()
                .debounce(DEBOUNCE_MS)
                .collectLatest(::search)
        }
    }

    override fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> onQueryChanged(intent.query)
            SearchIntent.ClearQuery -> onQueryChanged("")
            SearchIntent.Submit -> currentState.suggestions.firstOrNull()?.let(::select)
            is SearchIntent.PlaceSelected -> select(intent.place)
            is SearchIntent.RemoveRecent -> viewModelScope.launch {
                preferences.removeRecentPlace(intent.place)
            }
            SearchIntent.Back -> sendEffect(SearchEffect.Close)
        }
    }

    private fun onQueryChanged(query: String) {
        setState { copy(query = query) }
        queries.value = query
    }

    private suspend fun search(query: String) {
        if (query.length < SearchPlacesUseCase.MIN_QUERY_LENGTH) {
            setState { copy(suggestions = emptyList(), isSearching = false, searchedQuery = null, error = null) }
            return
        }
        setState { copy(isSearching = true, error = null) }
        when (val result = searchPlaces(query)) {
            is DataResult.Success -> setState {
                copy(suggestions = result.data, isSearching = false, searchedQuery = query)
            }
            is DataResult.Error -> setState {
                copy(suggestions = emptyList(), isSearching = false, searchedQuery = query, error = result.error)
            }
        }
    }

    private fun select(place: Place) {
        viewModelScope.launch {
            preferences.selectPlace(place)
            sendEffect(SearchEffect.Close)
        }
    }

    private companion object {
        const val DEBOUNCE_MS = 300L
    }
}
