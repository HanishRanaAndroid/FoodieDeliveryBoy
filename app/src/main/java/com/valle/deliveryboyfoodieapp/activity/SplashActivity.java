package com.valle.deliveryboyfoodieapp.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 1000;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setSpanishDefault();
        new Handler().postDelayed(() -> {
            if (TextUtils.isEmpty(new SharedPrefModule(SplashActivity.this).getUserLoginResponseData())) {
                startActivity(new Intent(SplashActivity.this, SliderActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, HomeTabActivity.class));
                finish();
            }
            this.fileList();
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void setSpanishDefault() {
        String lang = "es";
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
        res.updateConfiguration(conf, dm);
    }

}
