package com.valle.deliveryboyfoodieapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.models.OrderHistoryModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private List<OrderHistoryModel.responseData.orders_InfoData> orders_infoData;
    private List<OrderHistoryModel.responseData.orders_InfoData> getOrders_infoData;

    public OrderHistoryAdapter(Context context, List<OrderHistoryModel.responseData.orders_InfoData> orders_infoData) {
        this.context = context;
        this.orders_infoData = orders_infoData;
        getOrders_infoData = new ArrayList<>();
        getOrders_infoData.addAll(orders_infoData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_layout_order_history, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvOrderNo.setText(context.getResources().getString(R.string.order_number) + getOrders_infoData.get(position).Order_Number);
        holder.tvOrderAmount.setText(context.getResources().getString(R.string.order_amount) + getOrders_infoData.get(position).Grand_Total);
        holder.tvOrderDate.setText(getOrders_infoData.get(position).createdDtm);
        holder.tvDeliveryCharges.setText(context.getResources().getString(R.string.delivery_fees) + ": $" + getOrders_infoData.get(position).Ordered_Items.Delivery_Charges);
        int items = 0;
        List<OrderHistoryModel.responseData.orders_InfoData.Ordered_ItemsData.ItemsData> data = getOrders_infoData.get(position).Ordered_Items.Items;
        for (int i = 0; i < data.size(); i++) {
            int temp = Integer.parseInt(data.get(i).Quantity);
            items = items + temp;
        }
        holder.tvNoOfItems.setText(items + context.getResources().getString(R.string.items));
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(getOrders_infoData.get(position).Is_Rating));
            if (jsonObject.has("Rating")) {
                holder.ratingBar.setRating(Float.valueOf(getOrders_infoData.get(position).Is_Rating.Rating));
            } else {
                holder.ratingBar.setRating(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            holder.tvRestaurantName.setText(getOrders_infoData.get(position).Rest_Id.Restaurant_Name);
            holder.tvDeliveryLocation.setText(getOrders_infoData.get(position).Ordered_Items.Delivery_Address.Address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filterData(String data) {
        getOrders_infoData.clear();
        if (!TextUtils.isEmpty(data)) {

            for (int i = 0; i < orders_infoData.size(); i++) {
                if (orders_infoData.get(i).Order_Number.toLowerCase().contains(data.toLowerCase()) || orders_infoData.get(i).createdDtm.toLowerCase().contains(data.toLowerCase()) || orders_infoData.get(i).Grand_Total.toLowerCase().contains(data.toLowerCase())) {
                    getOrders_infoData.add(orders_infoData.get(i));
                }
            }
        } else {
            getOrders_infoData.addAll(orders_infoData);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return getOrders_infoData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvOrderNo, tvDeliveryCharges, tvOrderDate, tvOrderAmount, tvNoOfItems, tvRestaurantName, tvDeliveryLocation;
        private RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.tvOrderNo);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
            tvNoOfItems = itemView.findViewById(R.id.tvNoOfItems);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvDeliveryLocation = itemView.findViewById(R.id.tvDeliveryLocation);
            tvDeliveryCharges = itemView.findViewById(R.id.tvDeliveryCharges);
        }
    }

}
