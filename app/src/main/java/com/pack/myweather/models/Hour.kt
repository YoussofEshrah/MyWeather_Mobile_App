package com.pack.myweather.models

data class Hour(
    val condition: Condition,
    val humidity: Int,
    val temp_c: Double,
    val time: String,
    val wind_kph: Double
)