package com.carrental.ShivaSD.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.carrental.ShivaSD.R;

public class FeatureViewHolder extends RecyclerView.ViewHolder{

    public TextView title;
    public TextView detail;

    public FeatureViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.car_feature_title);
        detail = itemView.findViewById(R.id.car_price_title);

    }
}
