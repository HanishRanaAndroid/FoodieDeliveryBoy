package com.valle.deliveryboyfoodieapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.base.BaseActivity;
import com.valle.deliveryboyfoodieapp.models.OTPResendModel;
import com.valle.deliveryboyfoodieapp.models.RegisterModel;
import com.valle.deliveryboyfoodieapp.models.VerifyModel;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.utils.CommonUtils;
import com.valle.deliveryboyfoodieapp.utils.OtpEditText;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class OTPVerificationActivity extends BaseActivity implements NetworkResponceListener {

    @BindView(R.id.etOtp)
    OtpEditText etOtp;

    @BindView(R.id.tvPhoneNumber)
    AppCompatTextView tvPhoneNumber;

    private RegisterModel registerModel;

    private String validationCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        bindView(this);

        registerModel = new Gson().fromJson(getIntent().getStringExtra("responce"), RegisterModel.class);
        validationCode = registerModel.response.UserInfo.Validation_Code;
        tvPhoneNumber.setText(!TextUtils.isEmpty(registerModel.response.UserInfo.Mobile) ? registerModel.response.UserInfo.Mobile : "");
        //etOtp.setText(validationCode);
        etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    CommonUtils.hideKeyboard(OTPVerificationActivity.this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.tvVerifyOtp)
    void OnClicktvVerifyOtp() {
        String OTP = etOtp.getText().toString();

        if (TextUtils.isEmpty(OTP)) {
            Toast.makeText(OTPVerificationActivity.this, getResources().getString(R.string.plz_enter_otp), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!OTP.equalsIgnoreCase(validationCode)) {
            Toast.makeText(OTPVerificationActivity.this, getResources().getString(R.string.plz_entr_valid_code), Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog(OTPVerificationActivity.this);
        makeHttpCall(OTPVerificationActivity.this, Apis.VERIFY_ACCOUNT, getRetrofitInterface().verifyAccount(registerModel.response.UserInfo.Mobile, OTP, "Review"));

    }

    @OnClick(R.id.tvResendOTP)
    void OnClicktvResendOTP() {
        showProgressDialog(OTPVerificationActivity.this);
        makeHttpCall(OTPVerificationActivity.this, Apis.SEND_OTP, getRetrofitInterface().sendOTPtoUser(registerModel.response.UserInfo.Mobile));
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(OTPVerificationActivity.this);

        switch (url) {
            case Apis.SEND_OTP:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OTPResendModel otpResendModel = new Gson().fromJson(responce, OTPResendModel.class);
                        Toast.makeText(OTPVerificationActivity.this, otpResendModel.response.msg, Toast.LENGTH_SHORT).show();
                        validationCode = otpResendModel.response.validationCode;
                    } else {
                        Toast.makeText(OTPVerificationActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.VERIFY_ACCOUNT:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        VerifyModel verifyModel = new Gson().fromJson(responce, VerifyModel.class);
                        new AlertDialog.Builder(OTPVerificationActivity.this).setTitle(getResources().getString(R.string.alert)).setMessage("Gracias por registrarte con valle food. su cuenta ha estado en proceso Te avisaremos pronto.").setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(OTPVerificationActivity.this, LoginActivity.class));
                                dialog.dismiss();
                            }
                        }).create().show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(OTPVerificationActivity.this);
    }

}
