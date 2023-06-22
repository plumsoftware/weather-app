package ru.plumsoftware.weatherapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.data.WeatherManager;
import ru.plumsoftware.weatherapp.links.Link;
import ru.plumsoftware.weatherapp.weatherdata.current.CurrentWeather;
import ru.plumsoftware.weatherapp.weatherdata.forecast.Alert;
import ru.plumsoftware.weatherapp.weatherdata.forecast.ForecastWeather;

/**
 * Implementation of App Widget functionality.
 */
public class SimpleWhiteWidget extends AppWidgetProvider {
    public final static String FORCE_WIDGET_UPDATE = "FORCE_WIDGET_UPDATE";
    public static boolean isUpdated = false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        String city = Settings.getUserSettings(context).getQ();
        String units = Settings.getUserSettings(context).getSystem();
        String lang = Settings.getUserSettings(context).getLang();

        String s = "";

        switch (units) {
            case "metric":
                s = "°C";
                break;
            case "imperial":
                s = "°F";
                break;
        }

        WeatherManager weatherManager = new WeatherManager(context);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.simple_app_widget);
        views.setTextViewText(R.id.textViewMainLocationD, city);
        views.setImageViewResource(R.id.imageView2D, R.drawable.ic_location_black);
        views.setImageViewResource(R.id.imageView, R.drawable.ic_refresh_black);
        views.setTextViewText(R.id.textViewMainTempWD, "...");
        views.setTextViewText(R.id.textViewAlertWD, "...");

        String finalS = s;
        weatherManager.getCurrentWeather(city, "8babcdab0f80b414d35b6d4b0f3e752e", units, lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(@NonNull Call<CurrentWeather> call, @NonNull Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    Double d = Objects.requireNonNull(response.body()).getMain().getTemp();
                    String temp = Integer.toString((int) Math.round(d)) + finalS;
                    String iconId = response.body().getWeather().get(0).getIcon();
                    String build = Link.Builder.build(iconId);

                    RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.simple_app_widget);

                    updateViews.setTextViewText(R.id.textViewMainTempWD, temp);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Bitmap bitmap = Glide.with(context).asBitmap().load(build).into(80, 80).get();

                                updateViews.setImageViewBitmap(R.id.imageViewMainPromoWD, bitmap);
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Notify the widget that the list view needs to be updated.
                            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                            final ComponentName cn = new ComponentName(context, SimpleWhiteWidget.class);
                            mgr.updateAppWidget(cn, updateViews);

                        }
                    }).start();

                    isUpdated = true;

                    // Notify the widget that the list view needs to be updated.
                    final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    final ComponentName cn = new ComponentName(context, SimpleWhiteWidget.class);
                    mgr.updateAppWidget(cn, updateViews);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CurrentWeather> call, @NonNull Throwable t) {

            }
        });

        weatherManager.getDailyWeather("863d89dfe5734725a09155301221203", city, 1, "yes", "yes", lang).enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(@NonNull Call<ForecastWeather> call, @NonNull Response<ForecastWeather> response) {

                if (response.isSuccessful()) {
                    ForecastWeather body = response.body();

                    Integer dailyChanceOfRain = Objects.requireNonNull(body).getForecast().getForecastday().get(0).getDay().getDailyChanceOfRain();
                    String chanceOfRain = "\uD83C\uDF27 " + Integer.toString((int) dailyChanceOfRain) + "%";
                    List<Alert> alerts = body.getAlerts().getAlert();

                    for (int i = 0; i < alerts.size(); i++) {
                        Pattern pattern = Pattern.compile(
                                "[" + "A-Za-z" + "\\d" + "\\s" + "\\p{Punct}" + "]" + "*"
                        );
                        Matcher matcher = pattern.matcher(alerts.get(i).getEvent());

                        if (matcher.matches() && Settings.getUserSettings(context).getLang().equals(Locale.UK.getISO3Language())) {

                        }
                        if (!Settings.getUserSettings(context).getLang().equals(Locale.UK.getISO3Language()) && matcher.matches()) {
                            alerts.remove(i);
                        }
                    }

                    RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.simple_app_widget);

                    //updateViews.setTextViewText(R.id.textViewChanceOfRainW, chanceOfRain);
                    if (alerts.size() == 0) {
                        updateViews.setTextViewText(R.id.textViewAlertWD, chanceOfRain + "  •  ");
                    } else {
                        if (alerts.get(0).getEvent().length() > 11) {
                            updateViews.setTextViewText(R.id.textViewAlertWD, alerts.get(0).getEvent().substring(0, 5) + "..." + "  •  " + chanceOfRain + "  •  ");
                        } else
                            updateViews.setTextViewText(R.id.textViewAlertWD, alerts.get(0).getEvent() + "  •  " + chanceOfRain + "  •  ");
                    }

                    if (!isUpdated) {
                        String build = "https:" + body.getForecast().getForecastday().get(0).getDay().getCondition().getIcon();
                        String temp = "";
                        switch (finalS) {
                            case "°C":
                                temp = Integer.toString((int) Math.round(body.getCurrent().getTempC())) + finalS;
                                break;
                            case "°F":
                                temp = Integer.toString((int) Math.round(body.getCurrent().getTempF())) + finalS;
                                break;
                        }

                        updateViews.setTextViewText(R.id.textViewMainTempWD, temp);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                /**
                                 try {
                                 Bitmap bitmap = Glide.with(context).asBitmap().load(build).into(80, 80).get();

                                 updateViews.setImageViewBitmap(R.id.imageViewMainPromoWD, bitmap);
                                 } catch (ExecutionException | InterruptedException e) {
                                 e.printStackTrace();
                                 }
                                 **/

                                // Notify the widget that the list view needs to be updated.
                                final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                                final ComponentName cn = new ComponentName(context, SimpleWhiteWidget.class);
                                mgr.updateAppWidget(cn, updateViews);

                            }
                        }).start();

                        isUpdated = true;
                    }

                    // Notify the widget that the list view needs to be updated.
                    final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                    final ComponentName cn = new ComponentName(context, SimpleWhiteWidget.class);
                    mgr.updateAppWidget(cn, updateViews);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForecastWeather> call, @NonNull Throwable t) {

            }
        });

        views.setOnClickPendingIntent(R.id.imageView, getPendingSelfIntent(context, FORCE_WIDGET_UPDATE));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (FORCE_WIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            ComponentName watchWidget = new ComponentName(context, SimpleWhiteWidget.class);

            String city = Settings.getUserSettings(context).getQ();
            String units = Settings.getUserSettings(context).getSystem();
            String lang = Settings.getUserSettings(context).getLang();

            String s = "";

            switch (units) {
                case "metric":
                    s = "°C";
                    break;
                case "imperial":
                    s = "°F";
                    break;
            }

            WeatherManager weatherManager = new WeatherManager(context);

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.simple_app_widget);
            views.setTextViewText(R.id.textViewMainLocationD, city);
            views.setImageViewResource(R.id.imageView2D, R.drawable.ic_location_black);
            views.setImageViewResource(R.id.imageView, R.drawable.ic_refresh_black);
            views.setTextViewText(R.id.textViewMainTempWD, "...");
            views.setTextViewText(R.id.textViewAlertWD, "...");

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(watchWidget, views);

            String finalS = s;
            weatherManager.getCurrentWeather(city, "8babcdab0f80b414d35b6d4b0f3e752e", units, lang).enqueue(new Callback<CurrentWeather>() {
                @Override
                public void onResponse(@NonNull Call<CurrentWeather> call, @NonNull Response<CurrentWeather> response) {
                    if (response.isSuccessful()) {
                        Double d = Objects.requireNonNull(response.body()).getMain().getTemp();
                        String temp = Integer.toString((int) Math.round(d)) + finalS;
                        String iconId = response.body().getWeather().get(0).getIcon();
                        String build = Link.Builder.build(iconId);

                        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.simple_app_widget);

                        updateViews.setTextViewText(R.id.textViewMainTempWD, temp);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                /**
                                 try {
                                 Bitmap bitmap = Glide.with(context).asBitmap().load(build).into(80, 80).get();

                                 updateViews.setImageViewBitmap(R.id.imageViewMainPromoWD, bitmap);
                                 } catch (ExecutionException | InterruptedException e) {
                                 e.printStackTrace();
                                 }
                                 **/

                                // Notify the widget that the list view needs to be updated.
                                final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                                final ComponentName cn = new ComponentName(context, SimpleWhiteWidget.class);
                                mgr.updateAppWidget(cn, updateViews);

                            }
                        }).start();

                        // Notify the widget that the list view needs to be updated.
                        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                        final ComponentName cn = new ComponentName(context, SimpleWhiteWidget.class);
                        mgr.updateAppWidget(cn, updateViews);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CurrentWeather> call, @NonNull Throwable t) {

                }
            });

            weatherManager.getDailyWeather("863d89dfe5734725a09155301221203", city, 1, "yes", "yes", lang).enqueue(new Callback<ForecastWeather>() {
                @Override
                public void onResponse(@NonNull Call<ForecastWeather> call, @NonNull Response<ForecastWeather> response) {

                    if (response.isSuccessful()) {
                        ForecastWeather body = response.body();

                        Integer dailyChanceOfRain = Objects.requireNonNull(body).getForecast().getForecastday().get(0).getDay().getDailyChanceOfRain();
                        String chanceOfRain = "\uD83C\uDF27 " + Integer.toString((int) dailyChanceOfRain) + "%";
                        List<Alert> alerts = body.getAlerts().getAlert();

                        for (int i = 0; i < alerts.size(); i++) {
                            Pattern pattern = Pattern.compile(
                                    "[" + "A-Za-z" + "\\d" + "\\s" + "\\p{Punct}" + "]" + "*"
                            );
                            Matcher matcher = pattern.matcher(alerts.get(i).getEvent());

                            if (matcher.matches() && Settings.getUserSettings(context).getLang().equals(Locale.UK.getISO3Language())) {

                            }
                            if (!Settings.getUserSettings(context).getLang().equals(Locale.UK.getISO3Language()) && matcher.matches()) {
                                alerts.remove(i);
                            }
                        }

                        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.simple_app_widget);

                        //updateViews.setTextViewText(R.id.textViewChanceOfRainW, chanceOfRain);
                        if (alerts.size() == 0) {
                            updateViews.setTextViewText(R.id.textViewAlertWD, chanceOfRain + "  •  ");
                        } else {
                            if (alerts.get(0).getEvent().length() > 11) {
                                updateViews.setTextViewText(R.id.textViewAlertWD, alerts.get(0).getEvent().substring(0, 5) + "..." + "  •  " + chanceOfRain + "  •  ");
                            } else
                                updateViews.setTextViewText(R.id.textViewAlertWD, alerts.get(0).getEvent() + "  •  " + chanceOfRain + "  •  ");
                        }


                        String build = "https:" + body.getForecast().getForecastday().get(0).getDay().getCondition().getIcon();
                        String temp = "";
                        switch (finalS) {
                            case "°C":
                                temp = Integer.toString((int) Math.round(body.getCurrent().getTempC())) + finalS;
                                break;
                            case "°F":
                                temp = Integer.toString((int) Math.round(body.getCurrent().getTempF())) + finalS;
                                break;
                        }

                        updateViews.setTextViewText(R.id.textViewMainTempWD, temp);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                /**
                                 try {
                                 Bitmap bitmap = Glide.with(context).asBitmap().load(build).into(80, 80).get();

                                 updateViews.setImageViewBitmap(R.id.imageViewMainPromoWD, bitmap);
                                 } catch (ExecutionException | InterruptedException e) {
                                 e.printStackTrace();
                                 }

                                 // Notify the widget that the list view needs to be updated.
                                 final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                                 final ComponentName cn = new ComponentName(context, SimpleWhiteWidget.class);
                                 mgr.updateAppWidget(cn, updateViews);
                                 **/

                            }
                        }).start();

                        // Notify the widget that the list view needs to be updated.
                        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                        final ComponentName cn = new ComponentName(context, SimpleWhiteWidget.class);
                        mgr.updateAppWidget(cn, updateViews);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ForecastWeather> call, @NonNull Throwable t) {

                }
            });

            views.setOnClickPendingIntent(R.id.imageView, getPendingSelfIntent(context, FORCE_WIDGET_UPDATE));

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(watchWidget, views);
        }

    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, SimpleWhiteWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}