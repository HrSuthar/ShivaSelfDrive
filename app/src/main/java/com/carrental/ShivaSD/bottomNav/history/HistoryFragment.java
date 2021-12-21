package com.carrental.ShivaSD.bottomNav.history;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carrental.ShivaSD.R;
import com.carrental.ShivaSD.bottomNav.history.searchfrag.SearchFragment;
import com.carrental.ShivaSD.ui.customview.DateBlock;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class HistoryFragment extends Fragment {

    FragmentManager childFragManager;
    DateBlock sDateText, dDateText;
    EditText srcEditText, destEditText;
    Button searchBtn;
    RelativeLayout loadingBar;
    String srcLOC, destLOC, srcDate, destDate;
    SharedPreferences sharedPreferences;
    ArrayList<String[]> histDetails;
    String userID;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_ADMIN = "admin";
    private static final String KEY_USERID = "phone";
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("HISTORY");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_history, container, false);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        childFragManager = getChildFragmentManager();

        Calendar calendar = Calendar.getInstance();
        sDateText = root.findViewById(R.id.pick_up_date);
        dDateText = root.findViewById(R.id.destination_date);
        srcEditText = root.findViewById(R.id.sourceLOC);
        destEditText = root.findViewById(R.id.destinationLOC);
        loadingBar = root.findViewById(R.id.loadingPanel);

        int sDay = calendar.get(Calendar.DAY_OF_MONTH);
        int sMonth = calendar.get(Calendar.MONTH);
        int sYear = calendar.get(Calendar.YEAR);
        int dDay = calendar.get(Calendar.DAY_OF_MONTH);
        int dMonth = calendar.get(Calendar.MONTH);
        int dYear = calendar.get(Calendar.YEAR);

        sDateText.setOnClickListener(v -> new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            sDateText.setDate(String.valueOf(dayOfMonth), new SimpleDateFormat("EEEE").format(calendar.getTime()),
                    month, String.valueOf(year));
        }, sYear, sMonth, sDay).show());

        dDateText.setOnClickListener(v -> new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            dDateText.setDate(String.valueOf(dayOfMonth), new SimpleDateFormat("EEEE").format(calendar.getTime()),
                    month, String.valueOf(year));
        }, dYear, dMonth, dDay).show());


        userID =  sharedPreferences.getString(KEY_USERID, "007");
        searchBtn = root.findViewById(R.id.button);

        searchBtn.setOnClickListener(v -> {
            if(userID.equals("007"))
                alertDialog(v);
            else{
                searchLogic(v);
                loadingBar.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    private void alertDialog(View v){
        new AlertDialog.Builder(requireContext())
            .setTitle("Sign In")
            .setMessage(R.string.signInMsg)
            .setPositiveButton("Login", (dialog, which) -> {
                dialog.dismiss();
                Navigation.findNavController(v).navigate(R.id.action_navigation_history_to_login);
            })
            .setNegativeButton("Cancel",((dialog, which) -> dialog.dismiss()))
            .create()
            .show();
    }

    private void searchLogic(View v){
        srcLOC = srcEditText.getText().toString();
        destLOC = destEditText.getText().toString();
        srcDate = sDateText.getDate();
        destDate = dDateText.getDate();

        histDetails = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    if (String.valueOf(dataSnapshot.getKey()).equals(userID)) {

//                        if (!srcLOC.isEmpty() & !destLOC.isEmpty() & !srcDate.isEmpty() & !destDate.isEmpty()) {
//                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                                String rtnDate = String.valueOf(dataSnapshot1.child("ReturnDate").getValue());
//                                String deptLOC = String.valueOf(dataSnapshot1.child("Source").getValue());
//                                String rtnLOC = String.valueOf(dataSnapshot1.child("Destination").getValue());
//    // image ,carname, carloc, destLoc, rtndate, pickupdate, priceString, RCno, Status, Phone
//                                if (deptLOC.equals(srcLOC) & rtnLOC.equals(destLOC) & rtnDate.equals(destDate)) {
//                                    histDetails.add(new String[]{
//                                            dataSnapshot1.child("CarImage").getValue(String.class),
//                                            dataSnapshot1.child("CarName").getValue(String.class),
//                                            deptLOC,
//                                            String.valueOf(dataSnapshot1.child("Destination").getValue()),
//                                            String.valueOf(dataSnapshot1.child("ReturnDate").getValue()),
//                                            srcDate,
//                                            dataSnapshot1.child("AmountPaid").getValue(String.class),
//                                            dataSnapshot1.getKey(),
//                                            dataSnapshot1.child("Status").getValue(String.class),
//                                            userID
//                                    });
//                                }
//                            }
//                        } else if (!srcLOC.isEmpty() & !destLOC.isEmpty() & !srcDate.isEmpty()) {
//                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                                String deptDate = String.valueOf(dataSnapshot1.child("DepartDate").getValue());
//                                String deptLOC = String.valueOf(dataSnapshot1.child("Source").getValue());
//                                String rtnLOC = String.valueOf(dataSnapshot1.child("Destination").getValue());
//                                if (deptLOC.equals(srcLOC) & rtnLOC.equals(destLOC) & deptDate.equals(srcDate)) {
//                                    histDetails.add(new String[]{
//                                            String.valueOf(dataSnapshot1.child("CarImage").getValue()),
//                                            String.valueOf(dataSnapshot1.child("CarName").getValue()),
//                                            deptLOC,
//                                            String.valueOf(dataSnapshot1.child("Destination").getValue()),
//                                            String.valueOf(dataSnapshot1.child("ReturnDate").getValue()),
//                                            deptDate,
//                                            String.valueOf(dataSnapshot1.child("AmountPaid").getValue()),
//                                            dataSnapshot1.getKey(),
//                                            dataSnapshot1.child("Status").getValue(String.class),
//                                            userID
//                                    });
//                                }
//                            }
//                        } else
                            if (!srcLOC.isEmpty() & !destLOC.isEmpty()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String deptLOC = String.valueOf(dataSnapshot1.child("Source").getValue());
                                String rtnLOC = String.valueOf(dataSnapshot1.child("Destination").getValue());
                                if (deptLOC.equals(srcLOC) & rtnLOC.equals(destLOC)) {
                                    histDetails.add(new String[]{
                                            String.valueOf(dataSnapshot1.child("CarImage").getValue()),
                                            String.valueOf(dataSnapshot1.child("CarName").getValue()),
                                            deptLOC,
                                            String.valueOf(dataSnapshot1.child("Destination").getValue()),
                                            String.valueOf(dataSnapshot1.child("ReturnDate").getValue()),
                                            String.valueOf(dataSnapshot1.child("DepartDate").getValue()),
                                            String.valueOf(dataSnapshot1.child("AmountPaid").getValue()),
                                            dataSnapshot1.getKey(),
                                            dataSnapshot1.child("Status").getValue(String.class),
                                            userID
                                    });
                                }
                            }
                        } else if (!srcLOC.isEmpty()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String deptLOC = Objects.requireNonNull(dataSnapshot1.child("Source").getValue()).toString();
                                if (deptLOC.equals(srcLOC)) {
                                    histDetails.add(new String[]{
                                            String.valueOf(dataSnapshot1.child("CarImage").getValue()),
                                            String.valueOf(dataSnapshot1.child("CarName").getValue()),
                                            deptLOC,
                                            String.valueOf(dataSnapshot1.child("Destination").getValue()),
                                            String.valueOf(dataSnapshot1.child("ReturnDate").getValue()),
                                            String.valueOf(dataSnapshot1.child("DepartDate").getValue()),
                                            String.valueOf(dataSnapshot1.child("AmountPaid").getValue()),
                                            dataSnapshot1.getKey(),
                                            dataSnapshot1.child("Status").getValue(String.class),
                                            userID
                                    });
                                }
                            }
                        } else {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                histDetails.add(new String[]{
                                        String.valueOf(dataSnapshot1.child("CarImage").getValue()),
                                        String.valueOf(dataSnapshot1.child("CarName").getValue()),
                                        String.valueOf(dataSnapshot1.child("Source").getValue()),
                                        String.valueOf(dataSnapshot1.child("Destination").getValue()),
                                        String.valueOf(dataSnapshot1.child("ReturnDate").getValue()),
                                        String.valueOf(dataSnapshot1.child("DepartDate").getValue()),
                                        String.valueOf(dataSnapshot1.child("AmountPaid").getValue()),
                                        dataSnapshot1.getKey(),
                                        dataSnapshot1.child("Status").getValue(String.class),
                                        userID
                                });
                            }
                        }
                        break;
                    }// Don't Ever Put Navigation on Firebase Thread, Never Ever :(
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        new Handler().postDelayed(() -> {
            loadingBar.setVisibility(View.GONE);
            if(!histDetails.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("history",histDetails);
                Navigation.findNavController(v).navigate(R.id.action_navigation_history_to_searchhistoryFragment, bundle);
            }else NoSearchDialog();
        },4000);

    }

    private void NoSearchDialog(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Alert")
                .setMessage("Sorry! No Data Found")
                .setNegativeButton("Cancel",((dialog, which) -> dialog.dismiss()))
                .create()
                .show();
    }
}