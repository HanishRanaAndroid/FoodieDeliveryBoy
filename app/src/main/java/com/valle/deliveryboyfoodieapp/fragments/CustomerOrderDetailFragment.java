package com.valle.deliveryboyfoodieapp.fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.activity.HomeTabActivity;
import com.valle.deliveryboyfoodieapp.base.BaseFragment;
import com.valle.deliveryboyfoodieapp.models.OrderHistoryModel;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;
import com.valle.deliveryboyfoodieapp.utils.CommonUtils;
import com.valle.deliveryboyfoodieapp.utils.RoundedImageView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.valle.deliveryboyfoodieapp.utils.CommonUtils.MY_PERMISSIONS_REQUEST_CALL;

public class CustomerOrderDetailFragment extends BaseFragment implements NetworkResponceListener {

    @BindView(R.id.ivRestImage)
    RoundedImageView ivRestImage;

    @BindView(R.id.tvOrderNumber)
    AppCompatTextView tvOrderNumber;

    @BindView(R.id.tvOrderAmount)
    AppCompatTextView tvOrderAmount;

    @BindView(R.id.tvNoOfItems)
    AppCompatTextView tvNoOfItems;

    @BindView(R.id.tvPaymentType)
    AppCompatTextView tvPaymentType;

    @BindView(R.id.tvOrderDate)
    AppCompatTextView tvOrderDate;

    @BindView(R.id.tvRestName)
    AppCompatTextView tvRestName;

    @BindView(R.id.tvRestAddress)
    AppCompatTextView tvRestAddress;

    @BindView(R.id.tvRestPhone)
    AppCompatTextView tvRestPhone;

    @BindView(R.id.ivTrackRestaurant)
    AppCompatImageView ivTrackRestaurant;

    @BindView(R.id.tvCustomerName)
    AppCompatTextView tvCustomerName;

    @BindView(R.id.tvDeliveryAddress)
    AppCompatTextView tvDeliveryAddress;

    @BindView(R.id.tvCustomerPhone)
    AppCompatTextView tvCustomerPhone;

    @BindView(R.id.ivTrackDeliveryLocation)
    AppCompatImageView ivTrackDeliveryLocation;

    @BindView(R.id.llOrderedItems)
    LinearLayoutCompat llOrderedItems;

    @BindView(R.id.tvDeliveryFees)
    AppCompatTextView tvDeliveryFees;

    @BindView(R.id.tvServiceTax)
    AppCompatTextView tvServiceTax;

    @BindView(R.id.tvDiscount)
    AppCompatTextView tvDiscount;

    @BindView(R.id.tvGrandTotal)
    AppCompatTextView tvGrandTotal;

    @BindView(R.id.btOrderPickedUp)
    AppCompatButton btOrderPickedUp;

    @BindView(R.id.btOrderDelivered)
    AppCompatButton btOrderDelivered;

    @BindView(R.id.tvCustomerNote)
    AppCompatTextView tvCustomerNote;

    @BindView(R.id.tvItemsTotal)
    AppCompatTextView tvItemsTotal;

    @BindView(R.id.ivCallCustomerForLocation)
    AppCompatImageView ivCallCustomerForLocation;

