package ru.plumsoftware.weatherapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.weatherdata.forecast.Day;

public class DailyWeatherForecastAdapter extends RecyclerView.Adapter<WeatherDailyHolder> {
    private List<Day> days;
    private List<Long> time;
    private Context context;

    public DailyWeatherForecastAdapter(List<Day> days, List<Long> time, Context context) {
        this.days = days;
        this.time = time;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherDailyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherDailyHolder(LayoutInflater.from(context).inflate(R.layout.daily_forecast, parent, false));
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull WeatherDailyHolder holder, int position) {
        Day day = days.get(position);

        String linkToImage = "https:" + day.getCondition().getIcon();

        Glide
                .with(context)
                .load(linkToImage)
                .into(holder.imageViewMainPromo);

        if (Settings.getUserSettings(context).getSystem().equals("metric")) {

            int temp = Math.toIntExact(Math.round(day.getAvgtempC()));
            int tempMax = Math.toIntExact(Math.round(day.getMaxtempC()));
            int tempMin = Math.toIntExact(Math.round(day.getMintempC()));

            holder.textViewMainTemp.setText(Integer.toString(temp) + "°C");
            holder.textViewMainMaxMinTemp.setText(Integer.toString(tempMax) + "/" + Integer.toString(tempMin));
        } else {
            int temp = Math.toIntExact(Math.round(day.getAvgtempF()));
            int tempMax = Math.toIntExact(Math.round(day.getMaxtempF()));
            int tempMin = Math.toIntExact(Math.round(day.getMintempF()));

            holder.textViewMainTemp.setText(Integer.toString(temp) + "°F");
            holder.textViewMainMaxMinTemp.setText(Integer.toString(tempMax) + "/" + Integer.toString(tempMin));
        }

        holder.textViewChanceOfRain.setText("\uD83C\uDF27 " + Integer.toString(day.getDailyChanceOfRain()) + "%");
        holder.textViewMainTime.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date(time.get(position))));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }
}

class WeatherDailyHolder extends RecyclerView.ViewHolder {

    protected TextView textViewMainTemp, textViewMainMaxMinTemp, textViewChanceOfRain, textViewMainTime;
    protected ImageView imageViewMainPromo;

    public WeatherDailyHolder(@NonNull View itemView) {
        super(itemView);

        textViewMainTemp = (TextView) itemView.findViewById(R.id.textViewMainTemp);
        textViewMainMaxMinTemp = (TextView) itemView.findViewById(R.id.textViewMainMaxMinTemp);
        textViewChanceOfRain = (TextView) itemView.findViewById(R.id.textViewChanceOfRain);
        textViewMainTime = (TextView) itemView.findViewById(R.id.textViewMainTime);

        imageViewMainPromo = (ImageView) itemView.findViewById(R.id.imageViewMainPromo);
    }
}
