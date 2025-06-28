package com.barmej.weatherforecasts.data.entity;

import java.util.List;

public class WeatherForecast {

    private List<Forecast> list;

    public List<Forecast> getList() {
        return list;
    }

    public void setList(List<Forecast> list) {
        this.list = list;
    }
}
