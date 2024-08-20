package com.pack.myweather.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pack.myweather.adapters.CitySearchAdapter
import com.pack.myweather.databinding.BottomSheetSearchBinding

class SearchCityBottomSheet : BottomSheetDialogFragment() {

    interface OnCitySelectedListener {
        fun onCitySelected(cityName: String)
    }

    private var _binding: BottomSheetSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var citySearchAdapter: CitySearchAdapter
    private val viewModel: WeatherViewModel by activityViewModels()
    private var onCitySelectedListener: OnCitySelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Set up the EditText with a TextWatcher
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No-op
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { viewModel.searchCity(it.toString()) }
            }

            override fun afterTextChanged(s: Editable?) {
                // No-op
            }
        })

        viewModel.citySearchResults.observe(viewLifecycleOwner) { cities ->
            cities?.let {
                it.data?.let { cityList -> citySearchAdapter.submitList(cityList) }
            }
        }

        // Set up the click listener for the adapter
        citySearchAdapter.setOnItemClickListener { city ->
            onCitySelectedListener?.onCitySelected(city.name)
            dismiss() // Optionally dismiss the bottom sheet
        }
    }

    private fun setupRecyclerView() {
        citySearchAdapter = CitySearchAdapter()
        binding.rvCitySearch.apply {
            adapter = citySearchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun setOnCitySelectedListener(listener: OnCitySelectedListener) {
        onCitySelectedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
