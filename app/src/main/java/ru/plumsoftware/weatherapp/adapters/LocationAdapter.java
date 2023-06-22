package ru.plumsoftware.weatherapp.adapters;

import static ru.plumsoftware.weatherapp.database.LocationDataBaseEntry.CITY_NAME;
import static ru.plumsoftware.weatherapp.database.LocationDataBaseEntry.DATABASE_TABLE_NAME_1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.activities.main.MainActivity;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.data.UserLocation;
import ru.plumsoftware.weatherapp.database.LocationDataBase;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    private Context context;
    private Activity activity;
    private List<UserLocation> list;

    public LocationAdapter(Context context, Activity activity, List<UserLocation> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationViewHolder(LayoutInflater.from(context).inflate(R.layout.history_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {

        UserLocation item = list.get(position);

        holder.textViewHistoryName.setText(item.getCityName() + ", " + item.getCountryName());
//        region::Clickers
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDataBase locationDataBase = new LocationDataBase(context);
                SQLiteDatabase database = locationDataBase.getReadableDatabase();
                database.delete(
                        DATABASE_TABLE_NAME_1,
                        BaseColumns._ID + " = ?",
                        new String[]{String.valueOf(item.getId())});
                database.close();
                holder.lay.setVisibility(View.GONE);
            }
        });

        holder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings settings = Settings.getUserSettings(context);
                settings.putValue("q", item.getCityName().trim().replaceAll("\\s", "%20"));
                settings.putValue("city", item.getCityName().trim());
                activity.finish();
                activity.overridePendingTransition(0, 0);
                Intent intent = new Intent(context, MainActivity.class);
                activity.startActivity(intent);
            }
        });
//        endregion

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class LocationViewHolder extends RecyclerView.ViewHolder {
    protected ImageView delete;
    protected TextView textViewHistoryName;
    protected LinearLayout lay;

    public LocationViewHolder(@NonNull View itemView) {
        super(itemView);

        delete = (ImageView) itemView.findViewById(R.id.imageViewDelete);
        textViewHistoryName = (TextView) itemView.findViewById(R.id.textViewHistoryName);
        lay = (LinearLayout) itemView.findViewById(R.id.lay);
    }
}
