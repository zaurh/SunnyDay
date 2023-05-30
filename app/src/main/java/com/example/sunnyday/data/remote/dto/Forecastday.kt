package com.example.sunnyday.data.remote.dto

data class Forecastday(
    val date: String,
    val date_epoch: Double,
    val day: Day,
    val hour: List<Hour>
)