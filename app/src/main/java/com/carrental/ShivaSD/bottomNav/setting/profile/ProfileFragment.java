package com.carrental.ShivaSD.bottomNav.setting.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carrental.ShivaSD.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    ImageView profImg;
    ProgressBar loadingProfile;
    FloatingActionButton fabAdd;
    TextView profName, profContact, profEmail, profAddress, profUserType;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("USER");
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_USERID = "phone";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString(KEY_USERID, "007");

        profImg = root.findViewById(R.id.profile_image);
        profName = root.findViewById(R.id.profile_name);
        profContact = root.findViewById(R.id.profile_contact);
        profEmail = root.findViewById(R.id.profile_mail);
        profAddress = root.findViewById(R.id.profile_address);
        fabAdd = root.findViewById(R.id.fab_add);
        profUserType =  root.findViewById(R.id.profile_userType);
        profUserType.setText("User");
        loadingProfile = root.findViewById(R.id.loading_profile);
        loadingProfile.setVisibility(View.VISIBLE);

        fabAdd.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_profileFragment_to_manageProfileFragment));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    if(String.valueOf(datasnapshot.getKey()).equals(userID)){
                        Glide.with(root).load(datasnapshot.child("ProfilePhoto").getValue(String.class)).into(profImg);
                        loadingProfile.setVisibility(View.GONE);
                        profAddress.setText(datasnapshot.child("Address").getValue(String.class));
                        profName.setText(datasnapshot.child("Name").getValue(String.class));
                        profEmail.setText(datasnapshot.child("Email").getValue(String.class));
                        profContact.setText(datasnapshot.getKey());
                        if(datasnapshot.child("Admin").getValue(String.class).equals("true"))
                            profUserType.setText("Admin");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        return root;
    }
}