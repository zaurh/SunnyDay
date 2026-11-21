package com.zaurh.sunnyday.domain.model

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error(val error: DataError) : DataResult<Nothing>
}

enum class DataError {
    NO_INTERNET,
    LOCATION_NOT_FOUND,
    SERVER,
    UNKNOWN,
}
