package ru.plumsoftware.weatherapp.activities.about;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.plumsoftware.weatherapp.BuildConfig;
import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.activities.locations.LocationActivity;
import ru.plumsoftware.weatherapp.activities.main.MainActivity;
import ru.plumsoftware.weatherapp.data.Settings;

public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        TextView textViewConfig = (TextView) findViewById(R.id.textViewConfig);

        textViewConfig.setText("" + getResources().getString(R.string.app_name) + "\nВерсия приложения: " + BuildConfig.VERSION_NAME);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Settings.getUserSettings(AboutActivity.this).putValue("showAppOpen", false);
    }
}