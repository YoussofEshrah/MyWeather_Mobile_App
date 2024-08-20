package com.pack.myweather.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.pack.myweather.R
import com.pack.myweather.databinding.FragmentHomeBinding
import com.pack.myweather.models.Forecastday
import com.pack.myweather.models.Hour
import com.pack.myweather.ui.WeatherActivity
import com.pack.myweather.ui.WeatherViewModel
import com.pack.myweather.ui.adapters.HourWeatherAdapter
import com.pack.myweather.util.Resource
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment() {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: WeatherViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment"
    val sharedPref = context?.getSharedPreferences("cityPref", Context.MODE_PRIVATE)
    val editor = sharedPref?.edit()

    //    lateinit var defaultCity : String
    lateinit var city: String

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as WeatherActivity).viewModel
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())



        val cityArg = arguments?.getString("CITY_ARG")
        if (cityArg != null) {
            city = cityArg
            Log.d("HomeFragement-city", "getting $city forecast")
            setupWeather()
        } else {
            Log.d("HomeFragement-city", "getting current location")
            //setup weather is called inside this method too
            getCurrentLocation()
        }

        binding.btnSavedCities.setOnClickListener{
            val savedCitiesFragment = SavedCitiesFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, savedCitiesFragment)
                .commit()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupWeather() {

//        defaultCity = sharedPref?.getString("defaultCity", "Cairo").toString()
        binding.tvCityNameHeader.text = city
        viewModel.getWeather(city, 5)
        viewModel.weatherData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { weatherResponse ->
                        val forecastDays = weatherResponse.forecast.forecastday
                        if (forecastDays.isNotEmpty()) {
//                            Log.d("defCity", "defCity: "+defaultCity)

                            //current time weather setup
                            weatherResponse.location.localtime
                            setupCurrentWeather(forecastDays,weatherResponse.location.localtime)

                            //today hours weather setup
                            setupHoursWeather(forecastDays)

                            //setup upcoming days weather
                            setupDaysWeather(forecastDays)

                        } else {
                            Log.e(TAG, "Forecast days list is null or empty")
                        }
                    } ?: run {
                        Log.e(TAG, "Weather response data is null")
                    }
                }

                is Resource.Error -> {
                    val errorMessage = resource.message ?: "Unknown error"
                    Log.e(TAG, "Failed to fetch weather data: $errorMessage")
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch weather data",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> {
                    // Handle loading state
                }
            }
        }
    }


    private fun setupCurrentWeather(forecastDays: List<Forecastday>,localTime: String) {
        val currentTimeMillis = System.currentTimeMillis()
//        val timeFormat = SimpleDateFormat("HH:00", Locale.ENGLISH)
//        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:00", Locale.ENGLISH)
        val date = SimpleDateFormat("dd MMMM", Locale.ENGLISH).format(currentTimeMillis)
        binding.tvHeader.text = "Today, $date"

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:00", Locale.ENGLISH)
            val localParsedTime = inputFormat.parse(localTime)
            val localFormattedTime = outputFormat.format(localParsedTime)

        val todayForecast = forecastDays[0]
        val hoursIndex = getHoursIndex(localFormattedTime, todayForecast.hours)
        Log.d("current date time", localTime)
        Log.d("hours index", hoursIndex.toString())
        binding.tvTodayTemp.text = "${todayForecast.hours[hoursIndex].temp_c}°C"
        binding.tvWind.text = "Wind | ${todayForecast.hours[hoursIndex].wind_kph} kph"
        binding.tvHumidity.text = "Hum | ${todayForecast.hours[hoursIndex].humidity} %"
        binding.tvTodayDescription.text = "${todayForecast.hours[hoursIndex].condition.text}"
        val iconUrl = "https:${todayForecast.hours[hoursIndex].condition.icon}"
        Glide.with(this).load(iconUrl).into(binding.ivWeatherImage)
    }

