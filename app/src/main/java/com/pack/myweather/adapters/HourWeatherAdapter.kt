package com.pack.myweather.ui.adapters

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pack.myweather.R
import com.pack.myweather.models.Hour
import java.util.Locale

class HourWeatherAdapter(private val hours: List<Hour>) : RecyclerView.Adapter<HourWeatherAdapter.HourViewHolder>() {

    inner class HourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHourTemp: TextView = itemView.findViewById(R.id.tvHourTemp)
        val ivHourIcon: ImageView = itemView.findViewById(R.id.ivHourIcon)
        val tvHourTime: TextView = itemView.findViewById(R.id.tvHourTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hour_weather, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val hour = hours[position]
        //time setup
        val currentTimeMillis = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("HH:00", Locale.ENGLISH)
        val curTime = currentTimeMillis+(position * 3600000L)
        val time = timeFormat.format(curTime)


        holder.tvHourTemp.text = "${hour.temp_c}°"
        holder.tvHourTime.text = time
        Glide.with(holder.itemView.context).load("https:${hour.condition.icon}").into(holder.ivHourIcon)
    }

    override fun getItemCount(): Int = hours.size
}
