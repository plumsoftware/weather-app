package ru.plumsoftware.weatherapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

public class Settings {
    private String q, city;
    private String lang, system;
    private int theme;

    private int showAd;

    private boolean showAppOpen;

    protected static SharedPreferences sharedPreferences;

    public Settings(String q, String lang, String system) {
        this.q = q;
        this.lang = lang;
        this.system = system;
    }

    public Settings(String q, String city, String lang, String system) {
        this.q = q;
        this.city = city;
        this.lang = lang;
        this.system = system;
    }

    public Settings(String q, String city, String lang, String system, int theme) {
        this.q = q;
        this.city = city;
        this.lang = lang;
        this.system = system;
        this.theme = theme;
    }

    public Settings(String q, String city, String lang, String system, int theme, int showAd) {
        this.q = q;
        this.city = city;
        this.lang = lang;
        this.system = system;
        this.theme = theme;
        this.showAd = showAd;
    }

    public Settings(String q, String city, String lang, String system, int theme, int showAd, boolean showAppOpen) {
        this.q = q;
        this.city = city;
        this.lang = lang;
        this.system = system;
        this.theme = theme;
        this.showAd = showAd;
        this.showAppOpen = showAppOpen;
    }

    public static Settings getUserSettings(Context context) {
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        String q = sharedPreferences.getString("q", "");
        String lang = sharedPreferences.getString("lang", "ru");//Locale.getDefault().getISO3Language());
        String system = sharedPreferences.getString("system", "metric");
        String city = sharedPreferences.getString("city", "");
        int theme = sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO);
        int showAd = sharedPreferences.getInt("showAd", 0);
        boolean showAppOpen = sharedPreferences.getBoolean("showAppOpen", true);
        Log.d("TAG", "ShowAppOpen: " + Boolean.toString(showAppOpen));
        return new Settings(q, city, lang, system, theme, showAd, showAppOpen);
    }

    public void putValue(String name, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (value instanceof String) {
            String s = String.valueOf(value);
            editor.putString(name, s);
            editor.apply();
            return;
        }

        if (value instanceof Integer) {
            int i = Integer.parseInt(String.valueOf(value));
            editor.putInt(name, i);
            editor.apply();
            return;
        }

        if (value instanceof Boolean) {
            boolean b = (boolean) value;
            editor.putBoolean(name, b);
            editor.apply();
            return;
        }

        Log.d("TAG", "ShowAppOpen: " + value.toString());
    }

    public void putValue(String name, Integer value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    //    region::Getters
    public String getQ() {
        return q;
    }

    public String getLang() {
        return lang;
    }

    public String getSystem() {
        return system;
    }

    public String getCity() {
        return city;
    }

    public int getTheme() {
        return theme;
    }

    public int getShowAd() {
        return showAd;
    }

    public boolean isShowAppOpen() {
        return showAppOpen;
    }

    //    endregion
}
