package com.carrental.ShivaSD.bottomNav.setting.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carrental.ShivaSD.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ManageProfileFragment extends Fragment {
    String userId;
    EditText phone, address, password;
    boolean isPasswordValid, isAddressValid;
    TextInputLayout passError, addressError;
    Button profileImageBtn, cancelBtn, updateBtn;
    ProgressBar progressBar;
    TextView headerManage;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    Uri imageUri;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("USER");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_USERID = "phone";
    String strPassword, strAddress, strURL;
    boolean updatebtnPressed = false;
    ImageView profilePhoto;
    ProgressBar loadingProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    imageUri = data.getData();
                }
            });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_manage_profile, container, false);

        address = root.findViewById(R.id.address);
        loadingProfile = root.findViewById(R.id.loading_profile);
        loadingProfile.setVisibility(View.VISIBLE);
        password = root.findViewById(R.id.password);
        profilePhoto = root.findViewById(R.id.profile_image);
        headerManage = root.findViewById(R.id.update_Profile_textView);
        profileImageBtn =  root.findViewById(R.id.add_profile_url);
        updateBtn = root.findViewById(R.id.change_Btn);
        cancelBtn = root.findViewById(R.id.cancel_btn);
        progressBar = root.findViewById(R.id.profile_add_progress);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userId =  sharedPreferences.getString(KEY_USERID, "007");
        fetchFromFirebase(root);


        profileImageBtn.setOnClickListener(v -> {
            updatebtnPressed = true;
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            someActivityResultLauncher.launch(galleryIntent);
        });

        updateBtn.setOnClickListener(v -> {
            if(SetValidation(v)){
                progressBar.setVisibility(View.VISIBLE);
                updateToFirebase();
                new Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    Navigation.findNavController(v).popBackStack(R.id.navigation_home,false);
                },4000);
            }
        });

        cancelBtn.setOnClickListener(v -> requireActivity().onBackPressed());

        return root;
    }

    private void fetchFromFirebase(View root){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                strAddress = snapshot.child(userId).child("Address").getValue(String.class);
                strPassword = snapshot.child(userId).child("Password").getValue(String.class);
                strURL = snapshot.child(userId).child("ProfilePhoto").getValue(String.class);
                Glide.with(root).load(strURL).into(profilePhoto);
                loadingProfile.setVisibility(View.GONE);
                headerManage.setText("Update Profile");
                address.setText(strAddress);
                password.setText(strPassword);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Fetch Failed!!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateToFirebase(){

        myRef.child(userId).child("Address").setValue(address.getText().toString());
        myRef.child(userId).child("Password").setValue(password.getText().toString());
        myRef.child(userId).child("ProfilePhoto").setValue(strURL);

        if(updatebtnPressed) {
            StorageReference fileRef = storageReference.child("Profiles").child(System.currentTimeMillis() + ".png");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        myRef.child(userId).child("ProfilePhoto").setValue(uri.toString());
                        Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_LONG).show();
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Photo Upload Failed!!", Toast.LENGTH_LONG).show());
        }else
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_LONG).show();
    }

    public Boolean SetValidation(View v) {

        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
        }

        if (address.getText().toString().isEmpty()) {
            addressError.setError(getResources().getString(R.string.address_error));
            isAddressValid = false;
        } else  {
            isAddressValid = true;
        }

        if (isAddressValid){
            Toast.makeText(v.getContext(), "Validate Successfully", Toast.LENGTH_SHORT).show();
            return  true;
        }
        return  false;
    }

}