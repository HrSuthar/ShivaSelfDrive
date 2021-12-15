package com.carrental.ShivaSD.bottomNav.home.cars;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.carrental.ShivaSD.R;
import com.carrental.ShivaSD.bottomNav.home.HomeFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckoutFragment extends Fragment {

    DatabaseReference regRef = FirebaseDatabase.getInstance().getReference("HISTORY");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        View root = inflater.inflate(R.layout.z_activity_checkout, container, false);
        assert getArguments() != null;
        String[] carList = getArguments().getStringArray("BookedCar");
        String[] cDetails = getArguments().getStringArray("CustomerBook");

        ImageView carImage = root.findViewById(R.id.image_path);
        Glide.with(root).load(carList[5]).into(carImage);
        TextView carName = root.findViewById(R.id.car_name);
        carName.setText(carList[0]);

        TextView pickUpAddress = root.findViewById(R.id.car_src_location);
        pickUpAddress.setText(cDetails[0]);
        TextView dropAddress = root.findViewById(R.id.car_dest_location);
        dropAddress.setText(cDetails[9]);
        TextView rentalDate = root.findViewById(R.id.pick_up_date);
        rentalDate.setText(cDetails[1]);
        TextView rentalTime = root.findViewById(R.id.pick_up_time);
        rentalTime.setText(cDetails[2]);
        TextView rentalCost = root.findViewById(R.id.rental_price);
        rentalCost.setText(cDetails[7]);

    // Firebase Date Store
        regRef.child(cDetails[6]).child(carList[6]).child("CarName").setValue(carList[0]);
        regRef.child(cDetails[6]).child(carList[6]).child("DepartDate").setValue(cDetails[1]);
        regRef.child(cDetails[6]).child(carList[6]).child("ReturnDate").setValue(cDetails[8]);
        regRef.child(cDetails[6]).child(carList[6]).child("Source").setValue(cDetails[0]);
        regRef.child(cDetails[6]).child(carList[6]).child("Destination").setValue(cDetails[9]);
        regRef.child(cDetails[6]).child(carList[6]).child("CarImage").setValue(carList[5]);
        regRef.child(cDetails[6]).child(carList[6]).child("AmountPaid").setValue(cDetails[7]);
        regRef.child(cDetails[6]).child(carList[6]).child("UserName").setValue(cDetails[3]);


        Button backToMenuButton = root.findViewById(R.id.back_to_menu);
        backToMenuButton.setOnClickListener(v -> Navigation.findNavController(v).popBackStack(R.id.navigation_home,false));

        return root;
    }

}
