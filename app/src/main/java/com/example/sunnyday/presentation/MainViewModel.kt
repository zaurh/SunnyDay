package com.example.sunnyday.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunnyday.common.Resource
import com.example.sunnyday.data.remote.dto.Hour
import com.example.sunnyday.domain.use_case.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherUseCase: GetWeatherUseCase
) : ViewModel() {

    val hour = mutableStateOf<List<Hour>>(emptyList())

    private val _state = mutableStateOf(MainState())
    val state: State<MainState> = _state

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private var searchJob: Job? = null

    init {
        onSearch("Cracow")
    }

    fun onSearch(q: String) {
        _searchQuery.value = q
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            weatherUseCase(q).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = MainState(weather = result.data)
                        for (i in result.data?.forecast?.forecastday!!) {
                            hour.value = i.hour
                        }
                    }

                    is Resource.Error -> {
                    }

                    is Resource.Loading -> {
                    }
                }
            }.launchIn(this)
        }
    }
}