package ru.plumsoftware.weatherapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.activities.settings.SettingsActivity;
import ru.plumsoftware.weatherapp.data.Settings;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeViewHolder> {
    private Context context;
    private Activity activity;
    private List<Pair<String, Integer>> list;
    private Settings settings;

    public ThemeAdapter(Context context, Activity activity, List<Pair<String, Integer>> list, Settings settings) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        this.settings = settings;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThemeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_theme, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(list.get(position).first);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).second != 11 && list.get(position).second != 12) {
                    settings.putValue("theme", list.get(position).second);
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                    activity.startActivity(new Intent(context, SettingsActivity.class));
                    AppCompatDelegate.setDefaultNightMode(list.get(position).second);
                } else if (list.get(position).second == 11 || list.get(position).second == 12) {
                    settings.putValue("system", convertNameToUnit(list.get(position).second));
                    activity.finish();
                    activity.overridePendingTransition(0, 0);
                    activity.startActivity(new Intent(context, SettingsActivity.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String convertNameToUnit(int i) {
        switch (i) {
            case 11:
                return "metric";
            case 12:
                return "imperial";
            default:
                return "metric";
        }
    }
}

class ThemeViewHolder extends RecyclerView.ViewHolder {
    protected TextView textView;

    public ThemeViewHolder(@NonNull View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.textViewTheme2);
    }
}
