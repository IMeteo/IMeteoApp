package com.imeteo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

public class CItyDao implements DAO {

    protected SQLiteDatabase database;
    protected DbHelper dbHelper;

    public CItyDao(Context context) {
        dbHelper = DbHelper.getInstance(context);
    }

    public void open() throws SQLiteException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    @Override
    public void saveCityInfo(String cityName, double cityTemp) {
        ContentValues values = new ContentValues();
        values.put(Constants.CITIES_NAME, cityName);
        values.put(Constants.CITIES_TEMP, cityTemp);
        long insertId = database.insert(Constants.TABLE_CITIES, null, values);
        if (insertId < 0) {
            Log.e("tag", "db error");
        }
    }

    @Override
    public ArrayList<CityPojo> loadCities() {
        ArrayList<CityPojo> cities = new ArrayList<>();
        Cursor cursor = database.rawQuery("select "+Constants.CITIES_NAME+", "+ Constants.CITIES_TEMP+" from "+Constants.TABLE_CITIES, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String cityName = cursor.getString(1);
                String cityTemp = cursor.getString(2);
                cities.add(new CityPojo(cityName, cityTemp));
                cursor.moveToNext();
            }
            cursor.close();
            return cities;
        }
        cursor.close();
        return null;
    }

    @Override
    public CityPojo loadCity() {
        return null;
    }


}
