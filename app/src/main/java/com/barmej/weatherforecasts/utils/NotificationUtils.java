package com.barmej.weatherforecasts.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.format.DateUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.ui.activitys.MainActivity;

public class NotificationUtils {
    private static final String WEATHER_STATUS_CHANNEL_ID = "weather status";
    private static final int WEATHER_NOTIFICATION_ID = 1;
    public static final int SYNC_SERVICE_NOTIFICATION_ID = 2;

    public static void createWeatherStatusNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.weather_notification_channel_name);
            String description = context.getString(R.string.weather_notification_channel_description);
            NotificationChannel channel = new NotificationChannel(
                    WEATHER_STATUS_CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public static void showWeatherStatusNotification(Context context, WeatherInfo weatherInfo) {
        if (weatherInfo == null) return;

        long lastTimeNotification = SharedPreferenceHelper.getElapsedTimeSinceLastNotification(context);
        if (lastTimeNotification < DateUtils.DAY_IN_MILLIS) return;

        String title = context.getString(R.string.app_name);
        String notificationText = context.getString(R.string.format_notification,
                weatherInfo.getWeather().get(0).getDescription(),
                weatherInfo.getMain().getTempMax(),
                weatherInfo.getMain().getTempMin());
        int smallIcon = WeatherUtils.getWeatherIcon(weatherInfo.getWeather().get(0).getIcon());
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), smallIcon);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, WEATHER_STATUS_CHANNEL_ID);
        notificationBuilder.setColor(ContextCompat.getColor(context, R.color.primary));
        notificationBuilder.setSmallIcon(smallIcon);
        notificationBuilder.setLargeIcon(largeIcon);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(notificationText);
        notificationBuilder.setAutoCancel(true);
        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder taskStack = TaskStackBuilder.create(context);
        taskStack.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = taskStack.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(WEATHER_NOTIFICATION_ID, notification);

        SharedPreferenceHelper.saveLastNotificationTime(context,System.currentTimeMillis());
    }

    public static Notification getSyncNotification(Context context){
        String notificationTitle = context.getString(R.string.app_name);
        String notificationText = context.getString(R.string.sync_service_notification_message);

        NotificationCompat.Builder notificationBuilder =new NotificationCompat.Builder(context,WEATHER_STATUS_CHANNEL_ID)
                .setOngoing(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setSmallIcon(R.drawable.ic_launcher_background);
        return notificationBuilder.build();
    }
}

