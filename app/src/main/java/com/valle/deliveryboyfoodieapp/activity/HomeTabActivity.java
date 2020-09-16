package com.valle.deliveryboyfoodieapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.base.BaseActivity;
import com.valle.deliveryboyfoodieapp.fragments.HomeScreenFragment;
import com.valle.deliveryboyfoodieapp.fragments.MyProfileFragment;
import com.valle.deliveryboyfoodieapp.fragments.OrderHistoryFragment;
import com.valle.deliveryboyfoodieapp.fragments.SettingFragment;
import com.valle.deliveryboyfoodieapp.fragments.UpdateProfileFragment;
import com.valle.deliveryboyfoodieapp.prefs.SharedPrefModule;
import com.valle.deliveryboyfoodieapp.services.ForeGroundService;
import com.valle.deliveryboyfoodieapp.services.LocationHandlerService;
import com.valle.deliveryboyfoodieapp.services.OrderHandlerService;
import com.valle.deliveryboyfoodieapp.utils.CommonUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.valle.deliveryboyfoodieapp.utils.CommonUtils.MY_PERMISSIONS_REQUEST_CALL;
import static com.valle.deliveryboyfoodieapp.utils.CommonUtils.MY_PERMISSIONS_REQUEST_LOCATION;

public class HomeTabActivity extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    public FragmentManager fragmentManager;
    TextView tvSignOut;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab);

        fragmentManager = getSupportFragmentManager();
        tvSignOut = findViewById(R.id.tvSignOut);
        bottomNavigationView = findViewById(R.id.navigation);

        tvSignOut.setOnClickListener(v -> new AlertDialog.Builder(HomeTabActivity.this).setTitle(getResources().getString(R.string.logout_msg))
                .setMessage(getResources().getString(R.string.are_you_sure)).setPositiveButton(
                        getResources().getString(R.string.yes), (dialog, which) -> {
                            new SharedPrefModule(HomeTabActivity.this).setUserId("");
                            new SharedPrefModule(HomeTabActivity.this).setUserLoginResponse("");
                            dialog.dismiss();
                            startActivity(new Intent(HomeTabActivity.this, LoginActivity.class));
                        }).setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss()).create().show());

        addFragment(new HomeScreenFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.action_item1:
                    selectedFragment = new HomeScreenFragment();
                    break;
                case R.id.action_item2:
                    selectedFragment = new OrderHistoryFragment();
                    break;
                case R.id.action_item3:
                    selectedFragment = new UpdateProfileFragment();
                    break;
                case R.id.action_item4:
                    selectedFragment = new SettingFragment();
                    break;
            }
            replaceFragment(selectedFragment, null);
            return true;
        });

        try {
            if (CommonUtils.checkLocationPermission(HomeTabActivity.this)) {
                startService(new Intent(this, LocationHandlerService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        try {
                            if (CommonUtils.checkLocationPermission(HomeTabActivity.this)) {
                                startService(new Intent(this, LocationHandlerService.class));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } else {

                    try {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeTabActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            if (CommonUtils.checkLocationPermission(HomeTabActivity.this)) {
                                startService(new Intent(this, LocationHandlerService.class));
                            }
                        } else {

                            new androidx.appcompat.app.AlertDialog.Builder(HomeTabActivity.this)
                                    .setTitle(getResources().getString(R.string.plz_allow_loc_per))
                                    .setMessage(getResources().getString(R.string.plz_allow_for_best_results))
                                    .setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> {
                                        //Prompt the user once explanation has been shown
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    })
                                    .create()
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            break;

            case MY_PERMISSIONS_REQUEST_CALL:

                (fragmentManager.findFragmentById(R.id.frame_layout)).onRequestPermissionsResult(requestCode,
                        permissions, grantResults);

                break;

        }
    }

    public void replaceFragmentWithBackStack(Fragment fragment, Bundle bundle) {
        String backStateName = fragment.getClass().getName();
        if (!fragmentManager.popBackStackImmediate(backStateName, 0)) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            fragment.setArguments(bundle);
            ft.replace(R.id.frame_layout, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void callExitAPP() {
        new AlertDialog.Builder(HomeTabActivity.this).setTitle("Exit!")
                .setMessage(getResources().getString(R.string.exit_from_app)).setPositiveButton(
                getResources().getString(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }).setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss()).create().show();
    }

    public void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment, Bundle bundle) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (!CommonUtils.isMyServiceRunning(HomeTabActivity.this, OrderHandlerService.class)) {
                startService(new Intent(this, OrderHandlerService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (CommonUtils.isMyServiceRunning(HomeTabActivity.this, OrderHandlerService.class)) {
                stopService(new Intent(this, OrderHandlerService.class));
            }

            if (!CommonUtils.isMyServiceRunning(HomeTabActivity.this, LocationHandlerService.class)) {
                startService(new Intent(this, LocationHandlerService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        getFragmentCallBack();
    }

    public void getFragmentCallBack() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.frame_layout);
        if ((fragment instanceof HomeScreenFragment || fragment instanceof OrderHistoryFragment || fragment instanceof MyProfileFragment
                || fragment instanceof SettingFragment)) {
            callExitAPP();
        } else {
            fragmentManager.popBackStackImmediate();
        }
    }

}
