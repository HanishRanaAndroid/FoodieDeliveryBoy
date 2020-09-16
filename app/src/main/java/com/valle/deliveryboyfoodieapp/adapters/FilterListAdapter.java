package com.valle.deliveryboyfoodieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.deliveryboyfoodieapp.R;
import com.valle.deliveryboyfoodieapp.models.FilterHistoryBean;

import java.util.ArrayList;

import static com.valle.deliveryboyfoodieapp.fragments.OrderHistoryFragment.filterSelceted;


public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FilterHistoryBean> filterDataList;
    private FilterListener filterListener;

    public FilterListAdapter(Context context, ArrayList<FilterHistoryBean> filterDataList, FilterListener filterListener) {
        this.context = context;
        this.filterDataList = filterDataList;
        this.filterListener = filterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_layout_filter_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvFilterName.setText(filterDataList.get(position).getName());

        holder.tvFilterName.setOnClickListener(v -> {
            filterListener.OnFilterSelect(filterDataList.get(position));
            notifyDataSetChanged();
        });

        if (filterDataList.get(position).getName().equalsIgnoreCase(filterSelceted)) {
            holder.ivTickSelect.setVisibility(View.VISIBLE);
        } else {
            holder.ivTickSelect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filterDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView ivTickSelect;
        private AppCompatTextView tvFilterName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTickSelect = itemView.findViewById(R.id.ivTickSelect);
            tvFilterName = itemView.findViewById(R.id.tvFilterName);
        }
    }

    public interface FilterListener {
        void OnFilterSelect(FilterHistoryBean strFilter);
    }
}
