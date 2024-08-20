package com.pack.myweather.models

data class WeatherResponse(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)