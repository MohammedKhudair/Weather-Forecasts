package com.barmej.weatherforecasts.data.network;

import android.content.Context;
import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.utils.SharedPreferenceHelper;
import java.util.HashMap;
import java.util.Locale;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetWorkUtils {

    private static final String Tag = NetWorkUtils.class.getSimpleName();
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    private static final String WEATHER_ENDPOINT = "weather";
    private static final String FORECAST_ENDPOINT = "forecast";

    private static final String QUERY_PARAM = "q";
    private static final String FORMAT_PARAM = "mode";
    private static final String UNITS_PARAM = "units";
    private static final String LANG_PARAM = "lang";
    private static final String APP_ID_PARAM = "appid";

    private static final String FORMAT = "json";

    private static final String METRIC = "metric";
    private static final String IMPERIAL = "imperial";

    private static NetWorkUtils sInstance;
    private static final Object LOCK = new Object();
    private static Context mContext;

    OpenWeatherApiInterface mApiInterface;
//========================================

    public static NetWorkUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) sInstance = new NetWorkUtils(context);
            }
        }
        return sInstance;
    }


    private NetWorkUtils(Context context) {
        mContext = context.getApplicationContext();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiInterface = retrofit.create(OpenWeatherApiInterface.class);
    }

    public OpenWeatherApiInterface getApiInterface() {
        return mApiInterface;
    }

    public HashMap<String,String> getQueryMap() {
       HashMap<String,String> map = new HashMap<>();
        map.put(QUERY_PARAM, SharedPreferenceHelper.getPreferenceWeatherLocation(mContext));
        map.put(FORMAT_PARAM, FORMAT);
        map.put(UNITS_PARAM, SharedPreferenceHelper.getPreferenceMeasurementSystem(mContext));
        map.put(LANG_PARAM, Locale.getDefault().getLanguage());
        map.put(APP_ID_PARAM, mContext.getString(R.string.api_key));
        return map;
    }


}
