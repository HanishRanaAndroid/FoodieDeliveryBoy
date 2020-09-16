package com.valle.deliveryboyfoodieapp.base;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.network.APIClient;
import com.valle.deliveryboyfoodieapp.network.APIInterface;
import com.valle.deliveryboyfoodieapp.network.NetworkManager;
import com.valle.deliveryboyfoodieapp.network.NetworkResponceListener;
import com.valle.deliveryboyfoodieapp.utils.APIClientUpdateImage;
import com.valle.deliveryboyfoodieapp.utils.CommonUtils;
import com.google.android.material.snackbar.Snackbar;
import com.valle.deliveryboyfoodieapp.utils.NetworkManagerToUploadImageData;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseFragment extends Fragment {

    public ProgressDialog progressDialog;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void bindView(Fragment activity, View view) {
        unbinder = ButterKnife.bind(activity, view);
    }

    protected void unBindView() {
        unbinder.unbind();
    }

    public void showProgressDialog(Context activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void hideProgressDialog(Context context) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void makeHttpCall(NetworkResponceListener context, String url, Object o) {
        new NetworkManager(context).getNetworkResponce(url, o);
    }

    public APIInterface getRetrofitInterface() {
        return APIClient.getClient().create(APIInterface.class);
    }

    public void makeHttpCallToUploadImageData(NetworkResponceListener context, String url, Object o) {
        new NetworkManagerToUploadImageData(context).getNetworkResponce(url, o);
    }
    public APIInterface getRetrofitInterfaceToUploadImageData() {
        return APIClientUpdateImage.getClient().create(APIInterface.class);
    }

    public void showSnakBar(View viewLayout, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar
                .make(viewLayout, getResources().getString(R.string.internet_error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.restry), view -> {
                    if (!CommonUtils.isNetworkAvailable(getActivity())) {
                        showSnakBar(viewLayout, onClickListener);
                    } else {
                        onClickListener.onClick(view);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
