package com.pack.myweather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pack.myweather.models.CityForecast

@Database(
    entities = [CityForecast::class],
    version = 1)
@TypeConverters(ForecastConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): WeatherDatabase =
            instance ?: synchronized(LOCK) {
                instance ?: createDatabase(context).also { instance = it }
            }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "weather_db.db"
            ).build()
    }
}