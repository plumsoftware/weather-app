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
import ru.plumsoftware.weatherapp.weatherdata.forecast.Hour;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherHolder> {
    private List<Hour> hours;
    private Context context;
    private Calendar calendar = Calendar.getInstance();
    private Settings settings;

    public WeatherForecastAdapter(List<Hour> hours, Context context) {
        this.hours = hours;
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
        Hour hour = hours.get(position);

//        String link = "https:" + hour.getCondition().getIcon();
//
//        Glide
//                .with(context)
//                .load(link)
//                .into(holder.imageViewSmallPromo);

        String time = hour.getTime();

//        String[] split = time.split(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis())));
        String[] split = time.split("\\s");

        holder.textViewTime.setText(split[1]);

        int temp = 0;

        if (settings.getSystem().equals("metric")) {
            temp = Math.toIntExact(Math.round(hour.getTempC()));
            holder.textViewTemp.setText(Integer.toString(temp) + "°C");
        } else {
            temp = Math.toIntExact(Math.round(hour.getTempF()));
            holder.textViewTemp.setText(Integer.toString(temp) + "°F");
        }

        if (hour.getTempC() > 0.0) {
            //Summer
            holder.card.getLayoutParams().height = temp * 3;
            holder.textViewValue.setText(Integer.toString(hour.getChanceOfRain()) + "%");

            if (hour.getChanceOfRain() > 0) {
//                Glide
//                        .with(context)
//                        .load(R.drawable.rainy_fill1_wght400_grad0_opsz48)
//                        .into(holder.imageViewSmallPromo);
                holder.imageViewSmallPromo.setImageResource(R.drawable.rainy_fill1_wght400_grad0_opsz48);
            } else {
//                Glide
//                        .with(context)
//                        .load(R.drawable.ic_baseline_cloud_24)
//                        .into(holder.imageViewSmallPromo);
                holder.imageViewSmallPromo.setImageResource(R.drawable.ic_baseline_cloud_24);
            }
        } else {
            holder.card.getLayoutParams().height = temp * (-3);
            holder.textViewValue.setText(Integer.toString(hour.getChanceOfSnow()) + "%");

            if (hour.getChanceOfSnow() > 0) {
//                Glide
//                        .with(context)
//                        .load(R.drawable.weather_snowy_fill1_wght400_grad0_opsz48)
//                        .into(holder.imageViewSmallPromo);
                holder.imageViewSmallPromo.setImageResource(R.drawable.weather_snowy_fill1_wght400_grad0_opsz48);
            } else {
//                Glide
//                        .with(context)
//                        .load(R.drawable.ic_baseline_cloud_24)
//                        .into(holder.imageViewSmallPromo);
                holder.imageViewSmallPromo.setImageResource(R.drawable.ic_baseline_cloud_24);
            }
        }
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }
}

class WeatherHolder extends RecyclerView.ViewHolder {
    protected TextView textViewTime, textViewValue, textViewTemp;
    protected ImageView imageViewSmallPromo;
    protected CardView card;

    public WeatherHolder(@NonNull View itemView) {
        super(itemView);

        setIsRecyclable(true);

        textViewValue = (TextView) itemView.findViewById(R.id.textViewValue);
        textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
        textViewTemp = (TextView) itemView.findViewById(R.id.textViewTemp);

        imageViewSmallPromo = (ImageView) itemView.findViewById(R.id.imageViewSmallPromo);

        card = (CardView) itemView.findViewById(R.id.card);
    }
}
