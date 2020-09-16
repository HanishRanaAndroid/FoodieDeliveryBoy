package com.valle.deliveryboyfoodieapp.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefModule {

    private SharedPreferences prefs;
    private Context context;

    public SharedPrefModule(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUserLoginResponse(String response) {
        prefs.edit().putString("UserLoginResponse", response).commit();
    }


    public String getUserId() {
        return prefs.getString("userID", "");
    }

    public void setUserId(String userID) {
        prefs.edit().putString("userID", userID).commit();
    }

    public String getUserRememberData() {
        return prefs.getString("setUserRemember", "");
    }

    public void setUserRemember(String response) {
        prefs.edit().putString("setUserRemember", response).commit();
    }

    public void setNotification(String notification) {
        prefs.edit().putString("notification", notification).commit();
    }

    public void setNotificationId(String notification) {
        prefs.edit().putString("notificationId", notification).commit();
    }

    public String getNotificationId() {
        return prefs.getString("notificationId", "");
    }

    public String getnotification() {
        return prefs.getString("notification", "");
    }


    public String getUserLoginResponseData() {
        return prefs.getString("UserLoginResponse", "");
    }

    public void setLocationCheck(String notification) {
        prefs.edit().putString("setLocationCheck", notification).commit();
    }

    public String getLocationCheck() {
        return prefs.getString("setLocationCheck", "");
    }


}