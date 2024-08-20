package com.pack.myweather.db


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pack.myweather.models.Hour

class HourConverter {

    @TypeConverter
    fun fromHourList(hours: List<Hour>): String {
        return Gson().toJson(hours)
    }

    @TypeConverter
    fun toHourList(hourString: String): List<Hour> {
        val listType = object : TypeToken<List<Hour>>() {}.type
        return Gson().fromJson(hourString, listType)
    }
}
