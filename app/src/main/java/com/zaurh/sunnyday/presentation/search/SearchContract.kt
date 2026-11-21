package com.zaurh.sunnyday.presentation.search

import com.zaurh.sunnyday.core.mvi.UiEffect
import com.zaurh.sunnyday.core.mvi.UiIntent
import com.zaurh.sunnyday.core.mvi.UiState
import com.zaurh.sunnyday.domain.model.DataError
import com.zaurh.sunnyday.domain.model.Place

data class SearchState(
    val query: String = "",
    val suggestions: List<Place> = emptyList(),
    val recentPlaces: List<Place> = emptyList(),
    val isSearching: Boolean = false,
    /** The query [suggestions] belong to; lets us tell "no results" apart from "not searched yet". */
    val searchedQuery: String? = null,
    val error: DataError? = null,
) : UiState {
    val showRecent: Boolean get() = query.isBlank()
    val showNoResults: Boolean
        get() = !isSearching && error == null && suggestions.isEmpty() &&
            searchedQuery != null && searchedQuery == query.trim()
}

sealed interface SearchIntent : UiIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data object ClearQuery : SearchIntent
    data object Submit : SearchIntent
    data class PlaceSelected(val place: Place) : SearchIntent
    data class RemoveRecent(val place: Place) : SearchIntent
    data object Back : SearchIntent
}

sealed interface SearchEffect : UiEffect {
    data object Close : SearchEffect
}