//    private fun setupHoursWeather(forecastDays: List<Forecastday>) {
//        val currentTimeMillis = System.currentTimeMillis()
//        val dateFormat = SimpleDateFormat("HH:00", Locale.ENGLISH)
//        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:00", Locale.ENGLISH)
//        val todayHours = forecastDays[0].hours
//
//        //setting up time alone
//        for (i in 1..4) { // Adjust the range based on the number of hours you want to display
//            val timeToAdd = 2 * i * 3600000L // 3600000L is 1 hour in milliseconds
//            val futureTimeMillis = currentTimeMillis + timeToAdd
//            val futureTime = dateFormat.format(futureTimeMillis)
//            val futureDateTime = dateTimeFormat.format(futureTimeMillis)
//            var hoursIndex = 0
//            when (i) {
//                1 -> {
//                    hoursIndex = getHoursIndex(futureDateTime, todayHours)
//                    binding.tvHour1Temp.text = todayHours[hoursIndex].temp_c.toString()
//                    binding.Hour1Time.text = futureTime
//                    val iconUrl = "https:${todayHours[hoursIndex].condition.icon}"
//                    Glide.with(this).load(iconUrl).into(binding.ivHour1Icon)
//                }
//
//                2 -> {
//                    hoursIndex = getHoursIndex(futureDateTime, todayHours)
//                    binding.tvHour2Temp.text = todayHours[hoursIndex].temp_c.toString()
//                    binding.Hour2Time.text = futureTime
//                    val iconUrl = "https:${todayHours[hoursIndex].condition.icon}"
//                    Glide.with(this).load(iconUrl).into(binding.ivHour2Icon)
//                }
//
//                3 -> {
//                    hoursIndex = getHoursIndex(futureDateTime, todayHours)
//                    binding.tvHour3Temp.text = todayHours[hoursIndex].temp_c.toString()
//                    binding.Hour3Time.text = futureTime
//                    val iconUrl = "https:${todayHours[hoursIndex].condition.icon}"
//                    Glide.with(this).load(iconUrl).into(binding.ivHour3Icon)
//                }
//
//                4 -> {
//                    hoursIndex = getHoursIndex(futureDateTime, todayHours)
//                    binding.tvHour4Temp.text = todayHours[hoursIndex].temp_c.toString()
//                    binding.Hour4Time.text = futureTime
//                    val iconUrl = "https:${todayHours[hoursIndex].condition.icon}"
//                    Glide.with(this).load(iconUrl).into(binding.ivHour4Icon)
//                }
//            }
//        }
//    }

    private fun setupHoursWeather(forecastDays: List<Forecastday>) {
        val todayHours = forecastDays[0].hours
        val rvHourlyWeather = binding.rvHourlyWeather
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MMMM,dd", Locale.ENGLISH)
        binding.tvTodayDate.text =dateFormat.format(currentTimeMillis)
        rvHourlyWeather.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvHourlyWeather.adapter = HourWeatherAdapter(todayHours)
    }

    private fun setupDaysWeather(forecastDays: List<Forecastday>) {
        val dateStrFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
        val currentTimeMillis = System.currentTimeMillis()

        for (i in 0..4) {
            val timeToAdd = i * 86400000L // 86400000L is 1 day in milliseconds
            val futureTimeMillis = currentTimeMillis + timeToAdd
            val futureDate = dateStrFormat.format(futureTimeMillis)
            var todayForecast = forecastDays[i]
//            val dayIndex = getDaysIndex(futureDate, forecastDays)

            when (i) {
                1 -> {
                    binding.tvDay1Temp.text = todayForecast.day.avgtempC.toString()
                    binding.ivDay1Time.text = futureDate
                    val iconUrl = "https:${todayForecast.day.condition.icon}"
                    Glide.with(this).load(iconUrl).into(binding.ivDay1Icon)
                }

                2 -> {
                    binding.tvDay2Temp.text = todayForecast.day.avgtempC.toString()
                    binding.ivDay2Time.text = futureDate
                    val iconUrl = "https:${todayForecast.day.condition.icon}"
                    Glide.with(this).load(iconUrl).into(binding.ivDay2Icon)
                }

                3 -> {
                    binding.tvDay3Temp.text = todayForecast.day.avgtempC.toString()
                    binding.ivDay3Time.text = futureDate
                    val iconUrl = "https:${todayForecast.day.condition.icon}"
                    Glide.with(this).load(iconUrl).into(binding.ivDay3Icon)
                }

                4 -> {
                    binding.tvDay4Temp.text = todayForecast.day.avgtempC.toString()
                    binding.ivDay4Time.text = futureDate
                    val iconUrl = "https:${todayForecast.day.condition.icon}"
                    Glide.with(this).load(iconUrl).into(binding.ivDay4Icon)
                }
            }
        }
    }



    private fun getHoursIndex(time: String, hours: List<Hour>): Int {
//        Log.d("getHoursIndex", "hours size: "+hours.size)

        for (i in 0..23) {
//            Log.d("getHoursIndex comparison", "hours time: "+hours[i].time+" dateTime: "+time)
            if (hours[i].time == time) {
                return i
            }
        }
        return 404
    }

//    private fun savePreferences(){
//        editor?.apply {
//            putString("defaultCity", defaultCity)
//        }
//    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(
                requireActivity(),
                OnSuccessListener { location ->
                    location?.let {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address>? =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        // Check if addresses is not null and not empty
                        if (!addresses.isNullOrEmpty()) {
                            val cityName = addresses[0].locality // Get the city name
                            city = cityName ?: "Unknown" // Handle null city name
                            setupWeather()
                        } else {
                            Log.e(TAG, "No addresses found")
                            Toast.makeText(
                                requireContext(),
                                "Unable to retrieve city name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } ?: run {
                        Log.e(TAG, "Location is null")
                        Toast.makeText(
                            requireContext(),
                            "Unable to retrieve location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }


}


//        viewModel.fetchSavedCity(defaultCity)
//        viewModel.savedCity.observe(viewLifecycleOwner) { city ->
//            city?.let {
//                this.city = it
//                // Perform any additional setup with the city
//                Log.d("msg", "City fetched: ${it.name}")
//            }
//        }