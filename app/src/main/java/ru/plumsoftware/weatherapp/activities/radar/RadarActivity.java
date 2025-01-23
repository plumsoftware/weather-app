package ru.plumsoftware.weatherapp.activities.radar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.activities.locations.LocationActivity;
import ru.plumsoftware.weatherapp.activities.main.MainActivity;
import ru.plumsoftware.weatherapp.data.Settings;
import ru.plumsoftware.weatherapp.model.AdsConfig;

public class RadarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        WindowManager windowManager = (WindowManager) RadarActivity.this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

//        region::Views
        ImageView imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        ImageView imageViewMap = (ImageView) findViewById(R.id.imageViewMap);
        BannerAdView mBannerAdView = (BannerAdView) findViewById(R.id.banner_ad_view);
//        endregion

//        region::Setup map
        Glide.with(RadarActivity.this).load("https://tile.openweathermap.org/map/temp_new/1/1/1.png?appid=8babcdab0f80b414d35b6d4b0f3e752e").into(imageViewMap);

        mBannerAdView.setAdUnitId(AdsConfig.BANNER_ADS_ID);
        mBannerAdView.setAdSize(BannerAdSize.inlineSize(RadarActivity.this, displayMetrics.widthPixels, 160));

        AdRequest adRequest = new AdRequest.Builder().build();

        mBannerAdView.setBannerAdEventListener(new BannerAdEventListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                Toast.makeText(RadarActivity.this, adRequestError.getDescription().toString(), Toast.LENGTH_SHORT).show();
            }

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

        mBannerAdView.loadAd(adRequest);
//            endregion

//        region::Clickers
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//        endregion
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, MainActivity.class).putExtra("showAppOpen", false));
    }
}