package com.valle.deliveryboyfoodieapp.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.base.BaseFragment;
import com.valle.deliveryboyfoodieapp.models.ChangePasswordModel;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class ChangePasswordFragment extends BaseFragment implements NetworkResponceListener {

    @BindView(R.id.etOldPassword)
    AppCompatEditText etOldPassword;

    @BindView(R.id.etNewPassword)
    AppCompatEditText etNewPassword;

    @BindView(R.id.etConfPassword)
    AppCompatEditText etConfPassword;

    @BindView(R.id.ivOldPassword)
    AppCompatImageView ivOldPassword;

    @BindView(R.id.ivNewPassword)
    AppCompatImageView ivNewPassword;

    @BindView(R.id.ivConfirmPassword)
    AppCompatImageView ivConfirmPassword;

    private String Yes = "Y";
    private String No = "N";

    @OnClick({R.id.ivOldPassword, R.id.ivNewPassword, R.id.ivConfirmPassword})
    void OnClickivPasswordVisibility(View view) {
        switch (view.getId()) {
            case R.id.ivOldPassword:
                if (etOldPassword.getTag().toString().equalsIgnoreCase(Yes)) {
                    setVisiblityNo(etOldPassword, ivOldPassword);
                } else {
                    setVisiblityYes(etOldPassword, ivOldPassword);
                }
                break;
            case R.id.ivNewPassword:
                if (etNewPassword.getTag().toString().equalsIgnoreCase(Yes)) {
                    setVisiblityNo(etNewPassword, ivNewPassword);
                } else {
                    setVisiblityYes(etNewPassword, ivNewPassword);
                }
                break;
            case R.id.ivConfirmPassword:
                if (etConfPassword.getTag().toString().equalsIgnoreCase(Yes)) {
                    setVisiblityNo(etConfPassword, ivConfirmPassword);
                } else {
                    setVisiblityYes(etConfPassword, ivConfirmPassword);
                }
                break;
        }
    }

    private void setVisiblityNo(AppCompatEditText editText, AppCompatImageView imageView) {
        editText.setTag(No);
        editText.setTransformationMethod(null);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.pass_show));
    }

    private void setVisiblityYes(AppCompatEditText editText, AppCompatImageView imageView) {
        editText.setTransformationMethod(new PasswordTransformationMethod());
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.pass_hide));
        editText.setTag(Yes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
        etOldPassword.setTag(No);
        etNewPassword.setTag(No);
        etConfPassword.setTag(No);
    }

    @OnClick(R.id.tvChnagePassword)
    void OnClicktvChnagePassword() {
        String oldPassword  = etOldPassword.getText().toString();
        String newPassword  = etNewPassword.getText().toString();
        String confPassword = etConfPassword.getText().toString();

        if (TextUtils.isEmpty(oldPassword)) {
            etOldPassword.setError(getResources().getString(R.string.please_enter_your_old_password));
            etOldPassword.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError(getResources().getString(R.string.please_enter_new_password));
            etNewPassword.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(confPassword)) {
            etConfPassword.setError(getResources().getString(R.string.please_enter_conf_password));
            etConfPassword.setFocusable(true);
            return;
        }

        if (!newPassword.equalsIgnoreCase(confPassword)) {
            etConfPassword.setError(getResources().getString(R.string.password_not_matched));
            etConfPassword.setFocusable(true);
            return;
        }

        showProgressDialog(getActivity());
        makeHttpCall(this, Apis.CHNAGE_PASSWORD, getRetrofitInterface().changePassword(new SharedPrefModule(getActivity()).getUserId(),
                oldPassword, newPassword));

    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());

        switch (url) {

            case Apis.CHNAGE_PASSWORD:
                try {
                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        ChangePasswordModel changePasswordModel = new Gson().fromJson(responce, ChangePasswordModel.class);
                        Toast.makeText(getActivity(), changePasswordModel.response.msg, Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    private void clearFields() {
        etOldPassword.setText("");
        etNewPassword.setText("");
        etConfPassword.setText("");
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(getActivity());
    }

}
