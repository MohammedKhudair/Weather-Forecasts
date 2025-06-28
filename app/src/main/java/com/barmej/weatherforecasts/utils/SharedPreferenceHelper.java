package com.barmej.weatherforecasts.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.barmej.weatherforecasts.R;


public class SharedPreferenceHelper {

    private static final String PREF_LAST_NOTIFICATION_APPEARANCE = "PREF_LAST_NOTIFICATION_APPEARANCE";

    public static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(PREF_LAST_NOTIFICATION_APPEARANCE, timeOfNotification).apply();
    }

    public static long getElapsedTimeSinceLastNotification(Context context) {
        long lastNotificationTimeMillis = SharedPreferenceHelper.getLastNotificationTimeMillis(context);
        return System.currentTimeMillis() - lastNotificationTimeMillis;
    }

    private static long getLastNotificationTimeMillis(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(PREF_LAST_NOTIFICATION_APPEARANCE, 0);
    }

    public static String getPreferenceWeatherLocation(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String locationKey = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);
        return sp.getString(locationKey, defaultLocation);
    }

    public static String getPreferenceMeasurementSystem(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String unitKey = context.getString(R.string.pref_units_key);
        String defaultUnit = context.getString(R.string.pref_unit_metric);
        return sp.getString(unitKey, defaultUnit);
    }


}
