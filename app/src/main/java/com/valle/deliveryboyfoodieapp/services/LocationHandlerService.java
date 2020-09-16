package com.valle.deliveryboyfoodieapp.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.valle.deliveryboyfoodieapp.network.APIClient;
import com.valle.deliveryboyfoodieapp.network.APIInterface;
import com.valle.deliveryboyfoodieapp.network.Apis;
import com.valle.deliveryboyfoodieapp.network.NetworkManager;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class LocationHandlerService extends Service implements NetworkResponceListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MyLocationService";
    private GoogleApiClient googleApiClient;

    private double latitude = 0.0, longitude = 0.0;
    private Timer timer, deviceTokenTimer;
    private String deviceToken;
    private int count = 0;

    @Override
    public void onSuccess(String url, String responce) {

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
    public class UpdateLocation extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String userId = new SharedPrefModule(getApplicationContext()).getUserId();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("User_Id", userId);

                JSONObject object = new JSONObject();
                object.put("Latitude", latitude);
                object.put("Longitude", longitude);
                Log.e("LocationHandlerService", "LocationHandlerService is working");
                makeHttpCall(LocationHandlerService.this, Apis.USER_PRESENCE, getRetrofitInterface().updateUserPresence(jsonObject.toString(), object.toString()));
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

    @SuppressLint("StaticFieldLeak")
    public class UpdateToken extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String userId = new SharedPrefModule(getApplicationContext()).getUserId();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("User_Id", userId);

                JSONObject object = new JSONObject();
                object.put("Device_Token", !TextUtils.isEmpty(deviceToken) ? deviceToken : JSONObject.NULL);

                makeHttpCall(LocationHandlerService.this, Apis.USER_PRESENCE, getRetrofitInterface().updateToken(jsonObject.toString(), object.toString()));
                try {
                    if (!TextUtils.isEmpty(deviceToken)) {
                        count++;
                    }
                    if (count == 2) {
                        deviceTokenTimer.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
        Log.e(TAG, "onCreate");

        //Instantiating the GoogleApiClient
        try {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                deviceToken = instanceIdResult.getToken();
                // Do whatever you want with your token now
                // i.e. store it on SharedPreferences or DB
                // or directly send it to server
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                        Task task = fusedLocationProviderClient.getLastLocation();
                        task.addOnSuccessListener(o -> {
                            latitude = ((Location) o).getLatitude();
                            longitude = ((Location) o).getLongitude();
                            boolean isOn = new SharedPrefModule(getApplicationContext()).getLocationCheck().equalsIgnoreCase("ON");
                            if (isOn) {
                                new UpdateLocation().execute();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10000);

            deviceTokenTimer = new Timer();
            deviceTokenTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        new UpdateToken().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 30000);

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
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        try {
            googleApiClient.disconnect();
            timer.cancel();
            deviceTokenTimer.cancel();
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