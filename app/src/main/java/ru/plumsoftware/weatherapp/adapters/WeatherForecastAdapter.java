package ru.plumsoftware.weatherapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.weatherdata.forecast_owm.Rain;
import ru.plumsoftware.weatherapp.weatherdata.forecast_owm.WeatherItem;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherHolder> {
    private final List<WeatherItem> weatherItems;
    private final Context context;
    private final Settings settings;

    public WeatherForecastAdapter(List<WeatherItem> weatherItems, Context context) {
        this.weatherItems = weatherItems;
        this.context = context;
        settings = Settings.getUserSettings(context);
    }

    @NonNull
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherHolder(LayoutInflater.from(context).inflate(R.layout.hourly_item, parent, false));
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {
        WeatherItem weatherItem = weatherItems.get(position);
        String dateTime = new SimpleDateFormat("dd MMMM \n HH:mm", Locale.getDefault()).format(new Date(weatherItem.getDt() * 1000L));
        holder.textViewTime.setText(dateTime);

        int temp;

        if (settings.getSystem().equals("metric")) {
            temp = Math.toIntExact(Math.round(weatherItem.getMain().getTemp()));
            holder.textViewTemp.setText(temp + "°C");
        } else {
            temp = Math.toIntExact(Math.round(weatherItem.getMain().getTemp()));
            holder.textViewTemp.setText(temp + "°F");
        }

        if (weatherItem.getMain().getTemp() > 0.0) {
            //Summer
            holder.card.getLayoutParams().height = temp * 3;
            Rain rain = weatherItem.getRain();
            if (rain != null) {
                holder.textViewValue.setText((int) (rain.getH3() * 100) + "%");
            } else {
                holder.textViewValue.setText("0%");
            }

            if (rain != null && rain.getH3() > 0.0) {
                holder.imageViewSmallPromo.setImageResource(R.drawable.rainy_fill1_wght400_grad0_opsz48);
            } else {
                holder.imageViewSmallPromo.setImageResource(R.drawable.ic_baseline_cloud_24);
            }
        } else {
            holder.imageViewSmallPromo.setImageResource(R.drawable.ic_baseline_cloud_24);
        }
    }

    @Override
    public int getItemCount() {
        return weatherItems.size();
    }
}

class WeatherHolder extends RecyclerView.ViewHolder {
    protected TextView textViewTime, textViewValue, textViewTemp;
    protected ImageView imageViewSmallPromo;
    protected CardView card;

    public WeatherHolder(@NonNull View itemView) {
        super(itemView);

        setIsRecyclable(true);

        textViewValue = itemView.findViewById(R.id.textViewValue);
        textViewTime = itemView.findViewById(R.id.textViewTime);
        textViewTemp = itemView.findViewById(R.id.textViewTemp);

        imageViewSmallPromo = itemView.findViewById(R.id.imageViewSmallPromo);

        card = itemView.findViewById(R.id.card);
    }
}
