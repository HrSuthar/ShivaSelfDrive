package com.carrental.ShivaSD.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carrental.ShivaSD.R;
import com.carrental.ShivaSD.ui.models.FeatureObject;

import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureViewHolder>{


    private final List<FeatureObject> featureList;

    public FeatureAdapter(List<FeatureObject> featureList) {
        this.featureList = featureList;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_feature_layout, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeatureViewHolder holder, int position) {
        FeatureObject featureObject = featureList.get(position);

        holder.title.setText(featureObject.getFeatureTitle());
        holder.detail.setText(featureObject.getFeatureValue());

    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }
}