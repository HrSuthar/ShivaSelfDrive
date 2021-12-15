package com.carrental.ShivaSD.bottomNav.history.searchfrag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.carrental.ShivaSD.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;


public class SearchFragment extends BottomSheetDialogFragment {

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialog_Rounded;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_item_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemAdapter(Glide.with(view), (ArrayList<String[]>) getArguments().getSerializable("history")));
    }


    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dropDate, pickupDate, carLoc, carName, amount, carDestLoc, carRCno;
        ImageView imageView;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_search_individual_item, parent, false));

            dropDate = itemView.findViewById(R.id.h_drop_date);
            pickupDate = itemView.findViewById(R.id.h_pick_up_date);
            carLoc = itemView.findViewById(R.id.h_car_src_location);
            carDestLoc = itemView.findViewById(R.id.h_car_dest_location);
            carName = itemView.findViewById(R.id.h_car_name);
            carRCno = itemView.findViewById(R.id.h_reg_no);
            amount = itemView.findViewById(R.id.h_rental_price);
            imageView = itemView.findViewById(R.id.h_image_path);
        }
    }


    public static class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<String[]> histDetails;
        RequestManager glide;

        public ItemAdapter(RequestManager glide, ArrayList<String[]> histDetails) {
            this.glide = glide;
            this.histDetails = histDetails;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            glide.load(histDetails.get(position)[0])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
            holder.carName.setText(histDetails.get(position)[1]);
            holder.carLoc.setText(histDetails.get(position)[2]);
            holder.carDestLoc.setText(histDetails.get(position)[3]);
            holder.dropDate.setText(histDetails.get(position)[4]);
            holder.pickupDate.setText(histDetails.get(position)[5]);
            holder.amount.setText(histDetails.get(position)[6]);
            holder.carRCno.setText(histDetails.get(position)[7]);

        }

        @Override
        public int getItemCount() {
            return histDetails.size();
        }
    }

}