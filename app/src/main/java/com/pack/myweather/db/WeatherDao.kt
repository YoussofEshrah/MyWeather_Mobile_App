package com.pack.myweather.db

import androidx.room.*
import com.pack.myweather.models.CityForecast

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(cityForecast: CityForecast)

    @Update
    suspend fun updateForecast(cityForecast: CityForecast)

    @Delete
    suspend fun deleteForecast(cityForecast: CityForecast)

    @Query("SELECT * FROM saved_cities")
    suspend fun getAllForecasts(): List<CityForecast>

    @Query("SELECT * FROM saved_cities WHERE name = :cityName")
    suspend fun getForecastByCity(cityName: String): CityForecast?
}
