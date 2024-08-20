package com.pack.myweather.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pack.myweather.models.Forecastday

class ForecastDayConverter {

    private val gson = Gson()
    private val type = object : TypeToken<List<Forecastday>>() {}.type

    @TypeConverter
    fun fromForecastDays(forecastDays: List<Forecastday>): String {
        return gson.toJson(forecastDays, type)
    }

    @TypeConverter
    fun toForecastDays(forecastDaysString: String): List<Forecastday> {
        return gson.fromJson(forecastDaysString, type) ?: emptyList()
    }
}
