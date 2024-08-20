package com.pack.myweather.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pack.myweather.R
import com.pack.myweather.databinding.ItemSavedCityBinding
import com.pack.myweather.models.CityForecast
import com.pack.myweather.ui.fragments.HomeFragment

class SavedCitiesAdapter(private val activity: FragmentActivity) : ListAdapter<CityForecast, SavedCitiesAdapter.SavedCityViewHolder>(CityDiffCallback()) {

    inner class SavedCityViewHolder(val binding: ItemSavedCityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCityViewHolder {
        val binding = ItemSavedCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedCityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedCityViewHolder, position: Int) {
        val cityForecast = getItem(position)
        holder.binding.apply {
            tvCityName.text = cityForecast.name

            val forecastDay = cityForecast.forecast.forecastday[0].day
            tvTodayTemp.text = "${forecastDay.avgtempC}°"
            tvWind.text = "Wind | ${forecastDay.maxwindKph} km/h"
            tvHumidity.text = "Hum | ${forecastDay.avghumidity} %"
            tvTodayDescription.text = forecastDay.condition.text

            val iconUrl = "https:${forecastDay.condition.icon}"
            Glide.with(ivWeatherImage.context).load(iconUrl).into(ivWeatherImage)
        }

        holder.itemView.setOnClickListener {
            val cityName = cityForecast.name
            val homeFragment = HomeFragment().apply {
                arguments = Bundle().apply {
                    putString("CITY_ARG", cityName)
                }
            }
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, homeFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    class CityDiffCallback : DiffUtil.ItemCallback<CityForecast>() {
        override fun areItemsTheSame(oldItem: CityForecast, newItem: CityForecast): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CityForecast, newItem: CityForecast): Boolean {
            return oldItem == newItem
        }
    }
}
