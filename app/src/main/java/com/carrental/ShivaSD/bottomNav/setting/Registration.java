package com.carrental.ShivaSD.bottomNav.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.carrental.ShivaSD.R;
import com.carrental.ShivaSD.bottomNav.home.mailer.sendMail;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Registration extends Fragment {

    int n;
    EditText name, email, phone, password, address, reg_otp;
    Button register, registerNow;
    TextView login, whichUser;
    boolean isNameValid, isEmailValid, isPhoneValid, isPasswordValid, isAddressValid;
    TextInputLayout nameError, emailError, phoneError, passError, addressError;
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
        registerNow = root.findViewById(R.id.register_now);
        reg_otp = root.findViewById(R.id.otp);
        nameError =  root.findViewById(R.id.nameError);
        emailError =  root.findViewById(R.id.emailError);
        phoneError =  root.findViewById(R.id.phoneError);
        passError =  root.findViewById(R.id.passError);

        if(admin.equals("true")) {
            whichUser.setText("Admin");
            login.setVisibility(View.GONE);
        } else whichUser.setText("User");

        register.setOnClickListener(v -> {
            reg_otp.setVisibility(View.VISIBLE);
            register.setVisibility(View.GONE);
            registerNow.setVisibility(View.VISIBLE);
            if(SetValidation(v)){
                registerNow.setOnClickListener(v1 ->{
                    if(!reg_otp.getText().toString().isEmpty()){
                        if(reg_otp.getText().toString().equals(String.valueOf(n))) {
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
                        }else
                            Toast.makeText(getContext(),"Please Enter Valid OTP",Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(getContext(),"Please Enter OTP",Toast.LENGTH_LONG).show();
                });

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

        if (address.getText().toString().isEmpty()) {
            addressError.setError(getResources().getString(R.string.address_error));
            isAddressValid = false;
        } else  {
            isAddressValid = true;
        }


        if (isNameValid && isEmailValid && isPhoneValid && isPasswordValid && isAddressValid) {
            Toast.makeText(v.getContext(), "Registration Successfully", Toast.LENGTH_SHORT).show();

            Random rand = new Random();
            n = rand.nextInt(55320) + 1;
            String msg = "Please Enter "+ n+" as your OTP for Verification ->";

            new sendMail("Your OTP FOR Shiva Self Drive application ",
                    msg,
                    email.getText().toString(),
                    this.getContext()).execute();

            return true;
        }
        return false;
    }

}