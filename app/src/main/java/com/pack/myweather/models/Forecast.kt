package com.pack.myweather.models

import androidx.room.*
import com.pack.myweather.db.ForecastDayConverter

data class Forecast(
//    @TypeConverters(ForecastDayConverter::class)
    val forecastday: List<Forecastday>
)