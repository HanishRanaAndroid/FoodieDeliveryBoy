package com.valle.deliveryboyfoodieapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.base.BaseActivity;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity implements NetworkResponceListener {

    @BindView(R.id.etDeliveryBoyName)
    AppCompatEditText etDeliveryBoyName;

    @BindView(R.id.tvSTDCode)
    AppCompatTextView tvSTDCode;

    @BindView(R.id.etPhoneNumber)
    AppCompatEditText etPhoneNumber;

    @BindView(R.id.etEmail)
    AppCompatEditText etEmail;

    @BindView(R.id.etPassword)
    AppCompatEditText etPassword;

    @BindView(R.id.etConfPassword)
    AppCompatEditText etConfPassword;

    @BindView(R.id.ivPasswordVisi)
    AppCompatImageView ivPasswordVisi;

    @BindView(R.id.ivConfPassVisi)
    AppCompatImageView ivConfPassVisi;

    private String Yes = "Y";
    private String No = "N";
    private static final String TAG = "RegisterActivity";
    private String deviceToken = "";

    @OnClick(R.id.ivPasswordVisi)
    void OnClickivPasswordVisibility() {
        if (etPassword.getTag().toString().equalsIgnoreCase(Yes)) {
            etPassword.setTag(No);
            etPassword.setTransformationMethod(null);
            ivPasswordVisi.setImageDrawable(getResources().getDrawable(R.drawable.pass_show));
        } else {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            ivPasswordVisi.setImageDrawable(getResources().getDrawable(R.drawable.pass_hide));
            etPassword.setTag(Yes);
        }
    }

    @OnClick(R.id.ivConfPassVisi)
    void OnClickivConfPassVisi() {
        if (etConfPassword.getTag().toString().equalsIgnoreCase(Yes)) {
            etConfPassword.setTag(No);
            etConfPassword.setTransformationMethod(null);
            ivConfPassVisi.setImageDrawable(getResources().getDrawable(R.drawable.pass_show));
        } else {
            etConfPassword.setTransformationMethod(new PasswordTransformationMethod());
            ivConfPassVisi.setImageDrawable(getResources().getDrawable(R.drawable.pass_hide));
            etConfPassword.setTag(Yes);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindView(this);
        etPassword.setTag(No);
        etConfPassword.setTag(No);

        try {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                deviceToken = instanceIdResult.getToken();
                Log.d("LoginScreen", "findViewId: " + deviceToken);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tvDeliveryBoySignup)
    void OnClicktvDeliveryBoySignup() {

        String name = etDeliveryBoyName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confPassword = etConfPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            etDeliveryBoyName.setError(getResources().getString(R.string.plz_enter_your_name));
            etDeliveryBoyName.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError(getResources().getString(R.string.plz_enter_your_phone_number));
            etPhoneNumber.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getResources().getString(R.string.plz_enter_your_email_id));
            etEmail.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getResources().getString(R.string.plz_enter_your_password));
            etPassword.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(confPassword)) {
            etConfPassword.setError(getResources().getString(R.string.plz_enter_your_conf_password));
            etConfPassword.setFocusable(true);
            return;
        }

        if (!password.equalsIgnoreCase(confPassword)) {
            etConfPassword.setError(getResources().getString(R.string.plz_enter_valid_conf_password));
            etConfPassword.setFocusable(true);
            return;
        }



        showProgressDialog(RegisterActivity.this);
        makeHttpCall(this, Apis.REGISTER, getRetrofitInterface().register(name, phoneNumber, email, password, deviceToken, "Delivery_Boy"));

    }

    @OnClick(R.id.tvAlreadyHaveAAccount)
    void OnClicktvAlreadyHaveAAccount() {
        super.onBackPressed();
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(RegisterActivity.this);
        switch (url) {
            case Apis.REGISTER:
                try {
                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        Log.d(TAG, "onSuccess: " + responce);
                        Intent intent = new Intent(RegisterActivity.this, OTPVerificationActivity.class);
                        intent.putExtra("responce", responce);
                        startActivity(intent);
                    } else if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.ERROR)) {
                        new AlertDialog.Builder(RegisterActivity.this).setTitle(getResources().getString(R.string.alert)).setMessage(jsonObject.getString("response").toLowerCase().contains("email") ? "ID de correo electrónico ya registrado. Por favor, elija otro" : jsonObject.getString("response").toLowerCase().contains("mobile") ? "Número de móvil ya registrado, elija otro" : jsonObject.getString("response")).setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss()).create().show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(RegisterActivity.this);
    }
}
