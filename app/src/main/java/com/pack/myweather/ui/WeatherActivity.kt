package com.pack.myweather.ui

import WeatherRepository
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.pack.myweather.R
import com.pack.myweather.databinding.ActivityWeatherBinding
import com.pack.myweather.db.WeatherDatabase
import com.pack.myweather.ui.fragments.HomeFragment

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
    lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Use the companion object to get the instance
        val weatherRepository = WeatherRepository(WeatherDatabase(this))
        val viewModelProviderFactory = WeatherViewModelProviderFactory(weatherRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(WeatherViewModel::class.java)



        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.flFragment, homeFragment)
            .commit()
    }
}
