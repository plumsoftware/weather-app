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

import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.rewarded.Reward;
import com.yandex.mobile.ads.rewarded.RewardedAd;
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.weatherdata.forecast.Alert;

public class AlertAdapter extends RecyclerView.Adapter<AlertHolder> {
    private final List<Alert> alerts;
    private final Context context;

    public AlertAdapter(List<Alert> alerts, Context context) {
        this.alerts = alerts;
        this.context = context;
    }

    @NonNull
    @Override
    public AlertHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlertHolder(LayoutInflater.from(context).inflate(R.layout.alert_item, parent, false));
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull AlertHolder holder, int position) {
        Alert alert = alerts.get(position);

        Calendar calendar = Calendar.getInstance();
        String time = "";

        if (position == 0){
            time = "Сегодня, ";
        }

        if (position == 1){
            time = "Завтра, ";
        }

        calendar.setTimeInMillis(System.currentTimeMillis());

        time = time + (calendar.get(Calendar.DAY_OF_MONTH) + position) +
                " " +
                new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date(System.currentTimeMillis()));

//        if (!alert.getAreas().isEmpty())
//            titleTV = titleTV + "\n" + "in " +
//                    alert.getAreas();
//
//        if (!alert.getHeadline().isEmpty())
//            titleTV = titleTV + "\n" +
//                    alert.getHeadline() + "•" + alert.getEvent();
//        else
//            titleTV = titleTV + " | " + alert.getEvent();


        holder.textViewAlertTime.setText(time);

//        if (alerts.size() != 1) {
//            if (position == 0) {
//                holder.cardAlert.setVisibility(View.GONE);

                if (!alert.getHeadline().isEmpty())
                    holder.textViewHeadline.setText(alert.getEvent() + ", " + alert.getDesc());

//            }
//            if (position != 0) {
//                holder.cardAlert.setVisibility(View.VISIBLE);
//                holder.textViewHeadline.setVisibility(View.GONE);
//            }
//        } else {
//            holder.cardAlert.setVisibility(View.VISIBLE);
//            holder.textViewHeadline.setVisibility(View.GONE);
//        }

//        ProgressDialog progressDialog = new ProgressDialog(context);
//        RewardedAd mRewardedAd = new RewardedAd(context);
//        mRewardedAd.setAdUnitId("R-M-2149019-1");

        holder.cardAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressDialog.show();
//                mRewardedAd.setRewardedAdEventListener(new RewardedAdEventListener() {
//                    @Override
//                    public void onRewarded(@NonNull final Reward reward) {
//                        progressDialog.dismiss();
//                        holder.cardAlert.setVisibility(View.GONE);
//                        holder.textViewHeadline.setVisibility(View.VISIBLE);
//                        holder.textViewHeadline.setText(alert.getEvent() + ", " + alert.getDesc());
//                    }
//
//                    @Override
//                    public void onAdClicked() {
//
//                    }
//
//                    @Override
//                    public void onAdLoaded() {
//                        mRewardedAd.show();
//
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
//                        progressDialog.dismiss();
//                        holder.cardAlert.setVisibility(View.GONE);
//                        holder.textViewHeadline.setVisibility(View.VISIBLE);
//                        holder.textViewHeadline.setText(alert.getEvent() + ", " + alert.getHeadline());
//                        //Toast.makeText(context, alert.getHeadline(), Toast.LENGTH_SHORT).show();
//                        //Toast.makeText(context, adRequestError.toString(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onAdShown() {
//                        progressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onAdDismissed() {
//                        progressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onLeftApplication() {
//                        progressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onReturnedToApplication() {
//
//                    }
//
//                    @Override
//                    public void onImpression(@Nullable ImpressionData impressionData) {
//
//                    }
//                });
//
//                // Создание объекта таргетирования рекламы.
//                final AdRequest adRequest = new AdRequest.Builder().build();
//
//                // Загрузка объявления.
//                mRewardedAd.loadAd(adRequest);
            }
        });

        //holder.textViewInstruction.setEnabled(position == 0);
        //holder.textViewEvent.setEnabled(position == 0);

//        if (!alert.getDesc().isEmpty())
//            holder.textViewEvent.setText(alert.getDesc());
//        else
//            holder.textViewEvent.setText(alert.getEvent());
//
//        if (alert.getInstruction().isEmpty()) {
//            holder.textViewInstruction.setVisibility(View.GONE);
//        } else {
//            holder.textViewInstruction.setText(alert.getInstruction());
//        }
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }
}

class AlertHolder extends RecyclerView.ViewHolder {
    protected TextView textViewHeadline, textViewAlertTime; //textViewInstruction, textViewEvent;
    protected CardView cardAlert;

    public AlertHolder(@NonNull View itemView) {
        super(itemView);

        textViewHeadline = itemView.findViewById(R.id.textViewAlertHeadline);
        textViewAlertTime = itemView.findViewById(R.id.textViewAlertTime);
        //textViewInstruction = itemView.findViewById(R.id.textViewInstruction);
        //textViewEvent = itemView.findViewById(R.id.textViewEvent);

        cardAlert = itemView.findViewById(R.id.cardAlert);
    }
}

