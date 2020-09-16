package com.valle.deliveryboyfoodieapp.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class OrderPlacedFragment extends BaseFragment {

    @BindView(R.id.tvDetails)
    AppCompatTextView tvDetails;

    @BindView(R.id.vDetails)
    View vDetails;

    @BindView(R.id.tvHelp)
    AppCompatTextView tvHelp;

    @BindView(R.id.vHelp)
    View vHelp;

    @BindView(R.id.tvChat)
    AppCompatTextView tvChat;

    @BindView(R.id.vChat)
    View vChat;

    @BindView(R.id.llChat)
    LinearLayoutCompat llChat;

    @BindView(R.id.llHelp)
    LinearLayoutCompat llHelp;

    @BindView(R.id.llOrderDetails)
    LinearLayoutCompat llOrderDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_placed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
    }

    @OnClick({R.id.tvDetails, R.id.tvHelp, R.id.tvChat})
    void OnClick(View view) {

        if (view == tvDetails) {
            setUnderLineBack(true, false, false);
        } else if (view == tvHelp) {
            setUnderLineBack(false, true, false);
        } else if (view == tvChat) {
            setUnderLineBack(false, false, true);
        }

    }

    private void setUnderLineBack(boolean istvDetails, boolean istvHelp, boolean istvChat) {
        llOrderDetails.setVisibility(istvDetails ? View.VISIBLE : View.GONE);
        vDetails.setVisibility(istvDetails ? View.VISIBLE : View.GONE);
        llHelp.setVisibility(istvHelp ? View.VISIBLE : View.GONE);
        vHelp.setVisibility(istvHelp ? View.VISIBLE : View.GONE);
        llChat.setVisibility(istvChat ? View.VISIBLE : View.GONE);
        vChat.setVisibility(istvChat ? View.VISIBLE : View.GONE);
    }
}
