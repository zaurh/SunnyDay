package com.zaurh.sunnyday.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    val id: Long = 0,
    val name: String,
    val region: String = "",
    val country: String = "",
)
