package com.valle.deliveryboyfoodieapp.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.valle.deliveryboyfoodieapp.models.OrderHistoryModel;
import com.valle.deliveryboyfoodieapp.network.APIClient;
import com.valle.deliveryboyfoodieapp.network.APIInterface;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkManager;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;
import com.valle.deliveryboyfoodieapp.utils.CommonUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class OrderHandlerService extends Service implements NetworkResponceListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "OrderHandlerService";

    private Timer timer;

    @Override
    public void onSuccess(String url, String responce) {
        switch (url) {
            case Apis.CHECK_ORDER_IN_BACKGROUND:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OrderHistoryModel orderAssignedModel = new Gson().fromJson(responce, OrderHistoryModel.class);
                        if (orderAssignedModel.response.orders_Info.size() > 0) {
                            String order_number = orderAssignedModel.response.orders_Info.get(0).Order_Number;
                            String notificationId = new SharedPrefModule(this).getNotificationId();
                            if (TextUtils.isEmpty(notificationId)) {
                                notificationId = "";
                            }
                            if (!order_number.equalsIgnoreCase(notificationId)) {
                                new SharedPrefModule(this).setNotificationId(order_number);
                                CommonUtils.sendNotification(getApplicationContext(), "has recibido un nuevo pedido", "tu numero de orden es " + orderAssignedModel.response.orders_Info.get(0).Order_Number);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("StaticFieldLeak")
    public class checkOrderStatus extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.e("OrderHandlerService", "OrderHandlerService is working");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Delivery_Boy_Id", new SharedPrefModule(getApplicationContext()).getUserId());
                jsonObject.put("DeliveryBoy_Status", JSONObject.NULL);
                makeHttpCall(OrderHandlerService.this, Apis.CHECK_ORDER_IN_BACKGROUND, getRetrofitInterface().getAssignedOrder(jsonObject.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "OrderHandlerService Create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        boolean isOn = new SharedPrefModule(getApplicationContext()).getLocationCheck().equalsIgnoreCase("ON");
                        if (isOn) {
                            new checkOrderStatus().execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10000);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "OrderHandlerService onDestroy");
        super.onDestroy();
        try {
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void makeHttpCall(NetworkResponceListener context, String url, Object o) {
        new NetworkManager(context).getNetworkResponce(url, o);
    }

    public APIInterface getRetrofitInterface() {
        return APIClient.getClient().create(APIInterface.class);
    }

}