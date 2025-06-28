package com.barmej.weatherforecasts.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.databinding.FragmentSecondaryWeatherInfoBinding;
import com.barmej.weatherforecasts.viewModel.MainViewModel;

public class SecondaryWeatherInfoFragment extends Fragment {

    private FragmentSecondaryWeatherInfoBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_secondary_weather_info, container, false);
        mBinding.setLifecycleOwner(this);
        return mBinding.getRoot();
        //  return inflater.inflate(R.layout.fragment_secondary_weather_info,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View mainView = getView();

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mBinding.setWeatherInfo(mainViewModel.getWeatherInfoLiveData());
    }


}
