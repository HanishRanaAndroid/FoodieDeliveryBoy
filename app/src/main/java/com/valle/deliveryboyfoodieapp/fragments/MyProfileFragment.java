package com.valle.deliveryboyfoodieapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.activity.HomeTabActivity;
import com.valle.deliveryboyfoodieapp.base.BaseFragment;

import butterknife.OnClick;

public class MyProfileFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
    }

    @OnClick(R.id.tvEditProfile)
    void OnClicktvEditProfile() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new UpdateProfileFragment(), null);
    }

}
