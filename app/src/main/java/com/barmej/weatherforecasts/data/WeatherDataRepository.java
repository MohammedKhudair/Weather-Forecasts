package com.barmej.weatherforecasts.data;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.barmej.weatherforecasts.data.database.AppDatabase;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherForecast;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.data.network.NetWorkUtils;
import com.barmej.weatherforecasts.data.sync.SyncUtils;
import com.barmej.weatherforecasts.ui.activitys.MainActivity;
import com.barmej.weatherforecasts.utils.AppExecutor;
import com.barmej.weatherforecasts.utils.OpenWeatherDataParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataRepository {

    private static WeatherDataRepository sInstance;
    private static final Object LOCK = new Object();

    NetWorkUtils mNetWorkUtils;

    private Call<WeatherInfo> mWeatherInfoCall;
    private Call<WeatherForecast> mForecastCall;

   private AppDatabase mAppDatabase;

    private MutableLiveData<WeatherInfo> mWeatherInfoMutableLiveData;
    private MutableLiveData<ForecastLists> mForecastListMutableLiveData;

    private AppExecutor mAppExecutor;


    public static WeatherDataRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) sInstance = new WeatherDataRepository(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    private WeatherDataRepository(Context context) {
        mNetWorkUtils = NetWorkUtils.getInstance(context);
        mAppDatabase = AppDatabase.getInstance(context);
        mAppExecutor = AppExecutor.getInstance();
        SyncUtils.scheduleSync(context);
    }

    public void updateWeatherInfo() {
        mWeatherInfoCall = mNetWorkUtils.getApiInterface().getWeatherInfo(mNetWorkUtils.getQueryMap());
        mWeatherInfoCall.enqueue(new Callback<WeatherInfo>() {
            @Override
            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                if (response.code() == 200){
                    final WeatherInfo weatherInfo = response.body();
                    if (weatherInfo != null){
                        mAppExecutor.getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mAppDatabase.weatherInfoDao().addWeatherInfo(weatherInfo);
                            }
                        });
                    }
                }
            }
            @Override
            public void onFailure(Call<WeatherInfo> call, Throwable t) {
                Log.e("WeatherDataRepository",t.getMessage());
            }
        });
    }

    public void updateForecastLists() {
        mForecastCall = mNetWorkUtils.getApiInterface().getForecastInfo(mNetWorkUtils.getQueryMap());
        mForecastCall.enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                if (response.code() == 200){
                    WeatherForecast weatherForecast = response.body();
                    final ForecastLists forecastLists = OpenWeatherDataParser.getForecastsDataFromWeatherForecast(weatherForecast);
                    mAppExecutor.getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mAppDatabase.forecastListsDao().addForecastLists(forecastLists);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                Log.e("WeatherDataRepository",t.getMessage());
            }
        });
    }

    public LiveData<WeatherInfo> getWeatherInfo(){
        return mAppDatabase.weatherInfoDao().getWeatherInfo();
    }

    public LiveData<ForecastLists> getForecastInfo(){
        return mAppDatabase.forecastListsDao().getForecastLists();
    }

    public void cancelDataRequest() {
        if (mWeatherInfoCall != null){
            mWeatherInfoCall.cancel();
            mForecastCall.cancel();
        }
    }
}
