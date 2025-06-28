package com.barmej.weatherforecasts.viewModel;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.data.sync.SyncUtils;

public class MainViewModel extends AndroidViewModel {
    private WeatherDataRepository mRepository;

    private LiveData<WeatherInfo> mWeatherInfoLiveData;
    private LiveData<ForecastLists> mForecastListsLiveData;

    private Context mContext;
    private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           mWeatherInfoLiveData.observeForever(new Observer<WeatherInfo>() {
               @Override
               public void onChanged(WeatherInfo weatherInfo) {
                   if (weatherInfo == null){
                       SyncUtils.startSync(mContext);
                       Toast.makeText(mContext, "broadcastReceiver started", Toast.LENGTH_SHORT).show();
                   }
               }
           });
        }
    };

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = WeatherDataRepository.getInstance(application);
        mWeatherInfoLiveData = mRepository.getWeatherInfo();
        mForecastListsLiveData = mRepository.getForecastInfo();

        mContext = application.getApplicationContext();
        IntentFilter connectivityFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mConnectivityReceiver,connectivityFilter);
    }

    public LiveData<WeatherInfo> getWeatherInfoLiveData() {
        return mWeatherInfoLiveData;
    }

    public LiveData<ForecastLists> getForecastListsLiveData() {
        return mForecastListsLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.cancelDataRequest();
        mContext.unregisterReceiver(mConnectivityReceiver);
    }
}
