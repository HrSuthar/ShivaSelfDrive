package com.carrental.ShivaSD.bottomNav.home.manipulate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carrental.ShivaSD.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CarRemoveFragment extends Fragment {

    EditText carName, RCNo;
    Button RemoveBtn, cancelBtn;
    TextInputLayout modelError, registrationError;
    boolean isNameValid,isRegValid;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("CAR");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_car_remove, container, false);
        carName = root.findViewById(R.id.car_model);
        RCNo = root.findViewById(R.id.reg_no);
        RemoveBtn = root.findViewById(R.id.remove_Btn);
        cancelBtn = root.findViewById(R.id.remove_cancel);

        modelError =  root.findViewById(R.id.modelError);
        registrationError = root.findViewById(R.id.registrationError);


        RemoveBtn.setOnClickListener(v -> {
            if(SetValidation(v)){
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.child("carRegNumber").getChildren()) {
                            Toast.makeText(root.getContext(), RCNo.getText().toString(),Toast.LENGTH_LONG).show();

                            if (String.valueOf(dataSnapshot.getValue()).equals(RCNo.getText().toString())) {
                                String carNumber = dataSnapshot.getKey();
                                Toast.makeText(root.getContext(),carNumber,Toast.LENGTH_LONG).show();
                                assert carNumber != null;
                                myRef.child("carRegNumber").child(carNumber).removeValue();
                                myRef.child("capacity").child(carNumber).removeValue();
                                myRef.child("carmodels").child(carNumber).removeValue();
                                myRef.child("fueltype").child(carNumber).removeValue();
                                myRef.child("images").child(carNumber).removeValue();
                                myRef.child("mileage").child(carNumber).removeValue();
                                myRef.child("rate").child(carNumber).removeValue();

                                Toast.makeText(root.getContext(), "Entry Deleted Successful", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });

        cancelBtn.setOnClickListener(v -> requireActivity().onBackPressed());

        return root;
    }

    public Boolean SetValidation(View v) {

        if (carName.getText().toString().isEmpty()) {
            modelError.setError(getResources().getString(R.string.car_name_error));
            isNameValid = false;
        } else {
            isNameValid = true;
            modelError.setErrorEnabled(false);
        }
        if (RCNo.getText().toString().isEmpty()) {
            registrationError.setError(getResources().getString(R.string.car_regno_error));
            isRegValid = false;
        } else if (RCNo.getText().toString().length() != 10) {
            registrationError.setError(getResources().getString(R.string.error_invalid_rc));
            isRegValid = false;
        } else {
            isRegValid = true;
            registrationError.setErrorEnabled(false);
        }
        if (isNameValid && isRegValid) {
            Toast.makeText(v.getContext(), "Car Validate Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}