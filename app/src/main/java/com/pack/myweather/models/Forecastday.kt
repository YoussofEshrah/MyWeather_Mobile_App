package com.pack.myweather.models

import DayConverter
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName

data class Forecastday(
    @SerializedName
        ("date") val date: String,
    @SerializedName("day")
    val day: Day,
//    @TypeConverters(DayConverter::class)
//    @TypeConverters(HourConverter::class)
    @SerializedName("hour")
    val hours: List<Hour>

)
