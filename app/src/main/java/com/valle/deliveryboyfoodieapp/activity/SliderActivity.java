package com.valle.deliveryboyfoodieapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.base.BaseActivity;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;

public class SliderActivity extends BaseActivity implements NetworkResponceListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
       /* showProgressDialog(this);
        makeHttpCall(this, Apis.HOME_PAGE, getRetrofitInterface().getHomePageData());*/

        View.OnClickListener onClickListener = v -> {
            startActivity(new Intent(this, LoginActivity.class));
        };
        findViewById(R.id.tvLogin).setOnClickListener(onClickListener);

        findViewById(R.id.tvSignUp).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        findViewById(R.id.tvSkip).setOnClickListener(onClickListener);
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(SliderActivity.this);
        Toast.makeText(SliderActivity.this, "" + responce, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(SliderActivity.this);
        Toast.makeText(SliderActivity.this, "failure", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        exitFromApp(SliderActivity.this);
    }
}
