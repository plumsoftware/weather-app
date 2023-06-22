package ru.plumsoftware.weatherapp.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.activities.main.MainActivity;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.data.WeatherManager;
import ru.plumsoftware.weatherapp.weatherdata.current.CurrentWeather;

public class WeatherService extends Service {
    //    private final static int INTERVAL = 6 * 60 * 60 * 1000; // интервал, через который сервис выполняет задачу
    private final static int INTERVAL = 60000; // интервал, через который сервис выполняет задачу
    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // здесь выполняются необходимые операции, например, подключение к OpenWeatherMap API и получения данных
            // после получения данных можно вывести уведомление в статусную строку
//            sendNotification(WeatherService.this, "Заголовок", "Сообщение", 1, R.drawable.ic_sun);
            sendNotification();
            mHandler.postDelayed(mRunnable, INTERVAL);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler.post(mRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        region::Get weather data
        WeatherManager weatherManager = new WeatherManager(this);
        Settings settings = Settings.getUserSettings(this);
        weatherManager.getCurrentWeather(settings.getQ(), "8babcdab0f80b414d35b6d4b0f3e752e", settings.getSystem(), settings.getLang()).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(@NonNull Call<CurrentWeather> call, @NonNull Response<CurrentWeather> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Notification notification = new NotificationCompat.Builder(WeatherService.this, "default")
                            .setContentTitle(Integer.toString(Math.toIntExact(Math.round(response.body().getMain().getTemp()))) + "°")
                            .setContentText("Foreground service is running")
                            .setSmallIcon(R.drawable.ic_sun)
                            .setContentIntent(PendingIntent.getActivity(WeatherService.this, 0, new Intent(WeatherService.this, MainActivity.class), 0))
                            .build();

                    // Start the service in the foreground
                    startForeground(1, notification);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });
//        endregion
        return START_STICKY; // Сервис будет перезапущен после выхода из-за нехватки ресурсов
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification() {
        // Create notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_sun)
                .setContentTitle("My Title")
                .setContentText("My message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        // Create intent for MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Set content information with intent
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "Channel name",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel description");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build();

        // Send notification
        notificationManager.notify(1, notification);
    }

//    public void sendNotification(Context context, String title, String message, int notificationId, int iconResId) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
//                .setSmallIcon(iconResId)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
//
//        // Create intent for MainActivity
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//        // Set content information with intent
//        builder.setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("default",
//                    "ru_plumsoftware_weatherapp",
//                    NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Описание");
//            channel.enableLights(true);
//            channel.setLightColor(Color.BLUE);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        Notification notification = builder.build();
//
//        notificationManager.notify(notificationId, notification);
//    }
}
