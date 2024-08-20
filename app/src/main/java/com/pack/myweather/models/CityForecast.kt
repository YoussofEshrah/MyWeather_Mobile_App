package com.pack.myweather.models

import com.pack.myweather.db.ForecastDayConverter
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pack.myweather.db.ForecastConverter

@Entity(tableName = "saved_cities")
data class CityForecast (
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name : String,
    val lat: Double,
    val lon: Double,
    @TypeConverters(ForecastConverter::class)
    val forecast: Forecast
)