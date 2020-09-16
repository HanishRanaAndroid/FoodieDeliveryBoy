package com.valle.deliveryboyfoodieapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.base.BaseActivity;
import com.valle.deliveryboyfoodieapp.models.OTPResendModel;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgetPasswordActivity extends BaseActivity implements NetworkResponceListener {

    @BindView(R.id.etPhoneNumber)
    AppCompatEditText etPhoneNumber;


    private String validationCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        bindView(this);
    }

    @OnClick(R.id.tvSubmitForgetPassword)
    void OnClicktvForgetPassword() {
        String phoneNumber = etPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError(getResources().getString(R.string.plz_enter_your_phone_number));
            etPhoneNumber.setFocusable(true);
            return;
        }

        showProgressDialog(ForgetPasswordActivity.this);
        makeHttpCall(ForgetPasswordActivity.this, Apis.SEND_OTP, getRetrofitInterface().sendOTPtoUser(phoneNumber));
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(ForgetPasswordActivity.this);

        switch (url) {

            case Apis.SEND_OTP:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OTPResendModel otpResendModel = new Gson().fromJson(responce, OTPResendModel.class);
                        Toast.makeText(ForgetPasswordActivity.this, otpResendModel.response.msg, Toast.LENGTH_SHORT).show();
                        validationCode = otpResendModel.response.validationCode;
                        Intent intent = new Intent(ForgetPasswordActivity.this, OTPVerificationChangePasswordActivity.class);
                        intent.putExtra("validationCode", validationCode);
                        intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(ForgetPasswordActivity.this);
    }
}

