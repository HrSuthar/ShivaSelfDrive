package com.carrental.ShivaSD.bottomNav.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.carrental.ShivaSD.R;
import com.carrental.ShivaSD.ui.cardview.cardAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    cardAdapter mAdapter;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("CAR");
    String[][] carModels;
    FloatingActionButton floatingActionButton, fabAdd, fabRemove;
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_ADMIN = "admin";

    Animation rotateOpen ;
    Animation rotateClose ;
    Animation fromBottom ;
    Animation toBottom ;
    Boolean fabClicked = false;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String admin = sharedPreferences.getString(KEY_ADMIN, "false");

        rotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getContext(),R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getContext(),R.anim.to_bottom_anim);
        floatingActionButton = root.findViewById(R.id.floating_action_button_car);
        fabAdd = root.findViewById(R.id.fab_add);
        fabRemove = root.findViewById(R.id.fab_remove);

        mSwipeRefreshLayout = root.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        // Fetching data from server
        mSwipeRefreshLayout.post(this::DataFetchFromFirebase);

        if(admin.equals("true")) {
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setOnClickListener(v -> {
                if(!fabClicked) {
                    fabAdd.setVisibility(View.VISIBLE);
                    fabRemove.setVisibility(View.VISIBLE);
                    fabAdd.startAnimation(fromBottom);
                    fabRemove.startAnimation(fromBottom);
                    floatingActionButton.startAnimation(rotateOpen);
                }else{
                    floatingActionButton.startAnimation(rotateClose);
                    fabAdd.startAnimation(toBottom);
                    fabRemove.startAnimation(toBottom);
                    fabAdd.setVisibility(View.GONE);
                    fabRemove.setVisibility(View.GONE);
                }
                fabClicked = !fabClicked;
            });

            fabAdd.setOnClickListener(v -> Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_home_to_navigation_car_add));
            fabRemove.setOnClickListener(v -> Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_home_to_carRemoveFragment));
        }

        return root;
    }

    @Override
    public void onRefresh() {
        DataFetchFromFirebase();
    }

    void DataFetchFromFirebase(){
        mSwipeRefreshLayout.setRefreshing(true);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long ccnt = dataSnapshot.getChildrenCount();
                long rcnt = dataSnapshot.child("carmodels").getChildrenCount();
                carModels = new String[(int)rcnt][(int)ccnt];
                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.child("carmodels").getChildren()){
                    carModels[i][0] = snapshot.getValue(String.class);
                    i++;
                }
                i=0;
                for(DataSnapshot snapshot:dataSnapshot.child("mileage").getChildren()){
                    carModels[i][1] = snapshot.getValue(String.class);
                    i++;
                }
                i=0;
                for(DataSnapshot snapshot:dataSnapshot.child("fueltype").getChildren()){
                    carModels[i][2] = snapshot.getValue(String.class);
                    i++;
                }
                i=0;
                for(DataSnapshot snapshot:dataSnapshot.child("capacity").getChildren()){
                    carModels[i][3] = snapshot.getValue(String.class);
                    i++;
                }
                i=0;
                for(DataSnapshot snapshot:dataSnapshot.child("rate").getChildren()){
                    carModels[i][4] = snapshot.getValue(String.class);
                    i++;
                }
                i=0;
                for(DataSnapshot snapshot:dataSnapshot.child("images").getChildren()){
                    carModels[i][5] = snapshot.getValue(String.class);
                    i++;
                }
                i=0;
                for(DataSnapshot snapshot:dataSnapshot.child("carRegNumber").getChildren()){
                    carModels[i][6] = snapshot.getValue(String.class);
                    i++;
                }
                mSwipeRefreshLayout.setRefreshing(false);
                createView(root);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(root,"Unable to Connect to Server !!!", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    public void createView(final View root) {
        recyclerBuild(root);
    }
    private void recyclerBuild(View root) {
        recyclerView = root.findViewById(R.id.RecyclerEvent);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new cardAdapter(Glide.with(root),carModels);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
}
