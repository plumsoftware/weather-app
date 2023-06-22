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

import java.util.List;

import ru.plumsoftware.weatherapp.items.Data;
import ru.plumsoftware.weatherapp.R;

public class DataAdapter extends RecyclerView.Adapter<DataHolder> {
    private List<Data> dataList;
    private Context context;

    public DataAdapter(List<Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataHolder(LayoutInflater.from(context).inflate(R.layout.data_layout, parent, false));
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        Data data = dataList.get(position);

        holder.textViewName.setText(data.getName());
        holder.textViewValue.setText(data.getValue());
        holder.imageViewPromo.setImageResource(data.getResId());

        if (position == dataList.size() - 1)
            holder.imageViewDivider.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class DataHolder extends RecyclerView.ViewHolder {

    protected TextView textViewName, textViewValue;
    protected ImageView imageViewPromo, imageViewDivider;

    public DataHolder(@NonNull View itemView) {
        super(itemView);

        textViewName = (TextView) itemView.findViewById(R.id.textViewName);
        textViewValue = (TextView) itemView.findViewById(R.id.textViewValue);

        imageViewPromo = (ImageView) itemView.findViewById(R.id.imageViewPromo);
        imageViewDivider = (ImageView) itemView.findViewById(R.id.imageViewDivider);
    }
}

