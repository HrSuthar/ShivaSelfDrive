package com.carrental.ShivaSD.bottomNav.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carrental.ShivaSD.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends Fragment {


    EditText name, email, phone, password, address;
    Button register;
    TextView login, whichUser;
    boolean isNameValid, isEmailValid, isPhoneValid, isPasswordValid;
    TextInputLayout nameError, emailError, phoneError, passError;
    DatabaseReference regRef = FirebaseDatabase.getInstance().getReference("USER");
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_ADMIN = "admin";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root=  inflater.inflate(R.layout.fragment_registration, container, false);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String admin = sharedPreferences.getString(KEY_ADMIN,"false");

        whichUser = root.findViewById(R.id.user_admin_textView);
        name =  root.findViewById(R.id.name);
        email =  root.findViewById(R.id.email);
        phone =  root.findViewById(R.id.phone);
        password =  root.findViewById(R.id.password);
        address =  root.findViewById(R.id.address);
        login =  root.findViewById(R.id.login);
        register =  root.findViewById(R.id.register);
        nameError =  root.findViewById(R.id.nameError);
        emailError =  root.findViewById(R.id.emailError);
        phoneError =  root.findViewById(R.id.phoneError);
        passError =  root.findViewById(R.id.passError);

        if(admin.equals("true")) {
            whichUser.setText("Admin");
            login.setVisibility(View.GONE);
        } else whichUser.setText("User");

        register.setOnClickListener(v -> {
            if(SetValidation(v)){
                regRef.child(phone.getText().toString()).child("Name").setValue(name.getText().toString());
                regRef.child(phone.getText().toString()).child("Email").setValue(email.getText().toString());
                regRef.child(phone.getText().toString()).child("Password").setValue(password.getText().toString());
                regRef.child(phone.getText().toString()).child("Address").setValue(address.getText().toString());
                regRef.child(phone.getText().toString()).child("ProfilePhoto").setValue("https://firebasestorage.googleapis.com/v0/b/shivatours-4b4b0.appspot.com/o/images%2Finnova.png?alt=media&token=d488e71a-5ec1-4360-83b7-6b436f7251a4");
                if(admin.equals("true"))
                    regRef.child(phone.getText().toString()).child("Admin").setValue("true");
                else
                    regRef.child(phone.getText().toString()).child("Admin").setValue("false");

                Navigation.findNavController(v).navigate(R.id.action_registration_to_login);
            }
        });

        login.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_registration_to_login));

        return root;
    }


    public Boolean SetValidation(View v) {
        // Check for a valid name.
        if (name.getText().toString().isEmpty()) {
            nameError.setError(getResources().getString(R.string.name_error));
            isNameValid = false;
        } else  {
            isNameValid = true;
            nameError.setErrorEnabled(false);
        }

        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else  {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid phone number.
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

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isNameValid && isEmailValid && isPhoneValid && isPasswordValid) {
            Toast.makeText(v.getContext(), "Registration Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}