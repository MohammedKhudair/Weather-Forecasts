package com.barmej.weatherforecasts.data.sync;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.utils.AppExecutor;
import com.barmej.weatherforecasts.utils.NotificationUtils;

public class SyncIntentService  extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SyncIntentService() {
        super(SyncIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = NotificationUtils.getSyncNotification(this);
        startForeground(NotificationUtils.SYNC_SERVICE_NOTIFICATION_ID,notification);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


       // Toast.makeText(this, "Serves started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        final WeatherDataRepository repository = WeatherDataRepository.getInstance(this);
        repository.updateWeatherInfo();
        repository.updateForecastLists();
        AppExecutor.getInstance().getMainThread().execute(new Runnable() {
            @Override
            public void run() {
                repository.getWeatherInfo().observeForever(new Observer<WeatherInfo>() {
                    @Override
                    public void onChanged(WeatherInfo weatherInfo) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopSelf();
                            }
                        }, 5000);
                    }
                });
            }
        });
        return START_STICKY;
    }

}
