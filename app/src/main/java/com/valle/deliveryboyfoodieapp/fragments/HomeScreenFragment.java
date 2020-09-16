package com.valle.deliveryboyfoodieapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.activity.HomeTabActivity;
import com.valle.deliveryboyfoodieapp.base.BaseFragment;
import com.valle.deliveryboyfoodieapp.models.LoginModel;
import com.valle.deliveryboyfoodieapp.models.OrderHistoryModel;
import com.valle.deliveryboyfoodieapp.models.UpdateUserDataModel;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;
import com.valle.deliveryboyfoodieapp.services.ForeGroundService;
import com.valle.deliveryboyfoodieapp.utils.CommonUtils;
import com.valle.deliveryboyfoodieapp.utils.RoundedImageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeScreenFragment extends BaseFragment implements NetworkResponceListener, OnMapReadyCallback {

    @BindView(R.id.rvOrderList)
    RecyclerView rvOrderList;

    @BindView(R.id.ivDeliveryProfile)
    RoundedImageView ivDeliveryProfile;

    @BindView(R.id.tvName)
    AppCompatTextView tvName;

    @BindView(R.id.tvMobile)
    AppCompatTextView tvMobile;

    @BindView(R.id.tvDate)
    AppCompatTextView tvDate;

    @BindView(R.id.tvOrderNo)
    AppCompatTextView tvOrderNo;

    @BindView(R.id.tvOrderDate)
    AppCompatTextView tvOrderDate;

    @BindView(R.id.tvOrderAmount)
    AppCompatTextView tvOrderAmount;

    @BindView(R.id.tvRestaurantName)
    AppCompatTextView tvRestaurantName;

    @BindView(R.id.tvDeliveryLocation)
    AppCompatTextView tvDeliveryLocation;

    @BindView(R.id.notification_switch)
    Switch userPresenceSwitch;

    @BindView(R.id.llOrderedItems)
    LinearLayoutCompat llOrderedItems;

    @BindView(R.id.cvSearching)
    CardView cvSearching;

    @BindView(R.id.cvAssignedOrderData)
    CardView cvAssignedOrderData;

    @BindView(R.id.llAcceptOrderLayout)
    LinearLayoutCompat llAcceptOrderLayout;

    @BindView(R.id.tvViewOrderDetails)
    AppCompatButton tvViewOrderDetails;

    @BindView(R.id.llMainHomeScreen)
    LinearLayoutCompat llMainHomeScreen;

    @BindView(R.id.content)
    RippleBackground rippleBackground;

    private boolean getOrderResponse = true;


    private final String ON = "on";
    private final String OFF = "off";
    private String presense = "";
    private static final String TAG = "HomeScreenFragment";
    private boolean bPresenceOnOFF = false;

    public static final int PATTERN_DASH_LENGTH_PX = 20;
    public static final int PATTERN_GAP_LENGTH_PX = 20;
    public static final PatternItem DOT = new Dot();
    public static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    private final String ORDER_ASSIGNED = "ORDER_ASSIGNED";
    private final String ORDER_ACCEPTED = "ORDER_ACCEPTED";
    private final String ORDER_REJECTED = "ORDER_REJECTED";

    private String orderStatus = "";

    private Timer timer;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private OrderHistoryModel orderAcceptedModel;
    private OrderHistoryModel orderAssignedModel;
    private Timer timerForPresence;
    private boolean checkPresenceThread = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        bindView(this, view);
        rippleBackground.startRippleAnimation();

       /* ClientOrderListAdapter clientOrderListAdapter = new ClientOrderListAdapter(getActivity());
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvOrderList.setLayoutManager(verticalLayoutManager);
        rvOrderList.setAdapter(clientOrderListAdapter);*/

        userPresenceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (timerForPresence != null) {
                    timerForPresence.cancel();
                }
                checkPresenceThread = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject.put("User_Id", new SharedPrefModule(getActivity()).getUserId());

                if (isChecked) {
                    presense = ON;
                    jsonObject1.put("Set_Your_Presence", "ON");
                    makeHttpCall(HomeScreenFragment.this, Apis.USER_PRESENCE, getRetrofitInterface().updateUserPresence(jsonObject.toString(), jsonObject1.toString()));
                } else {
                    presense = OFF;
                    jsonObject1.put("Set_Your_Presence", "OFF");
                    makeHttpCall(HomeScreenFragment.this, Apis.USER_PRESENCE, getRetrofitInterface().updateUserPresence(jsonObject.toString(), jsonObject1.toString()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    private void startPresenceTimer() {
        try {
            timerForPresence = new Timer();
            timerForPresence.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            checkPresenceThread = true;
                            if (CommonUtils.isNetworkAvailable(getActivity())) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("User_Id", new SharedPrefModule(getActivity()).getUserId());

                                    makeHttpCall(HomeScreenFragment.this, Apis.GET_USER_BY_FILTER, getRetrofitInterface().getPresenceStatus(jsonObject.toString()));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        });
                }
            }, 0, 20000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.ivDeliveryProfile)
    void OnClickivDeliveryProfile() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new UpdateProfileFragment(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setData() {

        String userLoginResponseData = new SharedPrefModule(getActivity()).getUserLoginResponseData();
        if (TextUtils.isEmpty(userLoginResponseData)) {
            return;
        }

        LoginModel.responseData.UserInfoData userInfoData = new Gson().fromJson(userLoginResponseData, LoginModel.responseData.UserInfoData.class);

        tvName.setText(userInfoData.Full_Name);
        tvMobile.setText(userInfoData.Mobile);
        tvDate.setText(CommonUtils.getCurrentDate());

        try {
            Glide.with(getActivity()).load(Apis.API_URL_FILE +
                    userInfoData.Profile_Image).placeholder(getResources().getDrawable(R.drawable.upload_image)).into(ivDeliveryProfile);
            if (!TextUtils.isEmpty(userInfoData.Set_Your_Presence)) {
                Log.d(TAG, "setData: " + userInfoData.Set_Your_Presence);
                if (userInfoData.Set_Your_Presence.equalsIgnoreCase("ON")) {
                    userPresenceSwitch.setChecked(true);
                } else {
                    userPresenceSwitch.setChecked(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());
        switch (url) {
            case Apis.USER_PRESENCE:

                try {
                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        UpdateUserDataModel updateUserDataModel = new Gson().fromJson(responce, UpdateUserDataModel.class);
                        new SharedPrefModule(getActivity()).setUserLoginResponse(new Gson().toJson(updateUserDataModel.response.orders_Info));
                        bPresenceOnOFF = updateUserDataModel.response.orders_Info.Set_Your_Presence.equalsIgnoreCase("ON");
                        if (bPresenceOnOFF) {
                            new SharedPrefModule(getActivity()).setLocationCheck("ON");
                        } else {
                            new SharedPrefModule(getActivity()).setLocationCheck("OFF");
                        }
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getJSONObject("response").getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    startPresenceTimer();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.GET_ONLY_NEW:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        orderAssignedModel = new Gson().fromJson(responce, OrderHistoryModel.class);
                        if (orderAssignedModel.response.orders_Info.size() > 0) {
                            timer.cancel();
                        }
                        setAssgnedViewData(orderAssignedModel.response.orders_Info.get(0), ORDER_ASSIGNED);
                        //  CommonUtils.sendNotification(getActivity(), getResources().getString(R.string.new_order_received), getResources().getString(R.string.your_order_number_is) + orderAssignedModel.response.orders_Info.get(0).Order_Number);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                getOrderResponse = true;
                break;

            case Apis.GET_ACCEPTED_ORDER:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        orderAcceptedModel = new Gson().fromJson(responce, OrderHistoryModel.class);
                        setAssgnedViewData(orderAcceptedModel.response.orders_Info.get(0), ORDER_ACCEPTED);
                    } else {
                        getAssignedOrder();
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
                        if (orderStatus.equalsIgnoreCase(ORDER_ACCEPTED)) {
                            //setAssgnedViewData(orderUpdateModel.response.orders_Info, ORDER_ACCEPTED);
                            showProgressDialog(getActivity());
                            getAccedptedOrder();
                        } else {
                            cvAssignedOrderData.setVisibility(View.GONE);
                            cvSearching.setVisibility(View.VISIBLE);
                            getOrderResponse = true;
                            getAssignedOrder();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.GET_USER_BY_FILTER:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {

                        bPresenceOnOFF = jsonObject.getJSONObject("response").getJSONArray("response").getJSONObject(0).getString("Set_Your_Presence").equalsIgnoreCase("ON");
                        if (bPresenceOnOFF) {
                            new SharedPrefModule(getActivity()).setLocationCheck("ON");
                        } else {
                            new SharedPrefModule(getActivity()).setLocationCheck("OFF");
                        }
                        if (checkPresenceThread) {
                            if (bPresenceOnOFF) {
                                userPresenceSwitch.setChecked(true);
                            } else {
                                userPresenceSwitch.setChecked(false);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void setAssgnedViewData(OrderHistoryModel.responseData.orders_InfoData dataModel, String orderType) {
        OrderHistoryModel.responseData.orders_InfoData orders_infoData = dataModel;

        if (orders_infoData == null) {
            return;
        }

        tvViewOrderDetails.setVisibility(orderType.equalsIgnoreCase(ORDER_ACCEPTED) ? View.VISIBLE : View.GONE);
        llAcceptOrderLayout.setVisibility(!orderType.equalsIgnoreCase(ORDER_ACCEPTED) ? View.VISIBLE : View.GONE);

        cvSearching.setVisibility(View.GONE);
        cvAssignedOrderData.setVisibility(View.VISIBLE);

        drawMap(orders_infoData);

        tvOrderNo.setText(getResources().getString(R.string.order_number) + orders_infoData.Order_Number);
        tvOrderAmount.setText(getResources().getString(R.string.order_amount) + orders_infoData.Grand_Total);
        tvOrderDate.setText(orders_infoData.createdDtm);

        try {
            if (llOrderedItems.getChildCount() > 0) {
                llOrderedItems.removeAllViews();
            }
            for (int i = 0; i < orders_infoData.Ordered_Items.Items.size(); i++) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_item_view, null, false);
                AppCompatTextView tvOrderItems = view.findViewById(R.id.tvOrderItems);
                AppCompatTextView tvFlavor = view.findViewById(R.id.tvFlavor);
                LinearLayoutCompat llFlavor=view.findViewById(R.id.llFlavor);
                tvOrderItems.append(CommonUtils.getColoredString(getActivity(), (i + 1) + ": ", ContextCompat.getColor(getActivity(), R.color.foodie_color)));
                tvOrderItems.append(CommonUtils.getColoredString(getActivity(), orders_infoData.Ordered_Items.Items.get(i).Item_Name, ContextCompat.getColor(getActivity(), R.color.light_black)));
                tvOrderItems.append(CommonUtils.getColoredString(getActivity(), "  x" + orders_infoData.Ordered_Items.Items.get(i).Quantity, ContextCompat.getColor(getActivity(), R.color.colorAccent)));
                String ItemFlavor = orders_infoData.Ordered_Items.Items.get(i).Item_Flavor_Type;
                llFlavor.setVisibility(!TextUtils.isEmpty(ItemFlavor) ? View.VISIBLE : View.GONE);
                if (!TextUtils.isEmpty(ItemFlavor)) {
                    tvFlavor.setText("sabor: " + ItemFlavor);
                }
                llOrderedItems.addView(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            tvRestaurantName.setText(orders_infoData.Rest_Id.Restaurant_Name + "(" + orders_infoData.Rest_Id.Address + ")");
            tvDeliveryLocation.setText(orders_infoData.Ordered_Items.Delivery_Address.Address);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(getActivity());
    }

    @OnClick(R.id.tvViewOrderDetails)
    void onClicktvViewOrderDetails() {
        Bundle bundle = new Bundle();
        bundle.putString("Id", orderAcceptedModel.response.orders_Info.get(0).Id);
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new CustomerOrderDetailFragment(), bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderResponse = true;
        setData();
        if (!CommonUtils.isNetworkAvailable(getActivity())) {
            showSnakBar(llMainHomeScreen, v -> getAccedptedOrder());
            return;
        }

        getAccedptedOrder();
        startPresenceTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            timer.cancel();
            timerForPresence.cancel();
            checkPresenceThread = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAccedptedOrder() {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Order_Status !=", "Order Delivered");
            jsonObject.put("Delivery_Boy_Id", new SharedPrefModule(getActivity()).getUserId());
            jsonObject.put("DeliveryBoy_Status", "Accepted");

            makeHttpCall(HomeScreenFragment.this, Apis.GET_ACCEPTED_ORDER, getRetrofitInterface().getAcceptedOrders(jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tvAcceptOrder)
    void onClicktvAcceptOrder() {
        String orderId = orderAssignedModel.response.orders_Info.get(0).Id;
        orderStatus = ORDER_ACCEPTED;
        acceptOrder(orderId);
    }

    @OnClick(R.id.tvRejectOrder)
    void onClicktvRejectOrder() {
        String orderId = orderAssignedModel.response.orders_Info.get(0).Id;
        orderStatus = ORDER_REJECTED;
        rejectOrder(orderId);
    }

    private void acceptOrder(String orderId) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", orderId);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("Delivery_Boy_Id", new SharedPrefModule(getActivity()).getUserId());
            jsonObject1.put("DeliveryBoy_Status", "Accepted");

            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.ORDER_UPDATE, getRetrofitInterface().updateOrderStatus(jsonObject.toString(), jsonObject1.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rejectOrder(String orderId) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", orderId);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("Delivery_Boy_Id", JSONObject.NULL);
            jsonObject1.put("DeliveryBoy_Rejected_Ids", new SharedPrefModule(getActivity()).getUserId());
            jsonObject1.put("DeliveryBoy_Status", JSONObject.NULL);

            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.ORDER_UPDATE, getRetrofitInterface().updateOrderStatus(jsonObject.toString(), jsonObject1.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAssignedOrder() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    try {
                        if (bPresenceOnOFF) {
                            if (getOrderResponse) {
                                getOrderResponse = false;
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("Delivery_Boy_Id", new SharedPrefModule(getActivity()).getUserId());
                                jsonObject.put("DeliveryBoy_Status", JSONObject.NULL);
                                makeHttpCall(HomeScreenFragment.this, Apis.GET_ONLY_NEW, getRetrofitInterface().getAssignedOrder(jsonObject.toString()));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }
        }, 0, 10000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void drawMap(OrderHistoryModel.responseData.orders_InfoData orders_infoData) {
        if (orders_infoData != null) {
            try {

                if (mMap == null) {
                    return;
                }

                mMap.clear();

                if (!TextUtils.isEmpty(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Lat) && !TextUtils.isEmpty(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Long)) {
                    mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(Double.valueOf(orders_infoData.Rest_Id.Latitude), Double.valueOf(orders_infoData.Rest_Id.Longitude)), new LatLng(Double.valueOf(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Lat), Double.valueOf(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Long)))
                            .width(5)
                            .pattern(PATTERN_POLYGON_ALPHA)
                            .color(getActivity().getResources().getColor(R.color.black)));
                }

                MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.restau_icon));
                markerOptions.position(new LatLng(Double.valueOf(orders_infoData.Rest_Id.Latitude), Double.valueOf(orders_infoData.Rest_Id.Longitude)));
                markerOptions.title(orders_infoData.Rest_Id.Restaurant_Name);
                mMap.addMarker(markerOptions).showInfoWindow();

                if (!TextUtils.isEmpty(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Lat) && !TextUtils.isEmpty(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Long)) {
                    MarkerOptions markerOptions2 = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery_loc));
                    markerOptions2.position(new LatLng(Double.valueOf(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Lat), Double.valueOf(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Long)));
                    markerOptions2.title(getResources().getString(R.string.delivery_location));
                    mMap.addMarker(markerOptions2);
                }
                // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(orders_infoData.Rest_Id.Latitude), Double.valueOf(orders_infoData.Rest_Id.Longitude)), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.valueOf(orders_infoData.Rest_Id.Latitude), Double.valueOf(orders_infoData.Rest_Id.Longitude)))      // Sets the center of the map to location user
                        .zoom(14)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (!TextUtils.isEmpty(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Lat) && !TextUtils.isEmpty(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Long)) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(Double.valueOf(orders_infoData.Rest_Id.Latitude), Double.valueOf(orders_infoData.Rest_Id.Longitude)));
                    builder.include(new LatLng(Double.valueOf(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Lat), Double.valueOf(orders_infoData.Ordered_Items.Delivery_Address.Delivery_Long)));
                    LatLngBounds bounds = builder.build();
                    new Handler().postDelayed(() -> {
                        try {
                            if (mMap != null) {
                                final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                                mMap.animateCamera(cu);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (getActivity() !=null) {
                            getActivity().fileList();
                        }
                    }, 5000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}






/*

 try {
         if (!CommonUtils.isMyServiceRunning(getActivity(), ForeGroundService.class)) {
        Intent i = new Intent(getActivity(), ForeGroundService.class);
        getActivity().startService(i);
        }
        } catch (Exception e) {
        e.printStackTrace();
        }
        try {
        if (CommonUtils.isMyServiceRunning(getActivity(), ForeGroundService.class)) {
        Intent i = new Intent(getActivity(), ForeGroundService.class);
        getActivity().stopService(i);
        }
        } catch (Exception e) {
        e.printStackTrace();
        }*/
