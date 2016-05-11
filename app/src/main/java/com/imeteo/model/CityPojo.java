package com.imeteo.model;

public class CityPojo {

    private String cityName;
    private  String cityTemp;

    public CityPojo(String cityName, String cityTemp) {
        this.cityName = cityName;
        this.cityTemp = cityTemp;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityTemp() {
        return cityTemp;
    }

    public void setCityTemp(String cityTemp) {
        this.cityTemp = cityTemp;
    }
}
