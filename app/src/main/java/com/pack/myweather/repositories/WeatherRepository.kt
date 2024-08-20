import androidx.lifecycle.LiveData
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.model.CitySearchResult
import com.pack.myweather.db.WeatherDatabase
import com.pack.myweather.models.CityForecast
import com.pack.myweather.models.WeatherResponse
import retrofit2.Response

class WeatherRepository(
    private val db: WeatherDatabase
) {
    // Fetch weather data from the API
    suspend fun getCityWeather(location: String, days: Int): Response<WeatherResponse> {
        return RetrofitInstance.api.getForecast(location = location, days = days)
    }

//     Save the forecast data to the database
    suspend fun saveForecast(weatherResponse: WeatherResponse) {
        val cityForecast = CityForecast(
            name = weatherResponse.location.name,
            lat = weatherResponse.location.lat,
            lon = weatherResponse.location.lon,
            forecast = weatherResponse.forecast
        )
        db.weatherDao().insertForecast(cityForecast)
    }

    suspend fun getSavedForecast(cityName: String): CityForecast? {
        return db.weatherDao().getForecastByCity(cityName)
    }
//
//    // Optionally, get all saved forecasts
    suspend fun getAllSavedForecasts(): List<CityForecast> {
        return db.weatherDao().getAllForecasts()
    }

    suspend fun searchCity(query: String): Response<List<CitySearchResult>> {
        return RetrofitInstance.api.searchCity(query)
    }
}
