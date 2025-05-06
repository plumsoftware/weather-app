package ru.plumsoftware.weatherapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.plumsoftware.weatherapp.weatherdata.forecast.ForecastWeather;
import ru.plumsoftware.weatherapp.weatherdata.forecast_owm.MainWeatherResponse;

public interface ApiDailyInterface {

    @GET("forecast.json")
    Call<ForecastWeather> getForecastWeather(
            @Query("key") String key,
            @Query("q") String q,
            @Query("days") int days,
            @Query("aqi") String aqi,
            @Query("alerts") String alerts,
            @Query("lang") String lang
    );

    @GET("forecast")
    Call<MainWeatherResponse> getForecastWeatherNew(
            @Query("q") String q,
            @Query("appid") String appid,
            @Query("units") String units,
            @Query("lang") String lang
    );
}
