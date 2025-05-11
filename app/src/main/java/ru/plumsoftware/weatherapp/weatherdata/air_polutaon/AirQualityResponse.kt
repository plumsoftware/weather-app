package ru.plumsoftware.weatherapp.weatherdata.air_polutaon

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AirQualityResponse(
    @SerializedName("coord")
    @Expose
    val coord: Coord,

    @SerializedName("list")
    @Expose
    val list: List<AirQualityItem>
)