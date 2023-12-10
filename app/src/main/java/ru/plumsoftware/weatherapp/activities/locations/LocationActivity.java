package ru.plumsoftware.weatherapp.activities.locations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.activities.main.MainActivity;
import ru.plumsoftware.weatherapp.adapters.LocationAdapter;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.data.UserLocation;
import ru.plumsoftware.weatherapp.database.LocationDataBase;
import ru.plumsoftware.weatherapp.database.LocationDataBaseEntry;
import ru.plumsoftware.weatherapp.weatherdata.current.Main;

public class LocationActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

//        region::Views
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLocations);
        ImageView imageViewMyLoc = (ImageView) findViewById(R.id.imageViewMyLoc);
//        endregion

//        region::Load history
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LocationAdapter(this, LocationActivity.this, loadHistory(this)));
//        endregion

//        region::Clicks
        imageViewMyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocationEnabled()) {
                    fetchLocation();
                } else {
                    Snackbar.make(
                            LocationActivity.this,
                            findViewById(R.id.linearLayout),
                            "Определение местоположения недоступно на вашем устройстве",
                            Snackbar.LENGTH_LONG
                    ).show();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    Geocoder geocoder = new Geocoder(LocationActivity.this);
                    ContentValues contentValues = new ContentValues();
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(query.trim(), 1);
                        if (!addressList.isEmpty()) {
                            String country = addressList.get(0).getCountryName();
                            contentValues.put(LocationDataBaseEntry.COUNTRY_NAME, country);
                        } else {
                            Toast.makeText(LocationActivity.this, "Такого города не найден.", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LocationDataBase locationDataBase = new LocationDataBase(LocationActivity.this);
                    SQLiteDatabase database = locationDataBase.getReadableDatabase();
                    contentValues.put(LocationDataBaseEntry.CITY_NAME, query.trim());//.replace("\\s+", ""));
                    contentValues.put(LocationDataBaseEntry.COUNTRY_CODE, "");
                    database.insert(LocationDataBaseEntry.DATABASE_TABLE_NAME_1, null, contentValues);
                    database.close();

                    Settings settings = Settings.getUserSettings(LocationActivity.this);
                    settings.putValue("q", query.trim());//.replaceAll("\\s", "%20"));
                    settings.putValue("city", query.trim());

                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(LocationActivity.this, MainActivity.class));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
//        endregion
    }

    //    region::Location functions
    public String getCity(double lat, double lng) {
        Geocoder geoCoder = new Geocoder(LocationActivity.this, Locale.getDefault());
        List<Address> address;
        String cityName = "";
        try {
            address = geoCoder.getFromLocation(lat, lng, 1);
            if (address.size() > 0) {
                cityName = address.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    public String getSubLocality(double lat, double lng) {
        Geocoder geoCoder = new Geocoder(LocationActivity.this, Locale.getDefault());
        List<Address> address;
        String subLocality = "";
        try {
            address = geoCoder.getFromLocation(lat, lng, 1);
            if (address.size() > 0) {
                subLocality = address.get(0).getSubLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subLocality;
    }

    public String getCountry(double lat, double lng) {
        Geocoder geoCoder = new Geocoder(LocationActivity.this, Locale.getDefault());
        List<Address> address;
        String countryName = "";
        try {
            address = geoCoder.getFromLocation(lat, lng, 1);
            if (address.size() > 0) {
                countryName = address.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countryName;
    }

    public String getCountryCode(double lat, double lng) {
        Geocoder geoCoder = new Geocoder(LocationActivity.this, Locale.getDefault());
        List<Address> address;
        String countryCode = "";
        try {
            address = geoCoder.getFromLocation(lat, lng, 1);
            if (address.size() > 0) {
                countryCode = address.get(0).getCountryCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countryCode;
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                LocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                LocationActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    LocationActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    101
            );
            return;
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationActivity.this);
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LocationDataBase databaseHelper = new LocationDataBase(LocationActivity.this);
                        SQLiteDatabase database = databaseHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(
                                LocationDataBaseEntry.CITY_NAME,
                                getCity(location.getLatitude(), location.getLongitude())//.replaceAll("\\s+", "_")
                        );
                        contentValues.put(
                                LocationDataBaseEntry.COUNTRY_NAME,
                                getCountry(location.getLatitude(), location.getLongitude())
                        );
                        contentValues.put(
                                LocationDataBaseEntry.COUNTRY_CODE,
                                getCountryCode(location.getLatitude(), location.getLongitude())
                        );
                        database.insert(
                                LocationDataBaseEntry.DATABASE_TABLE_NAME_1,
                                null,
                                contentValues
                        );
                        database.close();
                        //                    userLocation = UserLocation(//                        it.latitude,//                        it.longitude,
//                        getCity(it.latitude, it.longitude),//                        getCountry(it.latitude, it.longitude),//                        getCountryCode(it.latitude, it.longitude)//                    )
                        finish();
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(LocationActivity.this, LocationActivity.class);
                        //                    Не сработал parcelable, не понял почему                    //intent.putExtra("location", userLocation)
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) LocationActivity.this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }

    @SuppressLint("Recycle")
    private ArrayList<UserLocation> loadHistory(Context context) {
        LocationDataBase databaseHelper = new LocationDataBase(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
//        val values = ContentValues()
//        values.put(EventDatabaseEntry.EventDatabaseConstants.COLUMN_NAME_EVENT, name)//        val newRowId =
//            database?.insert(EventDatabaseEntry.EventDatabaseConstants.TABLE_NAME, null, values)
//        val projection = arrayOf(//            BaseColumns._ID,
//            DatabaseEntry.DatabaseEntry.CITY_NAME,//            DatabaseEntry.DatabaseEntry.COUNTRY_NAME,
//            DatabaseEntry.DatabaseEntry.COUNTRY_CODE//        )
////        val selection = "${DatabaseEntry.DatabaseEntry.COUNTRY_NAME} = ?"
//        val selectionArgs = arrayOf("Title")//
        String sortOrder = BaseColumns._ID + " ASC";
        Cursor cursor = database.query(
                LocationDataBaseEntry.DATABASE_TABLE_NAME_1,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        ArrayList<UserLocation> locations = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(LocationDataBaseEntry._ID));
            String countryCode = cursor.getString(cursor.getColumnIndexOrThrow(LocationDataBaseEntry.COUNTRY_CODE));
            String countryName = cursor.getString(cursor.getColumnIndexOrThrow(LocationDataBaseEntry.COUNTRY_NAME));
            String cityName = cursor.getString(cursor.getColumnIndexOrThrow(LocationDataBaseEntry.CITY_NAME));
            locations.add(new UserLocation(id, -1.0, -1.0, cityName, countryName, countryCode));
        }
        cursor.close();
        database.close();
        return locations;
    }
//    endregion


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(LocationActivity.this, MainActivity.class).putExtra("showAppOpen", false));
    }
}