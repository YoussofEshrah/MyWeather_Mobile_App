package com.pack.myweather.ui

import WeatherRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.CitySearchResult
import com.pack.myweather.models.CityForecast
import com.pack.myweather.models.WeatherResponse
import com.pack.myweather.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _weatherData: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    val weatherData: LiveData<Resource<WeatherResponse>> = _weatherData
    private val _citySearchResults: MutableLiveData<Resource<List<CitySearchResult>>> = MutableLiveData()
    val citySearchResults: LiveData<Resource<List<CitySearchResult>>> = _citySearchResults

//    val savedForecasts: List<ForecastEntity> = weatherRepository.getAllSavedForecasts()

    fun getWeather(location: String, days: Int = 5) = viewModelScope.launch {
        _weatherData.postValue(Resource.Loading())
        val response = weatherRepository.getCityWeather(location, days)
        _weatherData.postValue(handleWeatherResponse(response))
    }


    fun getAndSaveWeather(location: String, days: Int = 5) = viewModelScope.launch {
        _weatherData.postValue(Resource.Loading())
        val response = weatherRepository.getCityWeather(location, days)
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                // Save the forecast data into the database
                weatherRepository.saveForecast(resultResponse)
            }
        }
        _weatherData.postValue(handleWeatherResponse(response))
    }

    fun getSavedForecast(cityName: String): LiveData<CityForecast?> = MutableLiveData<CityForecast?>().apply {
        viewModelScope.launch {
            value = weatherRepository.getSavedForecast(cityName)
        }
    }

    fun saveForecast(weatherResponse: WeatherResponse) = viewModelScope.launch {
        weatherRepository.saveForecast(weatherResponse)
    }

    fun getAllSavedForecasts(): LiveData<List<CityForecast>> {
        return MutableLiveData<List<CityForecast>>().apply {
            viewModelScope.launch {
                value = weatherRepository.getAllSavedForecasts()
            }
        }
    }

    private fun handleWeatherResponse(response: Response<WeatherResponse>): Resource<WeatherResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                // Save the forecast data into the database
                viewModelScope.launch {
//                    weatherRepository.saveForecast(resultResponse)
                }
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleCitySearchResponse(response: Response<List<CitySearchResult>>): Resource<List<CitySearchResult>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun searchCity(query: String) = viewModelScope.launch {
        _citySearchResults.postValue(Resource.Loading())
        val response = weatherRepository.searchCity(query)
        _citySearchResults.postValue(handleCitySearchResponse(response))
    }
}
