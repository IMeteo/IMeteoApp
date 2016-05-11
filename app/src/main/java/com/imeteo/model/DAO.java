package com.imeteo.model;

import java.util.ArrayList;

public interface DAO {

    void saveCityInfo(String cityName,double cityTemp);

    ArrayList<CityPojo> loadCities();

    CityPojo loadCity();

}
