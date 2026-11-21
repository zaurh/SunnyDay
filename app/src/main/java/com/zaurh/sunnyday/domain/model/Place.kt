package com.zaurh.sunnyday.domain.model

import kotlinx.serialization.Serializable

/**
 * A searchable location. [query] is what we send to the API: "id:<n>" for places picked
 * from autocomplete (unambiguous), or a free-text city name.
 */
@Serializable
data class Place(
    val name: String,
    val region: String = "",
    val country: String = "",
    val query: String = name,
) {
    val subtitle: String
        get() = listOf(region, country).filter { it.isNotBlank() }.distinct().joinToString(", ")

    companion object {
        val Default = Place(name = "Cracow", country = "Poland", query = "Cracow")
    }
}
