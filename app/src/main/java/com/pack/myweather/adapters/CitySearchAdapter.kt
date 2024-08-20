package com.pack.myweather.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pack.myweather.databinding.ItemCitySearchResultBinding
import com.example.weatherapp.model.CitySearchResult

class CitySearchAdapter : RecyclerView.Adapter<CitySearchAdapter.CitySearchViewHolder>() {

    private val cityList = mutableListOf<CitySearchResult>()

    inner class CitySearchViewHolder(val binding: ItemCitySearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                // Notify listener about the click
                onItemClickListener?.let { it(cityList[adapterPosition]) }
            }
        }
    }

    private var onItemClickListener: ((CitySearchResult) -> Unit)? = null

    fun setOnItemClickListener(listener: (CitySearchResult) -> Unit) {
        Log.d("onItemClick", "search item clicked")
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySearchViewHolder {
        val binding = ItemCitySearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitySearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitySearchViewHolder, position: Int) {
        val city = cityList[position]
        holder.binding.apply {
            tvCityName.text = city.name
        }
        // Item click handling is already set in the ViewHolder's init block
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    fun submitList(list: List<CitySearchResult>) {
        cityList.clear()
        cityList.addAll(list)
        Log.d("submitList", cityList.toString())
        notifyDataSetChanged()
    }
}
