package com.barmej.weatherforecasts.data.database;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.barmej.weatherforecasts.data.converter.DaysForecastsConverter;
import com.barmej.weatherforecasts.data.converter.HoursForecastsConverter;
import com.barmej.weatherforecasts.data.converter.WeatherListConverter;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;

@Database(entities = {WeatherInfo.class, ForecastLists.class}, version = 2, exportSchema = false)
@TypeConverters({WeatherListConverter.class, HoursForecastsConverter.class, DaysForecastsConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;
    private static final String DATABASE_NAME = "weather_db";

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return sInstance;
    }

    public abstract WeatherInfoDao weatherInfoDao();
    public abstract ForecastListsDao forecastListsDao();


}
