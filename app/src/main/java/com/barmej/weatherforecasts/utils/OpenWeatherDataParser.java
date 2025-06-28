package com.barmej.weatherforecasts.utils;

import android.util.Log;

import com.barmej.weatherforecasts.data.entity.Forecast;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.Main;
import com.barmej.weatherforecasts.data.entity.Weather;
import com.barmej.weatherforecasts.data.entity.WeatherForecast;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.data.entity.Wind;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OpenWeatherDataParser {
    private static final String TAG = OpenWeatherDataParser.class.getSimpleName();
    /**
     * Operation status code
     */
    private static final String OWM_MESSAGE_CODE = "cod";
    /**
     * Location information
     */
    private static final String OWM_CITY = "city";
    private static final String OWM_CITY_NAME = "name";
    /**
     * Weather information list
     * Each day's forecast info is an element of the "list" array
     */
    private static final String OWM_LIST = "list";
    /**
     * Date and time
     */
    private static final String OWM_DATE = "dt";
    private static final String OWM_DATE_TEXT = "dt_txt";
    /**
     * Wind information
     */
    private static final String OWM_WIND = "wind";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";
    /**
     * Main weather Information
     */
    private static final String OWM_MAIN = "main";
    private static final String OWM_TEMPERATURE = "temp";
    private static final String OWM_MAX = "temp_max";
    private static final String OWM_MIN = "temp_min";
    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    /**
     * Weather condition information
     */
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_DESCRIPTION = "description";
    private static final String OWM_WEATHER_ICON = "icon";

    private static boolean isError(JSONObject jsonObject) {
        try {
            // Check the response code to see if there is an error
            if (jsonObject.has(OWM_MESSAGE_CODE)) {
                int errorCode = jsonObject.getInt(OWM_MESSAGE_CODE);
                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        return false;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        Log.e(TAG, "Location Invalid");
                    default:
                        Log.e(TAG, "Server probably down");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static WeatherInfo getWeatherInfoObjectFromJson(JSONObject weatherJson) throws JSONException {
        // Check if there is an error in the json
        if(isError(weatherJson )){
            return null;
        }
        JSONObject weatherJsonObject = weatherJson .getJSONArray(OWM_WEATHER).getJSONObject(0);
        JSONObject mainJsonObject = weatherJson .getJSONObject(OWM_MAIN);
        JSONObject windJsonObject = weatherJson .getJSONObject(OWM_WIND);

        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setDt(weatherJson .getLong(OWM_DATE));
        weatherInfo.setName(weatherJson .has(OWM_CITY_NAME) ? weatherJson .getString(OWM_CITY_NAME) : "");

        Main main = new Main();
        main.setTemp(mainJsonObject.getDouble(OWM_TEMPERATURE));
        main.setTempMin(mainJsonObject.getDouble(OWM_MIN));
        main.setTempMax(mainJsonObject.getDouble(OWM_MAX));
        main.setPressure(mainJsonObject.getDouble(OWM_PRESSURE));
        main.setHumidity(mainJsonObject.getDouble(OWM_HUMIDITY));
        weatherInfo.setMain(main);

        Wind wind =new Wind();
        wind.setSpeed(windJsonObject.getDouble(OWM_WINDSPEED));
        wind.setDeg(windJsonObject.getDouble(OWM_WIND_DIRECTION));
        weatherInfo.setWind(wind);

        List<Weather> weatherList = new ArrayList<>();
        Weather weather = new Weather();
        weather.setDescription(weatherJsonObject.getString(OWM_WEATHER_DESCRIPTION));
        weather.setIcon(weatherJsonObject.getString(OWM_WEATHER_ICON));
        weatherList.add(weather);
        weatherInfo.setWeather(weatherList);

        return weatherInfo;
    }

    public static ForecastLists getForecastsDataFromWeatherForecast(WeatherForecast weatherForecast) {
        // Check if there is an error in the json


        List<Forecast> hoursForecasts = new ArrayList<>();
        LinkedHashMap<String,List<Forecast>> daysForecasts = new LinkedHashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH);
        String currentDay = dateFormat.format(new Date());
        int hoursForecastsCount = 0;

        for (int i = 0; i < weatherForecast.getList().size(); i++) {


            Forecast forecast = weatherForecast.getList().get(i);


            if(hoursForecastsCount++ < 8){
                hoursForecasts.add(forecast);
            }

     String date = forecast.getDtTxt().split(" ")[0];

            if (!date.equals(currentDay)) {
                if (daysForecasts.containsKey(date)) {
                    List<Forecast> forecasts = daysForecasts.get(date);
                    assert forecasts != null;
                    forecasts.add(forecast);
                } else {
                    List<Forecast> forecasts = new ArrayList<>();
                    forecasts.add(forecast);
                    daysForecasts.put(date, forecasts);
                }
            }

        }

        ForecastLists forecastsData = new ForecastLists();
        forecastsData.setHoursForecasts(hoursForecasts);
        List<List<Forecast>> listOfDaysForecasts = new ArrayList<>();
        for (Map.Entry entry : daysForecasts.entrySet()) {
            listOfDaysForecasts.add((List<Forecast>) entry.getValue());
        }
        forecastsData.setDaysForecasts(listOfDaysForecasts);
        return forecastsData;
    }


}
