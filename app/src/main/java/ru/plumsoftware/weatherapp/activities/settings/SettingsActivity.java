package ru.plumsoftware.weatherapp.activities.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.activities.main.MainActivity;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.dialogs.MyBottomSheetDialog;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        region::Base variables
        Context context = this;
//        Activity activity = SettingsActivity.this;
        Settings settings = Settings.getUserSettings(context);
        List<Pair<String, Integer>> list = new ArrayList<>();
//        endregion

        TextView textViewTheme = (TextView) findViewById(R.id.textViewTheme);
        TextView textViewUnits = (TextView) findViewById(R.id.textViewUnits);
        ImageView imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTheme.setText(convertToThemeName(settings.getTheme()));
        textViewUnits.setText(convertToUnitName(settings.getSystem()));

//        region::Clickers
        textViewTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                list.add(new Pair<>("Всегда светлая тема", AppCompatDelegate.MODE_NIGHT_NO));
                list.add(new Pair<>("Всегда тёмная тема", AppCompatDelegate.MODE_NIGHT_YES));
                list.add(new Pair<>("Как в системе", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));

                showBottomSheetDialog(list);
            }
        });
        textViewUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                list.add(new Pair<>("Метрическая система", 11));
                list.add(new Pair<>("Империческая система", 12));

                showBottomSheetDialog(list);
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//        endregion
    }

    private String convertToThemeName(int i) {
        switch (i) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                return "Всегда светлая тема";
            case AppCompatDelegate.MODE_NIGHT_YES:
                return "Всегда тёмная тема";
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                return "Как в системе";
            default:
                return "Ошибка: темы нераспознана";
        }
    }

    private void showBottomSheetDialog(List<Pair<String, Integer>> themes) {
        MyBottomSheetDialog bottomSheetDialog = new MyBottomSheetDialog();
        bottomSheetDialog.setData(themes);
        bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog");
    }

    private String convertToUnitName(String s) {
        switch (s) {
            case "metric":
                return "Метрическая";
            case "imperial":
                return "Империческая";
            default:
                return "Ошибка: не известная система единиц";
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, MainActivity.class));
    }
}