    @BindView(R.id.nsMainOrderDetail)
    NestedScrollView nsMainOrderDetail;
    private OrderHistoryModel orderHistoryModel;
    private String strOrderStatus = "";
    private boolean isPaid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
    }

    private void showPaymentDialog() {
        isPaid = false;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_order_bill);
        dialog.setCancelable(true);
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.setCancelable(true);
        dialog.getWindow().setLayout((6 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_back_green));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        AppCompatTextView tvPaymentAmountAndStatus = dialog.findViewById(R.id.tvPaymentAmountAndStatus);
        AppCompatCheckBox cbOrderAmount = dialog.findViewById(R.id.cbOrderAmount);
        AppCompatButton btSubmit = dialog.findViewById(R.id.btSubmit);
        tvPaymentAmountAndStatus.setText(getResources().getString(R.string.un_paid) + "\n$" + orderHistoryModel.response.orders_Info.get(0).Grand_Total);

        btSubmit.setOnClickListener(v -> {

            if (!isPaid) {
                Toast.makeText(getActivity(), getResources().getString(R.string.customer_paid_amount), Toast.LENGTH_SHORT).show();
                return;
            }
            String orderId = orderHistoryModel.response.orders_Info.get(0).Id;
            orderUpdate(orderId, "Order Delivered");
            dialog.dismiss();
        });

        cbOrderAmount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isPaid = true;
            } else {
                isPaid = false;
            }
        });

        dialog.show();

    }

    @OnClick(R.id.llChatWithCustomer)
    void OnClickllChatWithCustomer() {

        if (orderHistoryModel == null) {
            return;
        }

        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new ChatFragment(orderHistoryModel.response.orders_Info.get(0)), null);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!CommonUtils.isNetworkAvailable(getContext())) {
            showSnakBar(nsMainOrderDetail, v -> getOrderDetails());
            return;
        }

        getOrderDetails();
    }

    private void getOrderDetails() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", getArguments().getString("Id"));
            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.GET_ORDER, getRetrofitInterface().getOrderDetails(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.llCallCustomer, R.id.ivCallCustomerForLocation})
    void onClickllCallCustomer() {
        try {
            if (CommonUtils.checkCallPermission(getActivity())) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + orderHistoryModel.response.orders_Info.get(0).Customer_Id.Mobile));
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {

                        try {
                            if (CommonUtils.checkCallPermission(getActivity())) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + orderHistoryModel.response.orders_Info.get(0).Customer_Id.Mobile));
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    try {
                        if (CommonUtils.checkCallPermission(getActivity())) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + orderHistoryModel.response.orders_Info.get(0).Customer_Id.Mobile));
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @OnClick(R.id.btOrderPickedUp)
    void onClickbtOrderPickedUp() {
        if (orderHistoryModel == null) {
            return;
        }
        String orderId = orderHistoryModel.response.orders_Info.get(0).Id;
        orderUpdate(orderId, "Order Picked-up");
    }

    @OnClick(R.id.btOrderDelivered)
    void onOrderDelivered() {

        if (orderHistoryModel == null) {
            return;
        }
        if (orderHistoryModel.response.orders_Info.get(0).Payment_Status.equalsIgnoreCase("UnPaid")) {
            showPaymentDialog();
        } else {
            String orderId = orderHistoryModel.response.orders_Info.get(0).Id;
            orderUpdate(orderId, "Order Delivered");
        }
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());

        switch (url) {
            case Apis.GET_ORDER:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        orderHistoryModel = new Gson().fromJson(responce, OrderHistoryModel.class);
                        setOrderDetails();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.ORDER_UPDATE:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {

                        // OrderUpdateModel orderUpdateModel = new Gson().fromJson(responce, OrderUpdateModel.class);

                        if (strOrderStatus.equalsIgnoreCase("Order Delivered")) {
                            try {

                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("Customer_Id", orderHistoryModel.response.orders_Info.get(0).Customer_Id.User_Id);
                                jsonObject1.put("Delivery_Boy_Id", new SharedPrefModule(getActivity()).getUserId());
                                showProgressDialog(getActivity());
                                makeHttpCall(this, Apis.DELETE_CHAT, getRetrofitInterface().deleteChat(jsonObject1.toString()));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        getOrderDetails();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.DELETE_CHAT:

                try {
                    getActivity().onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @OnClick(R.id.ivTrackRestaurant)
    void onClickivTrackRestaurant() {

        if (orderHistoryModel == null) {
            return;
        }

        try {
            openMap(orderHistoryModel.response.orders_Info.get(0).Rest_Id.Latitude, orderHistoryModel.response.orders_Info.get(0).Rest_Id.Longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.ivTrackDeliveryLocation)
    void onClickivTrackDeliveryLocation() {

        if (orderHistoryModel == null) {
            return;
        }

        try {
            openMap(orderHistoryModel.response.orders_Info.get(0).Ordered_Items.Delivery_Address.Delivery_Lat, orderHistoryModel.response.orders_Info.get(0).Ordered_Items.Delivery_Address.Delivery_Long);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMap(String lat, String longi) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + longi);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOrderDetails() {

        if (orderHistoryModel == null) {
            return;
        }

        OrderHistoryModel.responseData.orders_InfoData orders_infoData = orderHistoryModel.response.orders_Info.get(0);
        try {
            if (!TextUtils.isEmpty(orders_infoData.Rest_Id.Profile_Image)) {
                Glide.with(getActivity()).load(Apis.API_URL_FILE + orders_infoData.Rest_Id.Profile_Image).into(ivRestImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvOrderNumber.setText(getResources().getString(R.string.order_number) + orders_infoData.Order_Number);
        tvOrderAmount.setText(getResources().getString(R.string.order_amount) + orders_infoData.Grand_Total);
        int items = 0;
        List<OrderHistoryModel.responseData.orders_InfoData.Ordered_ItemsData.ItemsData> data = orders_infoData.Ordered_Items.Items;
        for (int i = 0; i < data.size(); i++) {
            int temp = Integer.parseInt(data.get(i).Quantity);
            items = items + temp;
        }

        tvNoOfItems.setText(items + getResources().getString(R.string.items));
        tvPaymentType.setText(getResources().getString(R.string.pay_) + (orders_infoData.Payment_Type.equalsIgnoreCase("Cash") ? "Efectivo" : orders_infoData.Payment_Type));
        tvOrderDate.setText(orders_infoData.createdDtm);
        tvRestName.setText(orders_infoData.Rest_Id.Restaurant_Name);
        tvRestAddress.setText(orders_infoData.Rest_Id.Address);
        tvRestPhone.setText(orders_infoData.Rest_Id.Mobile);

        tvCustomerName.setText(orders_infoData.Customer_Id.Full_Name);
        tvDeliveryAddress.setText(orders_infoData.Ordered_Items.Delivery_Address.Address);
        tvCustomerPhone.setText(orders_infoData.Customer_Id.Mobile);

        if (!TextUtils.isEmpty(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Lat) && !TextUtils.isEmpty(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Long)) {
            ivTrackDeliveryLocation.setVisibility(View.VISIBLE);
            ivCallCustomerForLocation.setVisibility(View.GONE);
        } else {
            ivTrackDeliveryLocation.setVisibility(View.GONE);
            ivCallCustomerForLocation.setVisibility(View.VISIBLE);
        }

        try {

            if (llOrderedItems.getChildCount() > 0) {
                llOrderedItems.removeAllViews();
            }

            int itemTotal = 0;
            for (int i = 0; i < orders_infoData.Ordered_Items.Items.size(); i++) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_item_view, null, false);
                AppCompatTextView tvOrderItems = view.findViewById(R.id.tvOrderItems);
                AppCompatTextView tvItemPrice = view.findViewById(R.id.tvItemPrice);
                AppCompatTextView tvFlavor = view.findViewById(R.id.tvFlavor);
                LinearLayoutCompat llFlavor = view.findViewById(R.id.llFlavor);
                tvOrderItems.append(CommonUtils.getColoredString(getActivity(), "" + (i + 1) + ": ", getResources().getColor(R.color.foodie_color)));
                tvOrderItems.append(CommonUtils.getColoredString(getActivity(), orders_infoData.Ordered_Items.Items.get(i).Item_Name, ContextCompat.getColor(getActivity(), R.color.light_black)));
                tvOrderItems.append(CommonUtils.getColoredString(getActivity(), "  x" + orders_infoData.Ordered_Items.Items.get(i).Quantity, ContextCompat.getColor(getActivity(), R.color.colorAccent)));
                int itemsNo = Integer.parseInt(orders_infoData.Ordered_Items.Items.get(i).Quantity);
                int price = Integer.parseInt(orders_infoData.Ordered_Items.Items.get(i).Item_Price);

                int finalPrice = itemsNo * price;
                itemTotal = itemTotal + finalPrice;
                tvItemPrice.setText("$" + String.valueOf(finalPrice));

                String ItemFlavor = orders_infoData.Ordered_Items.Items.get(i).Item_Flavor_Type;
                llFlavor.setVisibility(!TextUtils.isEmpty(ItemFlavor) ? View.VISIBLE : View.GONE);
                if (!TextUtils.isEmpty(ItemFlavor)) {
                    tvFlavor.setText("sabor: " + ItemFlavor);
                }

                llOrderedItems.addView(view);
            }
            tvItemsTotal.setText("$" + String.valueOf(itemTotal));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvCustomerNote.setVisibility(!TextUtils.isEmpty(orders_infoData.Custom_Note) ? View.VISIBLE : View.GONE);
        tvCustomerNote.setText(!TextUtils.isEmpty(orders_infoData.Custom_Note) ? getResources().getString(R.string.customer_note) + " " + orders_infoData.Custom_Note : "");

        tvDeliveryFees.setText("$" + orders_infoData.Ordered_Items.Delivery_Charges);
        tvServiceTax.setText("$" + orders_infoData.Ordered_Items.Tax);
        tvDiscount.setText(!TextUtils.isEmpty(orders_infoData.Ordered_Items.Discount_Amount) ? "$" + orders_infoData.Ordered_Items.Discount_Amount : "$0");
        tvGrandTotal.setText("TOTAL  $" + orders_infoData.Grand_Total);

        if (orders_infoData.Order_Status.equalsIgnoreCase("Order Confirmed") || orders_infoData.Order_Status.equalsIgnoreCase("Order Processed") || orders_infoData.Order_Status.equalsIgnoreCase("Order Ready")) {
            btOrderPickedUp.setVisibility(View.VISIBLE);
            btOrderDelivered.setVisibility(View.GONE);
        } else if (orders_infoData.Order_Status.equalsIgnoreCase("Order Picked-up")) {
            btOrderPickedUp.setVisibility(View.GONE);
            btOrderDelivered.setVisibility(View.VISIBLE);
        } else {
            btOrderPickedUp.setVisibility(View.GONE);
            btOrderDelivered.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(getActivity());
    }

    private void orderUpdate(String orderId, String statusType) {
        try {
            strOrderStatus = statusType;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", orderId);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("Order_Status", statusType.equalsIgnoreCase("Order Delivered") ? "Order Delivered" : "Order Picked-up");

            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.ORDER_UPDATE, getRetrofitInterface().updateOrderStatus(jsonObject.toString(), jsonObject1.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
