package com.barmej.weatherforecasts.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import com.barmej.weatherforecasts.data.entity.WeatherForecast;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;

import java.util.Map;

public interface OpenWeatherApiInterface {
    String WEATHER_ENDPOINT = "weather";
    String FORECAST_ENDPOINT = "forecast";

    @GET(WEATHER_ENDPOINT)
    Call<WeatherInfo> getWeatherInfo(@QueryMap Map<String, String> queryParams);

    @GET(FORECAST_ENDPOINT)
    Call<WeatherForecast> getForecastInfo(@QueryMap Map<String, String> queryParams);
}
