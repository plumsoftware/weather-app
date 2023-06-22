package ru.plumsoftware.weatherapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.weatherdata.forecast.Day;

public class DayAdapter extends RecyclerView.Adapter<DayViewHolder> {

    private Context context;
    private Activity activity;
    private List<Pair<Day, String>> list;

    private Settings settings;

    //    region::Date
    private LocalDate date;
    private DayOfWeek dayOfWeek;
//    endregion

    public DayAdapter(Context context, Activity activity, List<Pair<Day, String>> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        settings = Settings.getUserSettings(context);
    }

    private Calendar calendar = Calendar.getInstance();
    private int dayOfWeekNum = calendar.get(Calendar.DAY_OF_WEEK);

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DayViewHolder(LayoutInflater.from(context).inflate(R.layout.day_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = list.get(position).first;
        String strDate = list.get(position).second;

//        region::Set data
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.parse(strDate);
            dayOfWeek = date.getDayOfWeek();
            holder.textViewDay.setText(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()));
        } else {
            dayOfWeekNum = dayOfWeekNum + position;
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeekNum);
            holder.textViewDay.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date(calendar.getTimeInMillis())));
        }

        if (settings.getSystem().equals("metric"))
            holder.textViewDayTemp.setText(Integer.toString(day.getAvgtempC().intValue()) + "°C");
        else
            holder.textViewDayTemp.setText(Integer.toString(day.getAvgtempF().intValue()) + "°F");

        if (day.getAvgtempC() > 0) {
            if (day.getDailyChanceOfRain() != 0) {
                holder.textViewChance.setText(Integer.toString(day.getDailyChanceOfRain()) + "%");
                holder.imageViewWeatherMode.setImageResource(R.drawable.rainy_fill1_wght400_grad0_opsz48);
            } else if (day.getDailyChanceOfRain() == 0) {
                holder.textViewChance.setText("0%");
                holder.imageViewWeatherMode.setImageResource(R.drawable.ic_baseline_cloud_24);
            }
        } else {
            if (day.getDailyChanceOfSnow() != 0) {
                holder.textViewChance.setText(Integer.toString(day.getDailyChanceOfSnow()) + "%");
                holder.imageViewWeatherMode.setImageResource(R.drawable.weather_snowy_fill1_wght400_grad0_opsz48);
            } else if (day.getDailyChanceOfSnow() == 0) {
                holder.textViewChance.setText("0%");
                holder.imageViewWeatherMode.setImageResource(R.drawable.ic_baseline_cloud_24);
            }
        }
//        endregion
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class DayViewHolder extends RecyclerView.ViewHolder {
    protected TextView textViewDay, textViewChance, textViewDayTemp;
    protected ImageView imageViewWeatherMode;

    public DayViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewDay = (TextView) itemView.findViewById(R.id.textViewDay);
        textViewChance = (TextView) itemView.findViewById(R.id.textViewChance);
        textViewDayTemp = (TextView) itemView.findViewById(R.id.textViewDayTemp);
        imageViewWeatherMode = (ImageView) itemView.findViewById(R.id.imageViewWeatherMode);
    }
}
