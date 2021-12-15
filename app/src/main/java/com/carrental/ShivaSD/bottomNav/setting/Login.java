package com.carrental.ShivaSD.bottomNav.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carrental.ShivaSD.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends Fragment {

    EditText email, password;
    Button login;
    RelativeLayout loadingBar;
    TextView register;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference().child("USER");
    String admin, phone;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_ADMIN = "admin";
    private static final String KEY_USERID = "phone";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        login = root.findViewById(R.id.login);
        register = root.findViewById(R.id.register);
        emailError = root.findViewById(R.id.emailError);
        passError = root.findViewById(R.id.passError);
        loadingBar = root.findViewById(R.id.loadingPanel);
        login.setOnClickListener(this::SetValidation);

        register.setOnClickListener(v -> Navigation.findNavController(v)
                    .navigate(R.id.action_login_to_registration));

        return root;
    }

    public void SetValidation(View v) {
        loadingBar.setVisibility(View.VISIBLE);

        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot.child("Email").getValue()).equals(email.getText().toString())) {
                            if (String.valueOf(dataSnapshot.child("Password").getValue()).equals(password.getText().toString())) {
                                admin = String.valueOf(dataSnapshot.child("Admin").getValue());
                                phone = dataSnapshot.getKey();
                                sharedPreferences.edit().putString(KEY_ADMIN, admin).apply();
                                sharedPreferences.edit().putString(KEY_USERID, phone).apply();
                                sharedPreferences.edit().putString(KEY_USERID, phone).apply();
                                sharedPreferences.edit().putString(KEY_USERID, phone).apply();
                                isPasswordValid = true;
                                break;
                            }
                        } else
                            isPasswordValid = false;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            new Handler().postDelayed(() -> {
                loadingBar.setVisibility(View.GONE);
                if(isEmailValid && isPasswordValid) {
                    Toast.makeText(v.getContext(), "Successfully Login", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(v).popBackStack(R.id.navigation_home,false);
                }else
                    Snackbar.make(v, "Incorrect UserID or Password", Snackbar.LENGTH_LONG).show();
            }, 5000);
        }
    }
}