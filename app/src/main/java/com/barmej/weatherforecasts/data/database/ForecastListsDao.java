package com.barmej.weatherforecasts.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;

@Dao
public interface ForecastListsDao {

    @Query("SELECT * From forecasts LIMIT 1")
    LiveData<ForecastLists> getForecastLists();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addForecastLists(ForecastLists forecastLists);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateForecastLists(ForecastLists forecastLists);

    @Delete
    void deleteForecastLists(ForecastLists forecastLists);

}
