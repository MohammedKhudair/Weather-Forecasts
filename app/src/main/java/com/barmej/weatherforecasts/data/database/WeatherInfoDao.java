package com.barmej.weatherforecasts.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.barmej.weatherforecasts.data.entity.WeatherInfo;

@Dao
public interface WeatherInfoDao {

    @Query("SELECT * From weather_info LIMIT 1")
    LiveData<WeatherInfo> getWeatherInfo();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addWeatherInfo(WeatherInfo weatherInfo);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWeatherInfo(WeatherInfo weatherInfo);

    @Delete
    void deleteWeatherInfo(WeatherInfo weatherInfo);

}
