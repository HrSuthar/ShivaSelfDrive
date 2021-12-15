package com.carrental.ShivaSD.bottomNav.home.cars;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carrental.ShivaSD.R;
import com.carrental.ShivaSD.ui.adapter.FeatureAdapter;
import com.carrental.ShivaSD.ui.models.FeatureObject;

import java.util.ArrayList;
import java.util.List;


public class CarsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.z_activity_car, container, false);

        assert getArguments() != null;
        String[] carlist = getArguments().getStringArray("CarList");

        ImageView carImage = root.findViewById(R.id.selected_car);
        Glide.with(root).load(carlist[5]).into(carImage);

        RecyclerView featureRecyclerView = root.findViewById(R.id.car_features);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        featureRecyclerView.setLayoutManager(linearLayoutManager);
        featureRecyclerView.setHasFixedSize(true);

        FeatureAdapter mAdapter = new FeatureAdapter(getTestData());
        featureRecyclerView.setAdapter(mAdapter);

        RecyclerView priceRecyclerView = root.findViewById(R.id.car_price);
        LinearLayoutManager linear = new LinearLayoutManager(getContext());
        priceRecyclerView.setLayoutManager(linear);
        priceRecyclerView.setHasFixedSize(true);

        FeatureAdapter priceAdapter = new FeatureAdapter(getTestDataTwo());
        priceRecyclerView.setAdapter(priceAdapter);

        Button bookingButton = root.findViewById(R.id.continue_booking);

       Bundle bundle = new Bundle();
       bundle.putStringArray("BookingCar",carlist);
        bookingButton.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_navigation_car_to_navigation_booking,bundle));

        return root;
    }

    private List<FeatureObject> getTestData() {
        assert getArguments() != null;
        String[] selectedCar = getArguments().getStringArray("CarList");

        List<FeatureObject> testdata = new ArrayList<>();
        testdata.add(new FeatureObject("Mileage",selectedCar[1]));
        testdata.add(new FeatureObject("Fuel Type", selectedCar[2]));
        testdata.add(new FeatureObject("Number of Seats", selectedCar[3]));
        testdata.add(new FeatureObject("Fuel Economy", "Yes"));
        return testdata;
    }

    private List<FeatureObject> getTestDataTwo() {

        List<FeatureObject> testdata = new ArrayList<>();
        assert getArguments() != null;
        testdata.add(new FeatureObject("Daily Price",getArguments().getStringArray("CarList")[4]));
        testdata.add(new FeatureObject("Booking Amount", "₹500"));
        return testdata;
    }
}
