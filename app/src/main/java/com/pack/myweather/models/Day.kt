package com.pack.myweather.models

import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("avgtemp_c") val avgtempC: Double,
    @SerializedName("maxwind_kph") val maxwindKph: Double,
    @SerializedName("avghumidity") val avghumidity: Int,
    @SerializedName("condition") val condition: Condition
)
