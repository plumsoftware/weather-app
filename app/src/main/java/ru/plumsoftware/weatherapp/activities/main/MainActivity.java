package ru.plumsoftware.weatherapp.activities.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.nativeads.MediaView;
import com.yandex.mobile.ads.nativeads.NativeAd;
import com.yandex.mobile.ads.nativeads.NativeAdEventListener;
import com.yandex.mobile.ads.nativeads.NativeAdException;
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration;
import com.yandex.mobile.ads.nativeads.NativeAdView;
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder;
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoadListener;
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoader;
import com.yandex.mobile.ads.rewarded.RewardedAd;
import com.yandex.mobile.ads.rewarded.RewardedAdLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.plumsoftware.weatherapp.activities.about.AboutActivity;
import ru.plumsoftware.weatherapp.activities.locations.LocationActivity;
import ru.plumsoftware.weatherapp.activities.radar.RadarActivity;
import ru.plumsoftware.weatherapp.activities.settings.SettingsActivity;
import ru.plumsoftware.weatherapp.adapters.DayAdapter;
import ru.plumsoftware.weatherapp.items.AirQualityData;
import ru.plumsoftware.weatherapp.dialogs.ProgressDialog;
import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.data.WeatherManager;
import ru.plumsoftware.weatherapp.adapters.AirQualityAdapter;
import ru.plumsoftware.weatherapp.adapters.AlertAdapter;
import ru.plumsoftware.weatherapp.adapters.WeatherForecastAdapter;
import ru.plumsoftware.weatherapp.links.ApiData;
import ru.plumsoftware.weatherapp.links.Link;
import ru.plumsoftware.weatherapp.model.AdsConfig;
import ru.plumsoftware.weatherapp.weatherdata.current.CurrentWeather;
import ru.plumsoftware.weatherapp.weatherdata.forecast.AirQuality;
import ru.plumsoftware.weatherapp.weatherdata.forecast.Alert;
import ru.plumsoftware.weatherapp.weatherdata.forecast.Day;
import ru.plumsoftware.weatherapp.weatherdata.forecast.ForecastWeather;
import ru.plumsoftware.weatherapp.weatherdata.forecast.Forecastday;
import ru.plumsoftware.weatherapp.weatherdata.forecast.Hour;
import ru.plumsoftware.weatherapp.weatherdata.forecast_owm.MainWeatherResponse;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    @Nullable
    private RewardedAd mRewardedAd = null;
    @Nullable
    private RewardedAdLoader mRewardedAdLoader = null;
    private AppOpenAdLoader appOpenAdLoader = null;
    private final String AD_UNIT_ID = AdsConfig.OPEN_ADS_ID;
    private final AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(AD_UNIT_ID).build();

    private AppOpenAd mAppOpenAd = null;

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        region::Base variables
        Context context = MainActivity.this;
        Activity activity = MainActivity.this;
        ProgressDialog progressDialog = new ProgressDialog(context);
//        endregion
        final Settings[] settings = {Settings.getUserSettings(context)};
        AppCompatDelegate.setDefaultNightMode(convertToTheme(settings[0].getTheme()));
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, () -> {

        });

//        region::Service
//        Intent intent = new Intent(this, WeatherService.class);
//        startService(intent);
//        endregion

//        region::Animations
        Animation sun_animation = AnimationUtils.loadAnimation(context, R.anim.sun_rotation);
//        endregion

//        region::Obtain the FirebaseAnalytics instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
//        endregion

//        region::Find view by id
        ImageView imageViewSearch = (ImageView) activity.findViewById(R.id.imageViewSearch);
        ImageView imageViewAddLocation = (ImageView) activity.findViewById(R.id.imageViewAddLocation);
        ImageView imageViewMore = (ImageView) activity.findViewById(R.id.imageViewMore);
        ImageView imageViewDots = (ImageView) activity.findViewById(R.id.imageViewDots);
        ImageView imageViewWeatherPromo = (ImageView) activity.findViewById(R.id.imageViewWeatherPromo);

        TextView textViewCityName = (TextView) activity.findViewById(R.id.textViewCityName);
        //TextView textViewFullDate = (TextView) activity.findViewById(R.id.textViewFullDate);
        TextView textViewMainTemp = (TextView) activity.findViewById(R.id.textViewMainTemp);
        TextView textViewStatus = (TextView) activity.findViewById(R.id.textViewStatus);
        TextView textViewDetailedStatus = (TextView) activity.findViewById(R.id.textViewDetailedStatus);
        TextView textViewTempYesterday = (TextView) activity.findViewById(R.id.textViewTempYesterday);
        TextView textViewChanceOfRain = (TextView) activity.findViewById(R.id.textViewChanceOfRain);
        TextView textViewWindSpeed = (TextView) activity.findViewById(R.id.textViewWindSpeed);
        TextView textViewSunriseTime = (TextView) activity.findViewById(R.id.textViewSunriseTime);
        TextView textViewSunsetTime = (TextView) activity.findViewById(R.id.textViewSunsetTime);
        EditText multiAutoCompleteTextView = (EditText) activity.findViewById(R.id.multiAutoCompleteTextView);

        LinearLayout alertsL = (LinearLayout) activity.findViewById(R.id.alerts);
        LinearLayout mainLayout = (LinearLayout) activity.findViewById(R.id.mainLayout);

        RecyclerView recyclerViewAlerts = (RecyclerView) activity.findViewById(R.id.recyclerViewAlerts);
        RecyclerView recyclerViewHourlyForecast = (RecyclerView) activity.findViewById(R.id.recyclerViewHourlyForecast);
        RecyclerView recyclerViewAirQuality = (RecyclerView) activity.findViewById(R.id.recyclerViewAirQuality);
        RecyclerView recyclerViewDailyForecast = (RecyclerView) activity.findViewById(R.id.recyclerViewDailyForecast);

        CardView cardSunrise = (CardView) activity.findViewById(R.id.cardSunrise);
        CardView cardSunset = (CardView) activity.findViewById(R.id.cardSunset);

        PieChart pieChart = (PieChart) activity.findViewById(R.id.pieChart);
