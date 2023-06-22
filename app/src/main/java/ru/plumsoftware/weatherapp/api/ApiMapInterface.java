package ru.plumsoftware.weatherapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.plumsoftware.weatherapp.weatherdata.forecast.ForecastWeather;

public interface ApiMapInterface {

    @GET("map/temp_new/2/1/1.png")
    Call<ForecastWeather> getForecastWeather(
            @Query("appid") String key
    );

}
