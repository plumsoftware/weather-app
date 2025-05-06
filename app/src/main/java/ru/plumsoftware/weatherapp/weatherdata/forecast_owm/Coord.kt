package ru.plumsoftware.weatherapp.weatherdata.forecast_owm

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lat")
    @Expose
    val lat: Double,

    @SerializedName("lon")
    @Expose
    val lon: Double
)