//        endregion

//        region::ADS
        NativeAdView mNativeAdView = (NativeAdView) activity.findViewById(R.id.nativeAdView);
        MediaView mediaView = (MediaView) activity.findViewById(R.id.media);
        TextView age = (TextView) activity.findViewById(R.id.age);
        TextView bodyView = (TextView) activity.findViewById(R.id.tvAdvertiser);
        TextView call_to_action = (TextView) activity.findViewById(R.id.btnVisitSite);
        TextView domain = (TextView) activity.findViewById(R.id.textViewDomain);
        ImageView favicon = (ImageView) activity.findViewById(R.id.adsPromo);
        ImageView imageViewFeedback = (ImageView) activity.findViewById(R.id.imageViewFeedback);
        TextView priceView = (TextView) activity.findViewById(R.id.priceView);
        TextView storeView = (TextView) activity.findViewById(R.id.storeView);
        TextView tvHeadline = (TextView) activity.findViewById(R.id.tvHeadline);
        TextView warning = (TextView) activity.findViewById(R.id.textViewWarning);
        CardView adsCard = (CardView) activity.findViewById(R.id.cardView2);
//        endregion

//        region::Load ad
        if (getIntent().getBooleanExtra("showAppOpen", true)) {
            if (settings[0].getShowAd() > 2) {
                progressDialog.show();
                appOpenAdLoader = new AppOpenAdLoader(context);
                AppOpenAdLoadListener appOpenAdLoadListener = new AppOpenAdLoadListener() {
                    @Override
                    public void onAdLoaded(@NonNull final AppOpenAd appOpenAd) {
                        // The ad was loaded successfully. Now you can show loaded ad.
                        mAppOpenAd = appOpenAd;
                        mAppOpenAd.show(activity);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                        // Ad failed to load with AdRequestError.
                        // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                        progressDialog.dismiss();
                    }
                };
                appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener);
                appOpenAdLoader.loadAd(adRequestConfiguration);

                AppOpenAdEventListener appOpenAdEventListener = new AppOpenAdEventListener() {
                    @Override
                    public void onAdShown() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onAdFailedToShow(@NonNull final AdError adError) {
                        progressDialog.dismiss();
                        // Called when ad failed to show.
                    }

                    @Override
                    public void onAdDismissed() {
                        // Called when ad is dismissed.
                        // Clean resources after dismiss and preload new ad.
                    }

                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                    }

                    @Override
                    public void onAdImpression(@Nullable final ImpressionData impressionData) {
                        progressDialog.dismiss();
                        // Called when an impression is recorded for an ad.
                    }
                };

                if (mAppOpenAd != null) {
                    mAppOpenAd.setAdEventListener(appOpenAdEventListener);
                    mAppOpenAd.show(activity);
                }
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
            }
        }

        if (settings[0].getShowAd() > 2) {
            progressDialog.show();
            final NativeBulkAdLoader nativeBulkAdLoader = new NativeBulkAdLoader(context);
            final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder(AdsConfig.NATIVE_ADS_ID).build();
            nativeBulkAdLoader.loadAds(nativeAdRequestConfiguration, 1);
            nativeBulkAdLoader.setNativeBulkAdLoadListener(new NativeBulkAdLoadListener() {
                @Override
                public void onAdsLoaded(@NonNull final List<NativeAd> nativeAds) {
                    try {
                        for (final NativeAd nativeAd : nativeAds) {
                            final NativeAdViewBinder nativeAdViewBinder = new NativeAdViewBinder.Builder(mNativeAdView)
                                    .setAgeView(age)
                                    .setBodyView(bodyView)
                                    .setCallToActionView(call_to_action)
                                    .setDomainView(domain)
                                    //.setFaviconView(notesViewHolder.favicon)
                                    .setFeedbackView(imageViewFeedback)
                                    .setIconView(favicon)
                                    .setMediaView(mediaView)
                                    .setPriceView(priceView)
                                    //.setRatingView((MyRatingView) findViewById(R.id.rating))
                                    //.setReviewCountView((TextView) findViewById(R.id.review_count))
                                    .setSponsoredView(storeView)
                                    .setTitleView(tvHeadline)
                                    .setWarningView(warning)
                                    .build();

                            try {
                                nativeAd.bindNativeAd(nativeAdViewBinder);
                                nativeAd.setNativeAdEventListener(new NativeAdEventListener() {
                                    @Override
                                    public void onAdClicked() {

                                    }

                                    @Override
                                    public void onLeftApplication() {

                                    }

                                    @Override
                                    public void onReturnedToApplication() {

                                    }

                                    @Override
                                    public void onImpression(@Nullable ImpressionData impressionData) {

                                    }
                                });
                                mNativeAdView.setVisibility(View.VISIBLE);
                                adsCard.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            } catch (final NativeAdException exception) {
                                Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onAdsFailedToLoad(@NonNull final AdRequestError error) {
                    adsCard.setVisibility(View.GONE);
                    mNativeAdView.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    //Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            settings[0].putValue("showAd", (settings[0].getShowAd() + 1));
            adsCard.setVisibility(View.GONE);
            mNativeAdView.setVisibility(View.GONE);
            progressDialog.dismiss();
        }
        progressDialog.dismiss();
//        endregion

//        region::Variables
        WeatherManager weatherManager = new WeatherManager(context);
        TextView[] textViewsCurrent = new TextView[]{
                textViewMainTemp,
                textViewStatus,
                textViewDetailedStatus,
                textViewSunriseTime,
                textViewSunsetTime,
                textViewChanceOfRain,
                textViewWindSpeed,
                textViewTempYesterday
        };

        RecyclerView[] recyclerViews = new RecyclerView[]{
                recyclerViewAlerts,
                recyclerViewHourlyForecast,
                recyclerViewAirQuality
        };

        CardView[] cardViews = new CardView[]{
                cardSunrise,
                cardSunset
        };
//        endregion

//        region::Setup data
        if (!settings[0].getQ().isEmpty()) {
            textViewCityName.setText(settings[0].getCity());

            try {
                loadCurrentWeather(
                        weatherManager,
                        settings[0],
                        textViewsCurrent,
                        mainLayout,
                        imageViewWeatherPromo,
                        context,
                        cardViews);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                loadData(
                        weatherManager,
                        settings[0],
                        recyclerViews,
                        textViewsCurrent,
                        alertsL,
                        mainLayout,
                        context,
                        pieChart
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {

                loadDailyForecast(
                        weatherManager,
                        settings[0],
                        recyclerViewDailyForecast,
                        mainLayout,
                        context,
                        activity,
                        3
                );

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            Snackbar
                    .make(context, mainLayout, "Укажите местоположение", Snackbar.LENGTH_SHORT)
                    .setTextColor(Color.parseColor("#E9D130"))
                    .setBackgroundTint(Color.parseColor("#6C621E"))
                    .show();

            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(MainActivity.this, LocationActivity.class));

//            multiAutoCompleteTextView.setVisibility(View.VISIBLE);
//            multiAutoCompleteTextView.setHint("где смотреть погоду");
//            multiAutoCompleteTextView.requestFocus();
//            showKeyboard(multiAutoCompleteTextView);
        }

        final int[] searchCount = {1};
//        endregion

//        region::Clickers
        imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, context);
            }
        });
        imageViewAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (searchCount[0]) {
                    case 1:
                        multiAutoCompleteTextView.setVisibility(View.VISIBLE);
                        multiAutoCompleteTextView.setHint("где смотреть погоду");
                        multiAutoCompleteTextView.requestFocus();
                        showKeyboard(multiAutoCompleteTextView);
                        searchCount[0]++;
                        break;
                    case 2:
                        if (multiAutoCompleteTextView.getText().toString().length() != 0) {
                            settings[0].putValue("q", multiAutoCompleteTextView.getText().toString());
                            hideKeyboard(multiAutoCompleteTextView);

                            settings[0] = Settings.getUserSettings(context);

                            multiAutoCompleteTextView.setVisibility(View.GONE);
                            textViewCityName.setText(settings[0].getQ());

                            try {
                                loadCurrentWeather(
                                        weatherManager,
                                        settings[0],
                                        textViewsCurrent,
                                        mainLayout,
                                        imageViewWeatherPromo,
                                        context,
                                        cardViews);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                loadData(
                                        weatherManager,
                                        settings[0],
                                        recyclerViews,
                                        textViewsCurrent,
                                        alertsL,
                                        mainLayout,
                                        context,
                                        pieChart
                                );
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            ProgressDialog progressDialog = new ProgressDialog(context);
                            final NativeBulkAdLoader nativeBulkAdLoader = new NativeBulkAdLoader(context);
                            final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder(AdsConfig.NATIVE_ADS_ID).build();
                            nativeBulkAdLoader.loadAds(nativeAdRequestConfiguration, 1);
                            nativeBulkAdLoader.setNativeBulkAdLoadListener(new NativeBulkAdLoadListener() {
                                @Override
                                public void onAdsLoaded(@NonNull final List<NativeAd> nativeAds) {
                                    try {
                                        for (final NativeAd nativeAd : nativeAds) {
                                            final NativeAdViewBinder nativeAdViewBinder = new NativeAdViewBinder.Builder(mNativeAdView)
                                                    .setAgeView(age)
                                                    .setBodyView(bodyView)
                                                    .setCallToActionView(call_to_action)
                                                    .setDomainView(domain)
                                                    //.setFaviconView(notesViewHolder.favicon)
                                                    .setFeedbackView(imageViewFeedback)
                                                    .setIconView(favicon)
                                                    .setMediaView(mediaView)
                                                    .setPriceView(priceView)
                                                    //.setRatingView((MyRatingView) findViewById(R.id.rating))
                                                    //.setReviewCountView((TextView) findViewById(R.id.review_count))
                                                    .setSponsoredView(storeView)
                                                    .setTitleView(tvHeadline)
                                                    .setWarningView(warning)
                                                    .build();

                                            try {
                                                nativeAd.bindNativeAd(nativeAdViewBinder);
                                                nativeAd.setNativeAdEventListener(new NativeAdEventListener() {
                                                    @Override
                                                    public void onAdClicked() {

                                                    }

                                                    @Override
                                                    public void onLeftApplication() {

                                                    }

                                                    @Override
                                                    public void onReturnedToApplication() {

                                                    }

                                                    @Override
                                                    public void onImpression(@Nullable ImpressionData impressionData) {

                                                    }
                                                });
                                                mNativeAdView.setVisibility(View.VISIBLE);
                                                adsCard.setVisibility(View.VISIBLE);
                                                progressDialog.dismiss();
                                            } catch (final NativeAdException exception) {
                                                Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onAdsFailedToLoad(@NonNull final AdRequestError error) {
                                    adsCard.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            multiAutoCompleteTextView.setError("Где смотреть погоду?");
                        }
                        searchCount[0]--;
                        break;
                }
            }
        });
        multiAutoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && multiAutoCompleteTextView.getText().toString().length() != 0) {
                    settings[0].putValue("q", multiAutoCompleteTextView.getText().toString());
                    hideKeyboard(multiAutoCompleteTextView);

                    settings[0] = Settings.getUserSettings(context);

                    multiAutoCompleteTextView.setVisibility(View.GONE);
                    textViewCityName.setText(settings[0].getQ());

                    try {
                        loadCurrentWeather(
                                weatherManager,
                                settings[0],
                                textViewsCurrent,
                                mainLayout,
                                imageViewWeatherPromo,
                                context,
                                cardViews);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        loadData(
                                weatherManager,
                                settings[0],
                                recyclerViews,
                                textViewsCurrent,
                                alertsL,
                                mainLayout,
                                context,
                                pieChart
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    ProgressDialog progressDialog = new ProgressDialog(context);
                    final NativeBulkAdLoader nativeBulkAdLoader = new NativeBulkAdLoader(context);
                    final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder(AdsConfig.NATIVE_ADS_ID).build();
                    nativeBulkAdLoader.loadAds(nativeAdRequestConfiguration, 1);
                    nativeBulkAdLoader.setNativeBulkAdLoadListener(new NativeBulkAdLoadListener() {
                        @Override
                        public void onAdsLoaded(@NonNull final List<NativeAd> nativeAds) {
                            try {
                                for (final NativeAd nativeAd : nativeAds) {
                                    final NativeAdViewBinder nativeAdViewBinder = new NativeAdViewBinder.Builder(mNativeAdView)
                                            .setAgeView(age)
                                            .setBodyView(bodyView)
                                            .setCallToActionView(call_to_action)
                                            .setDomainView(domain)
                                            //.setFaviconView(notesViewHolder.favicon)
                                            .setFeedbackView(imageViewFeedback)
                                            .setIconView(favicon)
                                            .setMediaView(mediaView)
                                            .setPriceView(priceView)
                                            //.setRatingView((MyRatingView) findViewById(R.id.rating))
                                            //.setReviewCountView((TextView) findViewById(R.id.review_count))
                                            .setSponsoredView(storeView)
                                            .setTitleView(tvHeadline)
                                            .setWarningView(warning)
                                            .build();

                                    try {
                                        nativeAd.bindNativeAd(nativeAdViewBinder);
                                        nativeAd.setNativeAdEventListener(new NativeAdEventListener() {
                                            @Override
                                            public void onAdClicked() {

                                            }

                                            @Override
                                            public void onLeftApplication() {

                                            }

                                            @Override
                                            public void onReturnedToApplication() {

                                            }

                                            @Override
                                            public void onImpression(@Nullable ImpressionData impressionData) {

                                            }
                                        });
                                        mNativeAdView.setVisibility(View.VISIBLE);
                                        adsCard.setVisibility(View.VISIBLE);
                                        progressDialog.dismiss();
                                    } catch (final NativeAdException exception) {
                                        Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onAdsFailedToLoad(@NonNull final AdRequestError error) {
                            adsCard.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                } else {
                    multiAutoCompleteTextView.setError("Где смотреть погоду?");
                }
                return false;
            }
        });
//        endregion
    }

    private void showPopupMenu(View v, Context context) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.main_menu);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.map:
                                startActivity(new Intent(context, RadarActivity.class).putExtra("showAppOpen", false));
                                finish();
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.settings:
                                startActivity(new Intent(context, SettingsActivity.class).putExtra("showAppOpen", false));
                                finish();
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.locations:
                                startActivity(new Intent(context, LocationActivity.class).putExtra("showAppOpen", false));
                                finish();
                                overridePendingTransition(0, 0);
                                break;
                            case R.id.about:
                                startActivity(new Intent(context, AboutActivity.class).putExtra("showAppOpen", false));
                                finish();
                                overridePendingTransition(0, 0);
                                break;
                        }
                        return false;
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }

    private String setupUVIndex(int UVIndex) {
        String str = "";

        if (UVIndex >= 1 && UVIndex <= 2)
            str = "Низкий";
        if (UVIndex >= 3 && UVIndex <= 5)
            str = "Средний";
        if (UVIndex >= 6 && UVIndex <= 7)
            str = "Высокий";
        if (UVIndex >= 8 && UVIndex <= 10)
            str = "Очень высокий";
        if (UVIndex >= 11)
            str = "Экстремальный";

        return str;
    }

    private void showKeyboard(View target) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(target, 0);
    }

    private void hideKeyboard(View target) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
    }

    private int convertToTheme(int i) {
        switch (i) {
            case 1:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case 2:
                return AppCompatDelegate.MODE_NIGHT_YES;
            case -1:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    //    region::Load weather data
    private void loadCurrentWeather(WeatherManager weatherManager, Settings settings, TextView[] textViews, LinearLayout mainLayout, ImageView weatherPromo, Context context, CardView[] cardViews) throws Exception {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        weatherManager.getCurrentWeather(settings.getQ(), "4e228e1be370d9d0d02284441d30cf0b", settings.getSystem(), settings.getLang()).enqueue(new Callback<CurrentWeather>() {
            @SuppressLint({"SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<CurrentWeather> call, @NonNull Response<CurrentWeather> response) {
                try {
                    String str = "";
                    //assert response.body() != null;
                    textViews[0].setText(Integer.toString(Math.toIntExact(Math.round(response.body().getMain().getTemp()))) + "°");

                    str = str + Integer.toString(Math.toIntExact(Math.round(response.body().getMain().getTempMin()))) + "°" + "/" + Integer.toString(Math.toIntExact(Math.round(response.body().getMain().getTempMax()))) + "°";
                    str = str + " Ощущается как " + Integer.toString(Math.toIntExact(Math.round(response.body().getMain().getFeelsLike()))) + "°";

                    textViews[1].setText(str);

                    String iconId = response.body().getWeather().get(0).getIcon();
                    String build = Link.Builder.build(iconId);
                    Glide.with(context).asBitmap().load(build).into(weatherPromo);

                    //assert response.body() != null;
                    textViews[2].setText(response.body().getWeather().get(0).getDescription());

                    //Another data
                    long sr = (long) response.body().getSys().getSunrise() * 1000;
                    long ss = (long) response.body().getSys().getSunset() * 1000;

                    String sunrise = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(sr));
                    String sunset = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(ss));

                    textViews[3].setText(sunrise);
                    textViews[4].setText(sunset);

                    Calendar calendar = Calendar.getInstance();

                    calendar.setTimeInMillis(sr);
                    if (calendar.get(Calendar.MINUTE) >= 50) {
                        cardViews[0].getLayoutParams().height = calendar.get(Calendar.MINUTE);
                    } else {
                        cardViews[0].getLayoutParams().height = calendar.get(Calendar.MINUTE) * 2;
                    }

                    calendar.setTimeInMillis(ss);
                    if (calendar.get(Calendar.MINUTE) >= 50) {
                        cardViews[1].getLayoutParams().height = calendar.get(Calendar.MINUTE);
                    } else {
                        cardViews[1].getLayoutParams().height = calendar.get(Calendar.MINUTE) * 2;
                    }

                    textViews[5].setText(response.body().getMain().getHumidity() + "%");
                    textViews[7].setText("Давление " + Integer.toString((int) (response.body().getMain().getPressure() * 0.75)) + " мм рт ст");

                    if (settings.getSystem().equals("metric")) {
                        textViews[6].setText(response.body().getWind().getSpeed() + " м/с");
                    } else {
                        textViews[6].setText(response.body().getWind().getSpeed() + " миль/час");
                    }

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar
                            .make(context, mainLayout, "Ошибка: " + settings.getQ() + "", Snackbar.LENGTH_SHORT)
                            .setTextColor(getResources().getColor(R.color.md_theme_dark_error))
                            .setBackgroundTint(getResources().getColor(R.color.md_theme_dark_errorContainer))
                            .show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CurrentWeather> call, @NonNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void loadData(WeatherManager weatherManager, Settings settings, RecyclerView[] recyclerViews, TextView[] textViews, LinearLayout alertsL, LinearLayout mainLayout, Context context, PieChart pieChart) throws Exception {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        weatherManager.getDailyWeatherNew(settings.getQ(), "4e228e1be370d9d0d02284441d30cf0b", settings.getSystem(), settings.getLang()).enqueue(new Callback<MainWeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<MainWeatherResponse> call, @NonNull Response<MainWeatherResponse> response) {
                assert response.body() != null;

                WeatherForecastAdapter weatherForecastAdapter = new WeatherForecastAdapter(response.body().getWeatherList(), context);
                weatherForecastAdapter.notifyDataSetChanged();
                recyclerViews[1].setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                recyclerViews[1].setHasFixedSize(true);
                recyclerViews[1].setAdapter(weatherForecastAdapter);

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<MainWeatherResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

//        weatherManager.getDailyWeather("863d89dfe5734725a09155301221203", settings.getQ(), 1, "yes", "yes", settings.getLang()).enqueue(new Callback<ForecastWeather>() {
//            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
//            @Override
//            public void onResponse(@NonNull Call<ForecastWeather> call, @NonNull Response<ForecastWeather> response) {
//                ForecastWeather body = response.body();
//
//                try {
//                    String linkToImage = "https:" + Objects.requireNonNull(body).getCurrent().getCondition().getIcon();
//                    String[] split = body.getCurrent().getLastUpdated().split(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis())));
//
//                    List<Forecastday> forecastDays = new ArrayList<>(Objects.requireNonNull(body).getForecast().getForecastday());
//                    List<Hour> hours = new ArrayList<>();
//                    List<Alert> alerts = new ArrayList<>(body.getAlerts().getAlert());
//
////                    alerts.clear();
////                    alerts.addAll(body.getAlerts().getAlert());
////
//                    for (int i = 0; i < alerts.size(); i++) {
//                        Pattern pattern = Pattern.compile(
//                                "[" + "A-Za-z" + "\\d" + "\\s" + "\\p{Punct}" + "]" + "*"
//                        );
//                        Matcher matcher = pattern.matcher(alerts.get(i).getEvent());
//
//                        if (matcher.matches() && Settings.getUserSettings(MainActivity.this).getLang().equals(Locale.UK.getISO3Language())) {
//
//                        }
//                        if (!Settings.getUserSettings(MainActivity.this).getLang().equals(Locale.UK.getISO3Language()) && matcher.matches()) {
//                            alerts.remove(i);
//                        }
//                    }
//
//                    if (alerts.size() != 0) {
//                        alertsL.setVisibility(View.VISIBLE);
//                    } else {
//                        alertsL.setVisibility(View.GONE);
//                    }
//
//                    AlertAdapter alertAdapter = new AlertAdapter(alerts, context);
//                    alertAdapter.notifyDataSetChanged();
//
//                    recyclerViews[0].setLayoutManager(new LinearLayoutManager(context));
//                    recyclerViews[0].setHasFixedSize(true);
//                    recyclerViews[0].setAdapter(alertAdapter);
//
//                    for (int i = 0; i < 1; i++) {
//                        hours.addAll(forecastDays.get(i).getHour());
//                    }
////
//////                    if (body.getCurrent().getIsDay() == 1) {
//////                        imageViewMainPromo.setImageResource(R.drawable.ic_wb_sunny);
//////                        isDay = true;
//////                    }
//////                    if (body.getCurrent().getIsDay() == 0) {
//////                        imageViewMainPromo.setImageResource(R.drawable.ic_nights_stay);
//////                        isDay = false;
//////                    }
//////
//////                    if (split.length == 1) {
//////                        textViewUpdateTime.setText(split[0]);
//////                    } else {
//////                        textViewUpdateTime.setText(split[1]);
//////                    }
////
//                    WeatherForecastAdapter weatherForecastAdapter = new WeatherForecastAdapter(hours, context);
//                    weatherForecastAdapter.notifyDataSetChanged();
//
//                    recyclerViews[1].setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                    recyclerViews[1].setHasFixedSize(true);
//                    recyclerViews[1].setAdapter(weatherForecastAdapter);
//
////                    double d = Math.round(body.getCurrent().getUv() * 10);
////                    d = d / 10;
////
////                    String uv = Double.toString(d);
//                    int uv = body.getCurrent().getUv().intValue();
//                    textViews[6].setText(setupUVIndex(uv));
////                    String windSpeed = "";
////                    String visibility;
////
////                    windSpeed = Integer.toString(Math.toIntExact(Math.round(body.getCurrent().getWindKph()))) + " км/ч";
////                    visibility = Integer.toString(Math.toIntExact(Math.round(body.getCurrent().getVisKm()))) + " км";
////
////                    //Another data
////                    String humidity = Integer.toString(body.getCurrent().getHumidity()) + "%";
////                    String cloud = Integer.toString(body.getCurrent().getCloud()) + "%";
////
//////                    dataList1.add(new Data("УФ индекс", uv, R.drawable.ic_uv));
//////                    dataList1.add(new Data("Скорость ветра", windSpeed, R.drawable.ic_wind));
//////                    dataList1.add(new Data("Облачность", cloud, R.drawable.ic_cloudy1));
//////                    dataList1.add(new Data("Влажность", humidity, R.drawable.ic_humidity));
////
////                    //PieChart
////                    AirQuality airQuality = body.getCurrent().getAirQuality();
////
////                    Double co = airQuality.getCo();
////                    Double no2 = airQuality.getNo2();
////                    Double so2 = airQuality.getSo2();
////                    Double pm10 = airQuality.getPm10();
////                    Double pm25 = airQuality.getPm25();
////                    Double o3 = airQuality.getO3();
////                    double dSum = co + no2 + so2 + pm10 + pm25 + o3;
////
////                    float sum = new BigDecimal(dSum).floatValue();
////
////                    //pieChart.setAutoCenterInSlice(false);
////
////                    float[] values = new float[6];
////                    String[] names = new String[6];
////                    int[] colors;
////
////
//////                    pieChart.addPieSlice(new PieModel("CO", new BigDecimal(co).floatValue(), getResources().getColor(R.color.blue_700)));
//////                    pieChart.addPieSlice(new PieModel("NO2", new BigDecimal(no2).floatValue(), getResources().getColor(R.color.orange_700)));
//////                    pieChart.addPieSlice(new PieModel("SO2", new BigDecimal(so2).floatValue(), getResources().getColor(R.color.green_700)));
//////
//////                    pieChart.addPieSlice(new PieModel("PM10", new BigDecimal(pm10).floatValue(), getResources().getColor(R.color.red_700)));
//////                    pieChart.addPieSlice(new PieModel("PM25", new BigDecimal(pm25).floatValue(), getResources().getColor(R.color.purple_700)));
//////                    pieChart.addPieSlice(new PieModel("O3", new BigDecimal(o3).floatValue(), getResources().getColor(R.color.blue_400)));
//////
//////                    pieChart.startAnimation();
////
////                    values[0] = new BigDecimal((co * 100) / sum).floatValue();
////                    values[1] = new BigDecimal((no2 * 100) / sum).floatValue();
////                    values[2] = new BigDecimal((so2 * 100) / sum).floatValue();
////                    values[3] = new BigDecimal((pm10 * 100) / sum).floatValue();
////                    values[4] = new BigDecimal((pm25 * 100) / sum).floatValue();
////                    values[5] = new BigDecimal((o3 * 100) / sum).floatValue();
////
////                    colors = new int[]{
////                            getResources().getColor(R.color.blue_700),
////                            getResources().getColor(R.color.orange_700),
////                            getResources().getColor(R.color.green_700),
////                            getResources().getColor(R.color.red_700),
////                            getResources().getColor(R.color.purple_700),
////                            getResources().getColor(R.color.blue_400)};
////
////                    names = new String[]{"CO", "NO2", "SO2", "PM10", "PM25", "O3"};
////
////                    airQualityDataList.clear();
////
////                    for (int i = 0; i < 6; i++) {
////                        airQualityDataList.add(new AirQualityData(names[i], colors[i], values[i]));
////                    }
////
////                    AirQualityAdapter airQualityAdapter = new AirQualityAdapter(airQualityDataList, MainActivity.this);
////                    airQualityAdapter.notifyDataSetChanged();
////                    recyclerAirQ.setHasFixedSize(true);
////                    recyclerAirQ.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
////                    recyclerAirQ.setAdapter(airQualityAdapter);
//
//                    //PieChart
//                    AirQuality airQuality = body.getCurrent().getAirQuality();
//                    List<AirQualityData> airQualityDataList = new ArrayList<>();
//
//                    Double co = airQuality.getCo();
//                    Double no2 = airQuality.getNo2();
//                    Double so2 = airQuality.getSo2();
//                    Double pm10 = airQuality.getPm10();
//                    Double pm25 = airQuality.getPm25();
//                    Double o3 = airQuality.getO3();
//                    double dSum = co + no2 + so2 + pm10 + pm25 + o3;
//
//                    float sum = new BigDecimal(dSum).floatValue();
//
//                    String[] names = new String[]{
//                            "Угарный газ (CO)",
//                            "Диоксид азота (NO₂)",
//                            "Диоксид серы (SO₂)",
//                            "Твердые микрочастицы (PM₁₀)",
//                            "Твердые микрочастицы (PM₂ ₅)",
//                            "Озон (O₃)"};
//
//                    int[] colors = new int[]{
//                            getResources().getColor(R.color.blue_700),
//                            getResources().getColor(R.color.orange_700),
//                            getResources().getColor(R.color.green_700),
//                            getResources().getColor(R.color.red_700),
//                            getResources().getColor(R.color.purple_700),
//                            getResources().getColor(R.color.blue_400)};
//
//                    pieChart.clearChart();
//                    pieChart.setAutoCenterInSlice(false);
//
//                    pieChart.addPieSlice(new PieModel(names[0], new BigDecimal(co).floatValue(), colors[0]));
//                    pieChart.addPieSlice(new PieModel(names[1], new BigDecimal(no2).floatValue(), colors[1]));
//                    pieChart.addPieSlice(new PieModel(names[2], new BigDecimal(so2).floatValue(), colors[2]));
//
//                    pieChart.addPieSlice(new PieModel(names[3], new BigDecimal(pm10).floatValue(), colors[3]));
//                    pieChart.addPieSlice(new PieModel(names[4], new BigDecimal(pm25).floatValue(), colors[4]));
//                    pieChart.addPieSlice(new PieModel(names[5], new BigDecimal(o3).floatValue(), colors[5]));
//
//                    pieChart.startAnimation();
//
//                    float[] values = new float[6];
//
//                    values[0] = new BigDecimal((co * 100) / sum).floatValue();
//                    values[1] = new BigDecimal((no2 * 100) / sum).floatValue();
//                    values[2] = new BigDecimal((so2 * 100) / sum).floatValue();
//                    values[3] = new BigDecimal((pm10 * 100) / sum).floatValue();
//                    values[4] = new BigDecimal((pm25 * 100) / sum).floatValue();
//                    values[5] = new BigDecimal((o3 * 100) / sum).floatValue();
//
//                    for (int i = 0; i < 6; i++) {
//                        airQualityDataList.add(new AirQualityData(names[i], colors[i], values[i]));
//                    }
//
//                    AirQualityAdapter airQualityAdapter = new AirQualityAdapter(airQualityDataList, context);
//                    airQualityAdapter.notifyDataSetChanged();
//                    recyclerViews[2].setHasFixedSize(true);
//                    recyclerViews[2].setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//                    recyclerViews[2].setAdapter(airQualityAdapter);
//
//                    progressDialog.dismiss();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Snackbar
//                            .make(MainActivity.this, mainLayout, "Ошибка: " + e.toString(), Snackbar.LENGTH_SHORT)
//                            .setTextColor(getResources().getColor(R.color.md_theme_dark_error))
//                            .setBackgroundTint(getResources().getColor(R.color.md_theme_dark_errorContainer))
//                            .show();
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ForecastWeather> call, @NonNull Throwable t) {
//                progressDialog.dismiss();
//            }
//        });
    }

    private void loadDailyForecast(WeatherManager weatherManager, Settings settings, RecyclerView recyclerView, LinearLayout mainLayout, Context context, Activity activity, int days) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        weatherManager.getDailyWeather(ApiData.API_KEY_2, settings.getQ(), days, "no", "no", settings.getLang()).enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(@NonNull Call<ForecastWeather> call, @NonNull Response<ForecastWeather> response) {
                ForecastWeather body = response.body();

                try {
                    List<Forecastday> forecastDays = new ArrayList<>(Objects.requireNonNull(body).getForecast().getForecastday());
                    List<Pair<Day, String>> list = new ArrayList<>();

                    for (int i = 0; i < days; i++) {
                        list.add(new Pair<>(forecastDays.get(i).getDay(), forecastDays.get(i).getDate()));
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(new DayAdapter(context, activity, list));

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar
                            .make(MainActivity.this, mainLayout, "Ошибка: " + e.toString(), Snackbar.LENGTH_SHORT)
                            .setTextColor(getResources().getColor(R.color.md_theme_dark_error))
                            .setBackgroundTint(getResources().getColor(R.color.md_theme_dark_errorContainer))
                            .show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForecastWeather> call, @NonNull Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
//    endregion
}