package com.carrental.ShivaSD.ui.cardview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.carrental.ShivaSD.R;

import java.util.ArrayList;


public class cardAdapter extends RecyclerView.Adapter<cardAdapter.eventViewHolder> {
    private String[][] mEventDesc;
    RequestManager glide;

    public cardAdapter(RequestManager glide, String[][] mEventDesc){
        this.glide = glide;
        this.mEventDesc = mEventDesc;
    }

    @NonNull
    @Override
    public eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            pref = parent.getContext().getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_card, parent,false);
        return new eventViewHolder(root);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull eventViewHolder holder, int position) {

        String eventDesc = mEventDesc[position][0];
        glide.load(mEventDesc[position][5])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

        holder.textView.setText(eventDesc);

//        if(pref.getString("UserType","-").equals("Customer"))
        Bundle bundle = new Bundle();
        bundle.putStringArray("CarList",mEventDesc[position]);
        holder.cardView.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_navigation_home_to_navigation_car,bundle)
        );
    }

    @Override
    public int getItemCount() {
        return mEventDesc.length;
    }

    public static class eventViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public CardView cardView;
        public ImageView imageView;

        public eventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.car_category_image);
            textView = itemView.findViewById(R.id.car_category_name);
            cardView = itemView.findViewById(R.id.card_View);
        }
    }

    public  void filterList(ArrayList<String[]> filteredList){
        mEventDesc = filteredList.toArray(new String[0][]);
        notifyDataSetChanged();
    }

}
