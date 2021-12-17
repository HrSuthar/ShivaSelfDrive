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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.carrental.ShivaSD.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class EditProfileFragment extends Fragment {

    String userId;
    EditText phone, address;
    boolean isPhoneValid, isAddressValid;
    TextInputLayout phoneError, addressError;
    Button profileImage, cancelBtn, updateBtn;
    ProgressBar progressBar;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    Uri imageUri;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("USER");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_USERID = "phone";

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

        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        phone = root.findViewById(R.id.phone);
        address = root.findViewById(R.id.address);
        profileImage =  root.findViewById(R.id.add_image_url);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userId =  sharedPreferences.getString(KEY_USERID, "007");

        profileImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            someActivityResultLauncher.launch(galleryIntent);
        });

        updateBtn.setOnClickListener(v -> {
            if(SetValidation(v)){
                progressBar.setVisibility(View.VISIBLE);
                uploadToFirebase();
                new Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    Navigation.findNavController(v).popBackStack(R.id.navigation_home,false);
                },4000);
            }
        });

        cancelBtn.setOnClickListener(v -> requireActivity().onBackPressed());


        return root;
    }

    private void uploadToFirebase(){
        StorageReference fileRef = storageReference.child("Profiles").child(System.currentTimeMillis() + ".png");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                myRef.child(userId).child("Address").setValue(address.getText().toString());
//                                myRef.child("carmodels").child("").setValue(phone.getText().toString());
                                myRef.child(userId).child("ProfilePhoto").setValue(uri.toString());

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(),"Upload Failed!!",Toast.LENGTH_LONG).show();
                            }
                        })
                )
        ).addOnFailureListener(e ->
                Toast.makeText(getContext(),"Upload Failed!!",Toast.LENGTH_LONG).show());
    }

    public Boolean SetValidation(View v) {
        if (phone.getText().toString().isEmpty()) {
            phoneError.setError(getResources().getString(R.string.phone_error));
            isPhoneValid = false;
        }else if(!Patterns.PHONE.matcher(phone.getText().toString()).matches()) {
            phoneError.setError(getResources().getString(R.string.error_invalid_phone));
            isPhoneValid = false;
        } else  {
            isPhoneValid = true;
            phoneError.setErrorEnabled(false);
        }

        if (address.getText().toString().isEmpty()) {
            addressError.setError(getResources().getString(R.string.address_error));
            isAddressValid = false;
        } else  {
            isAddressValid = true;
        }

        if ( isPhoneValid && isAddressValid){
            Toast.makeText(v.getContext(), "Registration Successfully", Toast.LENGTH_SHORT).show();
            return  true;
        }
        return  false;
    }

}