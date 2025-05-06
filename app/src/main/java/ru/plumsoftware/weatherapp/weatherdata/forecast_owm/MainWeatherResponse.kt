package ru.plumsoftware.weatherapp.weatherdata.forecast_owm

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MainWeatherResponse(
    @SerializedName("cod")
    @Expose
    val cod: Int,

    @SerializedName("message")
    @Expose
    val message: Int,

    @SerializedName("cnt")
    @Expose
    val cnt: Int,

    @SerializedName("list")
    @Expose
    val weatherList: List<WeatherItem>,

    @SerializedName("city")
    @Expose
    val city: City
)