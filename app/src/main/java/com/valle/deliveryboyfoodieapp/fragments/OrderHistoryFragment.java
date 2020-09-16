package com.valle.deliveryboyfoodieapp.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.adapters.FilterListAdapter;
import com.valle.deliveryboyfoodieapp.adapters.OrderHistoryAdapter;
import com.valle.deliveryboyfoodieapp.base.BaseFragment;
import com.valle.deliveryboyfoodieapp.models.FilterHistoryBean;
import com.valle.deliveryboyfoodieapp.models.OrderHistoryModel;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;
import com.valle.deliveryboyfoodieapp.utils.CommonUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OrderHistoryFragment extends BaseFragment implements NetworkResponceListener, FilterListAdapter.FilterListener {

    @BindView(R.id.rlOrderHistory)
    RecyclerView rlOrderHistory;

    @BindView(R.id.etSearch)
    AppCompatEditText etSearch;

    @BindView(R.id.ivClearText)
    AppCompatImageView ivClearText;

    @BindView(R.id.rlMainOrderHistory)
    RelativeLayout rlMainOrderHistory;

    @BindView(R.id.tvNoOrderFound)
    AppCompatTextView tvNoOrderFound;

    private List<String> stringsList = new ArrayList<>();
    private OrderHistoryAdapter orderHistoryAdapter;
    private Dialog dialog;

    public static String filterSelceted = "";
    public static String time = "-1 Days";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(this, view);
        filterSelceted = getActivity().getResources().getString(R.string.yesterday);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rlOrderHistory.setLayoutManager(verticalLayoutManager);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (orderHistoryAdapter == null) {
                    return;
                }

                orderHistoryAdapter.filterData(s.toString());
                if (TextUtils.isEmpty(s.toString())) {
                    ivClearText.setVisibility(View.GONE);
                } else {
                    ivClearText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void OnFilterSelect(FilterHistoryBean strFilter) {
        filterSelceted = strFilter.getName();
        dialog.dismiss();
        time = strFilter.getTime() + " Days";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Delivery_Boy_Id", new SharedPrefModule(getActivity()).getUserId());
            jsonObject.put("Order_Status", "Order Delivered");
            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.ORDER_HISTORY, getRetrofitInterface().getOrdersHistory(jsonObject.toString(), time));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.ivFilter)
    public void OnClickivFilter() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_filter_search);
        dialog.setCancelable(true);
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.setCancelable(true);
        dialog.getWindow().setLayout((6 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        RecyclerView rvFilterList = dialog.findViewById(R.id.rvFilterList);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvFilterList.setLayoutManager(verticalLayoutManager);
        ArrayList<FilterHistoryBean> filterHistoryBeans = new ArrayList<>();
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.yesterday), "-1"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_five_day), "-5"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_ten_day), "-10"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_twenty_days), "-20"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_one_month), "-30"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_two_month), "-60"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_three_month), "-90"));
        FilterListAdapter filterListAdapter = new FilterListAdapter(getActivity(), filterHistoryBeans, this);
        rvFilterList.setAdapter(filterListAdapter);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!CommonUtils.isNetworkAvailable(getActivity())) {
            showSnakBar(rlMainOrderHistory, v -> {
                callHistoryApi();
            });
            return;
        }
        callHistoryApi();
    }

    private void callHistoryApi() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Delivery_Boy_Id", new SharedPrefModule(getActivity()).getUserId());
            jsonObject.put("Order_Status", "Order Delivered");
            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.ORDER_HISTORY, getRetrofitInterface().getOrdersHistory(jsonObject.toString(), time));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.ivClearText)
    void OnClickivClearText() {
        etSearch.setText("");
        ivClearText.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());

        switch (url) {

            case Apis.ORDER_HISTORY:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OrderHistoryModel orderHistoryModel = new Gson().fromJson(responce, OrderHistoryModel.class);
                        handleVisibility(orderHistoryModel.response.orders_Info.size() > 0);
                        if (orderHistoryModel.response.orders_Info.size() > 0) {
                            orderHistoryAdapter = new OrderHistoryAdapter(getActivity(), orderHistoryModel.response.orders_Info);
                            rlOrderHistory.setAdapter(orderHistoryAdapter);
                        }
                    } else {
                        handleVisibility(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(getActivity());
    }

    private void handleVisibility(boolean isTrue) {
        rlOrderHistory.setVisibility(isTrue ? View.VISIBLE : View.GONE);
        tvNoOrderFound.setVisibility(!isTrue ? View.VISIBLE : View.GONE);
    }
}
