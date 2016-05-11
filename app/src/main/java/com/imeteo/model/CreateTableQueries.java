package com.imeteo.model;

public class CreateTableQueries {

    public static final String CREATE_TABLE_CITIES = "create table if not exists " + Constants.TABLE_CITIES + "(" +
            Constants.CITIES_ID + " integer primary key autoincrement not null," +
            Constants.CITIES_NAME + " varchar(50) not null," +
            Constants.CITIES_TEMP + " varchar(50) not null )";


}
