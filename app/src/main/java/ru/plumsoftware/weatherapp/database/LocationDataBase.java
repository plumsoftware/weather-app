package ru.plumsoftware.weatherapp.database;

import static ru.plumsoftware.weatherapp.database.LocationDataBaseEntry.DATABASE_CREATE_TABLE_1_IF_NOT_EXISTS;
import static ru.plumsoftware.weatherapp.database.LocationDataBaseEntry.DATABASE_NAME;
import static ru.plumsoftware.weatherapp.database.LocationDataBaseEntry.DATABASE_VERSION;
import static ru.plumsoftware.weatherapp.database.LocationDataBaseEntry.SQL_DELETE_TABLE_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocationDataBase extends SQLiteOpenHelper {

    public LocationDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_1_IF_NOT_EXISTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_1);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
