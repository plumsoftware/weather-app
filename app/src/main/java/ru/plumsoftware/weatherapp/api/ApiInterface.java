package ru.plumsoftware.weatherapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.plumsoftware.weatherapp.weatherdata.current.CurrentWeather;

public interface ApiInterface {
    @GET("weather")
    Call<CurrentWeather> getCurrentWeather(
            @Query("q") String q,
            @Query("appid") String appid,
            @Query("units") String units,
            @Query("lang") String lang
    );
}
