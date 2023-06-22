package ru.plumsoftware.weatherapp.database;

import android.provider.BaseColumns;

public class LocationDataBaseEntry implements BaseColumns {
    public static final String DATABASE_NAME = "ru_plumsoftware_weatherapp_location_data_base.db";
    public static int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE_NAME_1 = "ru_plumsoftware_weatherapp_location_data_base_table_1";

    public static final String CITY_NAME = "_city_name";
    public static final String COUNTRY_NAME = "_country_name";
    public static final String COUNTRY_CODE = "_country_code";

    public static final String DATABASE_CREATE_TABLE_1_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_NAME_1 + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY," +
            CITY_NAME + " TEXT," +
            COUNTRY_NAME + " TEXT," +
            COUNTRY_CODE + " TEXT)";

    public static final String SQL_DELETE_TABLE_1 = "DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME_1;
}
