package ru.plumsoftware.weatherapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.plumsoftware.weatherapp.items.AirQualityData;
import ru.plumsoftware.weatherapp.R;

public class AirQualityAdapter extends RecyclerView.Adapter<AirQualityDataHolder> {
    private final List<AirQualityData> airQualityDataList;
    private final Context context;

    public AirQualityAdapter(List<AirQualityData> airQualityDataList, Context context) {
        this.airQualityDataList = airQualityDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public AirQualityDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AirQualityDataHolder(LayoutInflater.from(context).inflate(R.layout.air_quality_layout, parent, false));
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull AirQualityDataHolder holder, int position) {
        AirQualityData airQualityData = airQualityDataList.get(position);

        holder.cardColor.setCardBackgroundColor(airQualityData.getColor());
        holder.textViewAirDataValue.setText(Integer.toString(Math.round(airQualityData.getValue())));
        holder.textViewAirDataName.setText(airQualityData.getName());
    }

    @Override
    public int getItemCount() {
        return airQualityDataList.size();
    }
}

class AirQualityDataHolder extends RecyclerView.ViewHolder {
    protected TextView textViewAirDataValue, textViewAirDataName;
    protected CardView cardColor;

    public AirQualityDataHolder(@NonNull View itemView) {
        super(itemView);

        textViewAirDataValue = itemView.findViewById(R.id.textViewAirDataValue);
        textViewAirDataName = itemView.findViewById(R.id.textViewAirDataName);
        cardColor = itemView.findViewById(R.id.cardColor);
    }
}

