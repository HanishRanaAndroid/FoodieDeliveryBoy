package com.valle.deliveryboyfoodieapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.activity.HomeTabActivity;
import com.valle.deliveryboyfoodieapp.activity.LoginActivity;
import com.valle.deliveryboyfoodieapp.base.BaseFragment;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;

import butterknife.OnClick;

public class SettingFragment extends BaseFragment {

    private final String ON = "ON";
    private final String OFF = "OFF";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
    }

    @OnClick(R.id.rlNotificationSetting)
    void OnCLickrlNotificationSetting() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_notification_setting);
        dialog.setCancelable(true);
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.setCancelable(true);
        dialog.getWindow().setLayout((6 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        Switch notificationSwitch = dialog.findViewById(R.id.notificationSwitch);

        notificationSwitch.setChecked(!TextUtils.isEmpty(new SharedPrefModule(getActivity()).getnotification()));

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                new SharedPrefModule(getActivity()).setNotification(ON);
            } else {
                new SharedPrefModule(getActivity()).setNotification("");
            }
        });

        dialog.show();
    }

    @OnClick(R.id.rlChangePassword)
    void OnClickrlChangePassword() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new ChangePasswordFragment(), null);
    }

    @OnClick(R.id.rlEditProfile)
    void OnClickrlEditProfile() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new UpdateProfileFragment(), null);
    }

    @OnClick(R.id.rlPrivacyPolicy)
    void OnclickrlPrivacyPolicy() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new PrivacyPolicyFragment(), null);
    }

    @OnClick(R.id.rlTermAndCondition)
    void OnClickrlTermAndCondition() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new TermAndConditonFragment(), null);
    }

    @OnClick({R.id.tvLogout, R.id.ivLogout})
    void OnclicktvLogout() {
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.logout_msg))
                .setMessage(getResources().getString(R.string.are_you_sure)).setPositiveButton(
                getResources().getString(R.string.yes), (dialog, which) -> {
                    new SharedPrefModule(getActivity()).setUserId("");
                    new SharedPrefModule(getActivity()).setUserLoginResponse("");
                    dialog.dismiss();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }).setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss()).create().show();
    }
}
