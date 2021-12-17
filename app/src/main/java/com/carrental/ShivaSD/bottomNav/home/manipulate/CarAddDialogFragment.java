package com.carrental.ShivaSD.bottomNav.home.manipulate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.carrental.ShivaSD.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CarAddDialogFragment extends Fragment {

    EditText carName, carMileage, carFuel, CarSeats, carCharges, carRegNo;
    DatabaseReference addRef = FirebaseDatabase.getInstance().getReference("CAR");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    Uri imageUri;
    ProgressBar progressBar;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    Button cancelBtn,carImage, addBtn;
    boolean isNameValid, isRateValid, isMileageValid, isFuelValid, isSeatValid, isUrlValid, isRegValid;
    TextInputLayout modelError, mileageError, fuelError, seatError, chargesError, registrationError;

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
        View root = inflater.inflate(R.layout.fragment_car_add_dialog, container, false);

        carName =  root.findViewById(R.id.add_car_model);
        carImage =  root.findViewById(R.id.add_image_url);
        carMileage =  root.findViewById(R.id.add_mileage);
        carFuel =  root.findViewById(R.id.add_fuel_type);
        CarSeats =  root.findViewById(R.id.add_seat_capacity);
        carCharges =  root.findViewById(R.id.add_charges);
        carRegNo = root.findViewById(R.id.add_reg_no);
        cancelBtn =  root.findViewById(R.id.add_cancel);
        addBtn =  root.findViewById(R.id.add_Btn);
        progressBar = root.findViewById(R.id.car_add_progress);

        modelError =  root.findViewById(R.id.modelError);
        mileageError =  root.findViewById(R.id.mileageError);
        fuelError =  root.findViewById(R.id.fuelError);
        seatError =  root.findViewById(R.id.seatError);
        chargesError =  root.findViewById(R.id.chargesError);
        registrationError = root.findViewById(R.id.registrationError);

        carImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            someActivityResultLauncher.launch(galleryIntent);
        });

        addBtn.setOnClickListener(v -> {
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
        final long[] childCnt = new long[1];
        StorageReference fileRef = storageReference.child("images").child(System.currentTimeMillis() + ".png");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                        addRef.child("carmodels").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            childCnt[0] = snapshot.getChildrenCount();
                            childCnt[0]++;
                            addRef.child("capacity").child("car"+childCnt[0]).setValue(CarSeats.getText().toString()+" Seator");
                            addRef.child("carmodels").child("car"+childCnt[0]).setValue(carName.getText().toString());
                            addRef.child("fueltype").child("car"+childCnt[0]).setValue(carFuel.getText().toString());
                            addRef.child("mileage").child("car"+childCnt[0]).setValue(carMileage.getText().toString() + " km");
                            addRef.child("rate").child("car"+childCnt[0]).setValue("₹"+carCharges.getText().toString());
                            addRef.child("carRegNumber").child("car"+childCnt[0]).setValue(carRegNo.getText().toString());
                            addRef.child("images").child("car"+childCnt[0]).setValue(uri.toString());

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    })
                )
        ).addOnFailureListener(e ->
                            Toast.makeText(getContext(),"Upload Failed!!",Toast.LENGTH_LONG).show());
    }


    private Boolean SetValidation(View v) {

        if (carName.getText().toString().isEmpty()) {
            modelError.setError(getResources().getString(R.string.car_name_error));
            isNameValid = false;
        } else  {
            isNameValid = true;
            modelError.setErrorEnabled(false);
        }

        if (carMileage.getText().toString().isEmpty()) {
            mileageError.setError(getResources().getString(R.string.car_mileage_error));
            isMileageValid = false;
        } else  {
            isMileageValid = true;
            mileageError.setErrorEnabled(false);
        }

        if (carFuel.getText().toString().isEmpty()) {
            fuelError.setError(getResources().getString(R.string.car_fuel_error));
            isFuelValid = false;
        } else  {
            isFuelValid = true;
            fuelError.setErrorEnabled(false);
        }
        if (carCharges.getText().toString().isEmpty()) {
            chargesError.setError(getResources().getString(R.string.car_charges_error));
            isRateValid = false;
        } else  {
            isRateValid = true;
            chargesError.setErrorEnabled(false);
        }

        if (CarSeats.getText().toString().isEmpty()) {
            seatError.setError(getResources().getString(R.string.car_seat_error));
            isSeatValid = false;
        } else if (Integer.parseInt(CarSeats.getText().toString()) < 4) {
            seatError.setError(getResources().getString(R.string.error_invalid_seat));
            isSeatValid = false;
        } else  {
            isSeatValid = true;
            seatError.setErrorEnabled(false);
        }

        if (carRegNo.getText().toString().isEmpty()) {
            registrationError.setError(getResources().getString(R.string.car_regno_error));
            isRegValid = false;
        } else if (carRegNo.getText().toString().length()!= 10) {
            registrationError.setError(getResources().getString(R.string.error_invalid_rc));
            isRegValid = false;
        } else  {
            isRegValid = true;
            registrationError.setErrorEnabled(false);
        }

        if ( imageUri!= null && isNameValid && isRateValid && isMileageValid && isFuelValid && isSeatValid && isRegValid) {
            Toast.makeText(v.getContext(), "Car Validate Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}


//
//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // There are no request codes
//                        Intent data = result.getData();
//                        doSomeOperations();
//                    }
//                }
//            });
//
//    public void openSomeActivityForResult() {
//        Intent intent = new Intent(this, SomeActivity.class);
//        someActivityResultLauncher.launch(intent);
//    }
