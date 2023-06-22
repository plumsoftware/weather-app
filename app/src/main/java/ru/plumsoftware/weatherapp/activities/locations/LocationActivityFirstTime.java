package ru.plumsoftware.weatherapp.activities.locations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.activities.main.MainActivity;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.database.LocationDataBase;
import ru.plumsoftware.weatherapp.database.LocationDataBaseEntry;

public class LocationActivityFirstTime extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_first_time);

//        region::Views
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        ImageView imageViewMyLoc = (ImageView) findViewById(R.id.imageViewFetchLocation);
//        endregion

//        region::Clicks
        imageViewMyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocationEnabled()) {
                    fetchLocation();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(LocationActivityFirstTime.this, MainActivity.class));
                } else {
                    Snackbar.make(
                            LocationActivityFirstTime.this,
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
                    Geocoder geocoder = new Geocoder(LocationActivityFirstTime.this);
                    ContentValues contentValues = new ContentValues();
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(query.trim(), 1);
                        if (!addressList.isEmpty()) {
                            String country = addressList.get(0).getCountryName();
                            contentValues.put(LocationDataBaseEntry.COUNTRY_NAME, country);
                        } else {
                            Toast.makeText(LocationActivityFirstTime.this, "Такого города не найден.", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LocationDataBase locationDataBase = new LocationDataBase(LocationActivityFirstTime.this);
                    SQLiteDatabase database = locationDataBase.getReadableDatabase();
                    contentValues.put(LocationDataBaseEntry.CITY_NAME, query.trim());//.replace("\\s+", ""));
                    contentValues.put(LocationDataBaseEntry.COUNTRY_CODE, "");
                    database.insert(LocationDataBaseEntry.DATABASE_TABLE_NAME_1, null, contentValues);
                    database.close();

                    Settings settings = Settings.getUserSettings(LocationActivityFirstTime.this);
                    settings.putValue("q", query.trim());//.replaceAll("\\s", "%20"));
                    settings.putValue("city", query.trim());

                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(LocationActivityFirstTime.this, MainActivity.class));
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
        Geocoder geoCoder = new Geocoder(LocationActivityFirstTime.this, Locale.getDefault());
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
        Geocoder geoCoder = new Geocoder(LocationActivityFirstTime.this, Locale.getDefault());
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
        Geocoder geoCoder = new Geocoder(LocationActivityFirstTime.this, Locale.getDefault());
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
        Geocoder geoCoder = new Geocoder(LocationActivityFirstTime.this, Locale.getDefault());
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
                LocationActivityFirstTime.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                LocationActivityFirstTime.this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    LocationActivityFirstTime.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    101
            );
            return;
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationActivityFirstTime.this);
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LocationDataBase databaseHelper = new LocationDataBase(LocationActivityFirstTime.this);
                        SQLiteDatabase database = databaseHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(
                                LocationDataBaseEntry.CITY_NAME,
                                getCity(location.getLatitude(), location.getLongitude()).replaceAll("\\s+", "_")
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

                        finish();
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(LocationActivityFirstTime.this, MainActivity.class);
                        //                    Не сработал parcelable, не понял почему                    //intent.putExtra("location", userLocation)
                        startActivity(intent);
                    }
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LocationActivityFirstTime.this, "Невозможно определить местоположение.\nОшибка: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) LocationActivityFirstTime.this.getSystemService(Context.LOCATION_SERVICE);
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
//    endregion
}