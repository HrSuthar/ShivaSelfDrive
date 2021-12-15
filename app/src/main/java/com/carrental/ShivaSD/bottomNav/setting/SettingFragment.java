package com.carrental.ShivaSD.bottomNav.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.carrental.ShivaSD.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingFragment extends Fragment {

    TextView legal;
    TextView login;
    TextView logout;
    TextView contact, profile, howWorks;
    FloatingActionButton floatingActionButton;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_ADMIN = "admin";
    private static final String KEY_USERID = "phone";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        floatingActionButton = root.findViewById(R.id.floating_action_button);
        legal = root.findViewById(R.id.legal);
        login = root.findViewById(R.id.notifyLogin);
        howWorks = root.findViewById(R.id.notifyWorks);
        contact = root.findViewById(R.id.notifyContact);
        logout = root.findViewById(R.id.notifyLogOut);
        profile  = root.findViewById(R.id.notifyProfile);

        String userID = sharedPreferences.getString(KEY_USERID, "007");
        if(!userID.equals("007")){
            profile.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
        }

        profile.setOnClickListener(v-> Navigation.findNavController(v)
                .navigate(R.id.action_navigation_setting_to_profileFragment));
        legal.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_navigation_notifications_to_developing));
        login.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_navigation_setting_to_login));
        logout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Navigation.findNavController(view).popBackStack();
            Toast.makeText(view.getContext(),"Logout Successful",Toast.LENGTH_LONG).show();
        });
        howWorks.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_navigation_notifications_to_developing));
        contact.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_navigation_notifications_to_developing));

        String admin = sharedPreferences.getString(KEY_ADMIN, "false");
        if(admin.equals("true")) {
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setOnClickListener(v -> Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_setting_to_registration));
        }
        return root;
    }
}