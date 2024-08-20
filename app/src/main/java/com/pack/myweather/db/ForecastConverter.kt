package com.pack.myweather.db


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pack.myweather.models.Forecast

class ForecastConverter {
    @TypeConverter
    fun fromForecast(forecast: Forecast): String {
        return Gson().toJson(forecast)
    }

    @TypeConverter
    fun toForecast(json: String): Forecast {
        return Gson().fromJson(json, Forecast::class.java)
    }
}

