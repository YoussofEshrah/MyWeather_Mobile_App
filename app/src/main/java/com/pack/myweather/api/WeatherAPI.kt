package com.pack.myweather.api

import com.example.weatherapp.model.CitySearchResult
import com.example.weatherapp.util.Constants.Companion.API_KEY
import com.pack.myweather.models.WeatherResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("key") apiKey: String = API_KEY
    ): Response<WeatherResponse>



    @GET("search.json")
    suspend fun searchCity(
        @Query("q") query: String,
        @Query("key") apiKey: String = API_KEY
    ): Response<List<CitySearchResult>>

}
