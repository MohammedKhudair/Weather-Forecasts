package com.barmej.weatherforecasts;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.ui.activitys.MainActivity;
import com.barmej.weatherforecasts.utils.CustomDateUtils;
import com.barmej.weatherforecasts.utils.WeatherUtils;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int widgetWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int widgetHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);


        // Construct the RemoteViews object
        final RemoteViews views;

         if (widgetHeight > 120 && widgetWidth > 180) {
            views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_larg);

        } else if (widgetWidth > 120) {
            views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_medium);

        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_small);
        }

        WeatherDataRepository repository = WeatherDataRepository.getInstance(context);
        LiveData<WeatherInfo> weatherInfoLiveData = repository.getWeatherInfo();

        weatherInfoLiveData.observeForever(new Observer<WeatherInfo>() {
            @Override
            public void onChanged(WeatherInfo weatherInfo) {
                String temperature = context.getString(R.string.format_temperature, weatherInfo.getMain().getTemp());
                views.setTextViewText(R.id.appwidget_text, temperature);

                int weatherImageId = WeatherUtils.getWeatherIcon(weatherInfo.getWeather().get(0).getIcon());
                views.setImageViewResource(R.id.appwidget_image_weather_icon, weatherImageId);

                String date = CustomDateUtils.getFriendlyDateString(context,weatherInfo.getDt(),false);
                views.setTextViewText(R.id.appWidget_date,date);

                String cityName = weatherInfo.getName();
                views.setTextViewText(R.id.appWidget_city_name,cityName);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appWidgetContainer, pendingIntent);


    }



    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context,appWidgetManager,appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}