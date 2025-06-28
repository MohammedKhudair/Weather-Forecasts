package com.barmej.weatherforecasts.ui.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.data.OnDataDeliveryListener;
import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.ui.adapters.DaysForecastAdapter;
import com.barmej.weatherforecasts.ui.adapters.HoursForecastAdapter;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherForecast;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.ui.fragments.PrimaryWeatherInfoFragment;
import com.barmej.weatherforecasts.ui.fragments.SecondaryWeatherInfoFragment;
import com.barmej.weatherforecasts.data.network.NetWorkUtils;
import com.barmej.weatherforecasts.utils.NotificationUtils;
import com.barmej.weatherforecasts.utils.OpenWeatherDataParser;
import com.barmej.weatherforecasts.viewModel.MainViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private HoursForecastAdapter mHoursForecastAdapter;
    private DaysForecastAdapter mDaysForecastsAdapter;

    private RecyclerView mHoursForecastsRecyclerView;
    private RecyclerView mDaysForecastRecyclerView;
    private FrameLayout mFrameLayoutHeader;
    private FragmentManager mFragmentManager;
    private ViewPager viewPager;
    private TabLayout TabLayoutIndicator;

    HeaderFragmentAdapter mHeaderFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create notification channel.
        NotificationUtils.createWeatherStatusNotificationChannel(this);

        mFrameLayoutHeader = findViewById(R.id.header);
        TabLayoutIndicator = findViewById(R.id.TabLayoutIndicator);

        mFragmentManager = getSupportFragmentManager();

        viewPager = findViewById(R.id.pager);
        mHeaderFragmentAdapter = new HeaderFragmentAdapter(mFragmentManager);
        viewPager.setAdapter(mHeaderFragmentAdapter);
        TabLayoutIndicator.setupWithViewPager(viewPager);


        // Create new HoursForecastAdapter and set it to RecyclerView
        mHoursForecastAdapter = new HoursForecastAdapter(this);
        mHoursForecastsRecyclerView = findViewById(R.id.rv_hours_forecast);
        mHoursForecastsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mHoursForecastsRecyclerView.setAdapter(mHoursForecastAdapter);

        // Create new DaysForecastAdapter and set it to RecyclerView
        mDaysForecastsAdapter = new DaysForecastAdapter(this);
        mDaysForecastRecyclerView = findViewById(R.id.rv_days_forecast);
        mDaysForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDaysForecastRecyclerView.setAdapter(mDaysForecastsAdapter);

        mHoursForecastsRecyclerView.setVisibility(View.INVISIBLE);
        mDaysForecastRecyclerView.setVisibility(View.INVISIBLE);
        mFrameLayoutHeader.setVisibility(View.INVISIBLE);


        // Request current weather data
        requestWeatherInfo();

        // Request forecasts data
        requestForecastsInfo();

    }

    /**
     * Request weather data
     */
    private void requestWeatherInfo() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getWeatherInfoLiveData().observe(this, new Observer<WeatherInfo>() {
            @Override
            public void onChanged(WeatherInfo weatherInfo) {
                if (weatherInfo != null) {
                    mFrameLayoutHeader.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Request forecasts data
     */
    private void requestForecastsInfo() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        mainViewModel.getForecastListsLiveData().observe(this, new Observer<ForecastLists>() {
            @Override
            public void onChanged(ForecastLists forecastLists) {
                if (forecastLists != null) {
                    mHoursForecastAdapter.updateData(forecastLists.getHoursForecasts());
                    mDaysForecastsAdapter.updateData(forecastLists.getDaysForecasts());
                    mHoursForecastsRecyclerView.setVisibility(View.VISIBLE);
                    mDaysForecastRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    class HeaderFragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments;

        public HeaderFragmentAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragments = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PrimaryWeatherInfoFragment();
                case 1:
                    return new SecondaryWeatherInfoFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragments.add(fragment);
            return fragment;
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
