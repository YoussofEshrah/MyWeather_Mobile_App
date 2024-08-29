package com.pack.myweather.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pack.myweather.R
import com.pack.myweather.adapters.SavedCitiesAdapter
import com.pack.myweather.databinding.FragmentSavedCitiesBinding
import com.pack.myweather.ui.SearchCityBottomSheet
import com.pack.myweather.ui.WeatherActivity
import com.pack.myweather.ui.WeatherViewModel

class SavedCitiesFragment : Fragment(), SearchCityBottomSheet.OnCitySelectedListener {

    private lateinit var viewModel: WeatherViewModel
    private var _binding: FragmentSavedCitiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var citiesAdapter: SavedCitiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as WeatherActivity).viewModel
        setupRecyclerView()

        binding.fabAddCity.setOnClickListener {
            val searchCityBottomSheet = SearchCityBottomSheet()
            searchCityBottomSheet.setOnCitySelectedListener(this)
            searchCityBottomSheet.show(parentFragmentManager, searchCityBottomSheet.tag)
        }

        binding.btnBack.setOnClickListener {
            val homeFragment = HomeFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, homeFragment)
                .addToBackStack(null)
                .commit()
        }

        viewModel.getAllSavedForecasts().observe(viewLifecycleOwner) { forecasts ->
            citiesAdapter.submitList(forecasts)
        }
    }

    private fun setupRecyclerView() {
        citiesAdapter = SavedCitiesAdapter(requireActivity())
        binding.rvSavedCities.apply {
            adapter = citiesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onCitySelected(cityName: String) {
        viewModel.getAndSaveWeather(cityName)
        viewModel.weatherData.observe(viewLifecycleOwner) { resource ->
            resource.data?.let { weatherResponse ->
                loadData()
            }
        }

    }

    private fun loadData() {
        viewModel.getAllSavedForecasts().observe(viewLifecycleOwner) { forecasts ->
            citiesAdapter.submitList(null)
            citiesAdapter.submitList(forecasts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

