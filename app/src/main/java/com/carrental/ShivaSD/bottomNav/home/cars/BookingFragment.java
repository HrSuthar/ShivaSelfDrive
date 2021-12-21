package com.carrental.ShivaSD.bottomNav.home.cars;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.carrental.ShivaSD.PaymentActivity;
import com.carrental.ShivaSD.R;
import com.carrental.ShivaSD.bottomNav.home.mailer.sendMail;
import com.carrental.ShivaSD.ui.customview.DateBlock;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookingFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    int n;
    FrameLayout mWrapperFL;
    TextView bookingCar;
    private AutoCompleteTextView pickUpLocation, dropLocation;
    SwitchCompat switchCompat;
    LinearLayout linearLayout;
    CardView cardViewCustomerDetails;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_USERID = "phone";
    double p1,bp,tp;
    private DateBlock startDateBlock, endDateBlock;
    String userId;
    TextView dailyPrice, bookPrice, extraHour, tax, totalAmount;
    EditText name, address, email, phone, reg_otp, pass;
    String cName, cAddress, cEmail, cPass, cPhone;
    String cPickDate,cPickTime, cDropDate, cDropTime, cPickupL, cDropL;
    String[] CustomerDetail, template;
    String finalAmount;

    private static final String[] SURAT_AREAS = new String[] {
            "Piplod", "Athwalines", "Surat Dumas Road", "Ghod Dod Road", "City Light",
            "Vesu",  "Katargam", "Adajan",  "Althan", "Canal Road", "VIP Road", "Varachha",
            "Kharwar Nagar", "Rander Road", "Rander", "Navagam", "Hazira - Adajan Road",
            "Vishnu Nagar", "Bhimrad", "Dahin Nagar", "Jahangir Pura", "Bhestan", "Saroli",
            "Parvat Gam", "Anand Mahal Road", "Bardoli", "Pal Gam", "Mota Varachha",
            "Olpad", "Athwa Gate", "Udhna", "Amroli", "Palsana", "Dindoli", "Palanpur Gam",
            "Sachin", "Pankaj Nagar", "Jahangirabad", "Sagrampura", "Mota", "Maroli", "Parvat Patiya",
            "Mahindra Pur", "Godadara", "Saniya Hemad", "Khodiyar Nagar", "Nanpura", "Athwa",
            "Kapodra", "Gothan", "Kamrej", "Salabatpura","Majura Gate","Kadodara","Laskana","Patel Nagar",
            "Pandesara", "Limbayat", "Gopipura", "Mughal Sarai", "Rustampura", "Bamroligam", "Begampura",
            "Kim", "Kosamba", "Kadodara Nagar","Bhatar", "Shahpore","Kumbharia Gam","Nana Varachha",
            "Mahuva","Chowk Bazar","Sayan","Haripura","Mahadev Nagar","Vidhey Nagar","Umarwada",
            "New City Light", "Tadwadi", "Punagam", "Sima Nagar", "Karamala", "Mandvi", "Dabholi",
            "Magob", "Limla", "Shakti Nagar", "Palanpur Jakatnaka", "Pasodara", "Nanavat", "Narthan",
            "Vareli", "Dumas", "Navsari Road", "Narotam Nagar", "Hazira", "Uttran",
            "New City Light Road", "Ichchhapor", "Ambanagar", "Masma", "Mosali"
    };

    Boolean flagStartDatePressed = Boolean.FALSE;
    Boolean flagEndDatePressed = Boolean.FALSE;

    RadioButton payLater, payNow;
    RadioGroup paymentGrp;
    boolean isNameValid, isEmailValid, isPhoneValid, isAddressValid, isPickupAddressValid, isPasswordValid;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("USER");
    DatabaseReference refDateBlock= FirebaseDatabase.getInstance().getReference("HISTORY");
    SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy",Locale.US);
    Calendar[] disabledDays1;
    Calendar calendar = Calendar.getInstance();
    int sDay = calendar.get(Calendar.DAY_OF_MONTH);
    int sMonth = calendar.get(Calendar.MONTH);
    int sYear = calendar.get(Calendar.YEAR);
    int sHour = calendar.get(Calendar.HOUR_OF_DAY);
    int sMin = calendar.get(Calendar.MINUTE);
    int dDay = calendar.get(Calendar.DAY_OF_MONTH);
    int dMonth = calendar.get(Calendar.MONTH);
    int dYear = calendar.get(Calendar.YEAR);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.z_activity_booking, container, false);
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userId =  sharedPreferences.getString(KEY_USERID, "007");

        assert getArguments() != null;
        String[] carList = getArguments().getStringArray("BookingCar");

        List<Calendar> datesWillBlock = new ArrayList<>();

        refDateBlock.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshots: snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot: dataSnapshots.getChildren()){
                        if(String.valueOf(dataSnapshot.getKey()).equals(carList[6])
                                && dataSnapshot.child("Status").getValue(String.class).equals("Success")){
                            try {
                                Date date1 = format.parse(dataSnapshot.child("DepartDate").getValue(String.class));
                                Date date2 = format.parse(dataSnapshot.child("ReturnDate").getValue(String.class));
                                if(date1.getTime() > Calendar.getInstance().getTimeInMillis() ||  date2.getTime() > Calendar.getInstance().getTimeInMillis()) {
                                    long Diff = (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
                                    for (long i = 0; i <= Diff; i++) {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(new Date(date1.getTime() + (i * (1000 * 60 * 60 * 24))));
                                        datesWillBlock.add(cal);
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                disabledDays1 = datesWillBlock.toArray(new Calendar[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        cardViewCustomerDetails = root.findViewById(R.id.customer_card_details);
        name =  root.findViewById(R.id.customer_name);
        address = root.findViewById(R.id.customer_address);
        email = root.findViewById(R.id.customer_email);
        phone = root.findViewById(R.id.customer_phone);
        reg_otp = root.findViewById(R.id.otp);
        pass = root.findViewById(R.id.customer_password);
        cName = name.getText().toString();
        cAddress = address.getText().toString();
        cEmail = email.getText().toString();
        cPhone = phone.getText().toString();

        if(!userId.equals("007")){
            cardViewCustomerDetails.setVisibility(View.GONE);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        if (String.valueOf(datasnapshot.getKey()).equals(userId)) {
                            cName = datasnapshot.child("Name").getValue(String.class);
                            cAddress = datasnapshot.child("Address").getValue(String.class);
                            cEmail = datasnapshot.child("Email").getValue(String.class);
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
            cPhone = userId;
        }

        ImageView carImage = root.findViewById(R.id.car_image);
        Glide.with(root).load(carList[5]).into(carImage);
        mWrapperFL = root.findViewById(R.id.flWrapper);

        ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, SURAT_AREAS);
        bookingCar = root.findViewById(R.id.booking_car_name);
        bookingCar.setText(carList[0]);
        pickUpLocation = root.findViewById(R.id.pick_up_address);
        pickUpLocation.setAdapter(dropDownAdapter);
        dropLocation = root.findViewById(R.id.drop_address);
        dropLocation.setAdapter(dropDownAdapter);
        startDateBlock = root.findViewById(R.id.pick_up_date);
        endDateBlock = root.findViewById(R.id.destination_date);
        switchCompat = root.findViewById(R.id.same_location);
        linearLayout = root.findViewById(R.id.drop_linerlayout);

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) ->
                linearLayout.setVisibility(!isChecked ? View.VISIBLE : View.GONE));

        autoloadDate();

        p1 = Integer.parseInt(carList[4].substring(1));
        double ehp = p1*0.08;
        tp = p1*0.18;
        bp = 500;
        String rs = "₹";
        dailyPrice = root.findViewById(R.id.daily_price);
        dailyPrice.setText(carList[4]);
        bookPrice = root.findViewById(R.id.book_price);
        bookPrice.setText(String.format("%s%s",rs,bp));
        extraHour = root.findViewById(R.id.extra_hour);
        extraHour.setText(String.format("%s%s", rs, ehp));
        tax = root.findViewById(R.id.tax);
        tax.setText(String.format("%s%s",rs,tp));
        totalAmount = root.findViewById(R.id.total_amount);
        totalAmount.setText(String.format("%s%s",rs,p1+bp+tp));


        payLater = root.findViewById(R.id.pay_now);
        payNow = root.findViewById(R.id.pay_later);
        paymentGrp = root.findViewById(R.id.payment_option);


        Button confirmButton = root.findViewById(R.id.confirmBtn);
        Button confirmNowButton = root.findViewById(R.id.confirmNowBtn);


        startDateBlock.setOnClickListener(v -> {
            flagStartDatePressed = true;
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, sYear, sMonth, sDay);
            datePickerDialog.setMinDate(Calendar.getInstance());
            if(flagEndDatePressed)
                datePickerDialog.setMaxDate(calendar);
            datePickerDialog.setThemeDark(true);
            datePickerDialog.setDisabledDays(disabledDays1);
            datePickerDialog.show(getChildFragmentManager(), "StartDatePickerDialog");
        });

        endDateBlock.setOnClickListener(v -> {
            flagEndDatePressed = true;
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, dYear, dMonth, dDay);
            datePickerDialog.setMinDate(calendar);
            datePickerDialog.setThemeDark(true);
            datePickerDialog.setDisabledDays(disabledDays1);
            datePickerDialog.show(getChildFragmentManager(), "EndDatePickerDialog");
        });


        paymentGrp.setOnCheckedChangeListener((group, checkedId) -> {
            Toast.makeText(getContext(),String.valueOf(checkedId),Toast.LENGTH_LONG).show();
            if(checkedId == R.id.pay_now){
                startActivity(new Intent(getActivity(), PaymentActivity.class)
                        .putExtra("Amount", totalAmount.getText().toString()));
            }
        });

        confirmButton.setOnClickListener(v -> {
            if(userId.equals("007")) {
                reg_otp.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.GONE);
                confirmNowButton.setVisibility(View.VISIBLE);
            }
            Bundle bundle = reFetchData(carList);
            if(SetValidation(v)){
                if(userId.equals("007")) {
                    confirmNowButton.setOnClickListener(v1 -> {
                        if (!reg_otp.getText().toString().isEmpty()) {
                            if (reg_otp.getText().toString().equals(String.valueOf(n))){
                                myRef.child(cPhone).child("Address").setValue(cAddress);
                                myRef.child(cPhone).child("Admin").setValue("false");
                                myRef.child(cPhone).child("Email").setValue(cEmail);
                                myRef.child(cPhone).child("Password").setValue(cPass);
                                myRef.child(cPhone).child("Name").setValue(cName);
                                myRef.child(cPhone).child("ProfilePhoto").setValue("https://firebasestorage.googleapis.com/v0/b/shivatours-4b4b0.appspot.com/o/Profiles%2Fdefaultprofile.jpeg?alt=media&token=4ef6fd1c-f0c6-4a74-b472-80ef1073e955");
                                mailing(v,bundle);
                            }
                            else Toast.makeText(getContext(), "Please Enter Valid OTP", Toast.LENGTH_LONG).show();
                        } else Toast.makeText(getContext(), "Please Enter OTP", Toast.LENGTH_LONG).show();
                    });
                }else mailing(v,bundle);
            }
        });

        return root;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year,monthOfYear,dayOfMonth);
        boolean Sdate = false;
        if(flagStartDatePressed) {
            startDateBlock.setDate(String.valueOf(dayOfMonth), new SimpleDateFormat("EEEE", Locale.US)
                    .format(calendar.getTime()), monthOfYear, String.valueOf(year));
            endDateBlock.setDate(String.valueOf(dayOfMonth + 1), new SimpleDateFormat("EEEE", Locale.US)
                    .format(calendar.getTime()), monthOfYear, String.valueOf(year));
            flagStartDatePressed = false;
            Sdate = true;
        }
        if(flagEndDatePressed){
            endDateBlock.setDate(String.valueOf(dayOfMonth), new SimpleDateFormat("EEEE", Locale.US)
                    .format(calendar.getTime()), monthOfYear, String.valueOf(year));
            flagEndDatePressed = false;
        }
        AtomicBoolean finalSdate = new AtomicBoolean(Sdate);
        new TimePickerDialog(getContext(), (view1, hourOfDay, minute) ->{
            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.YEAR, year);
            datetime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            datetime.set(Calendar.MONTH, monthOfYear);
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay - 1);
            datetime.set(Calendar.MINUTE, minute);
            if (datetime.getTimeInMillis() >= Calendar.getInstance().getTimeInMillis()) {
                if(finalSdate.get()) {
                    startDateBlock.setTime(hourOfDay + ":" + minute);
                    finalSdate.set(false);
                }else endDateBlock.setTime(hourOfDay + ":" + minute);
            }
            else {
                if (finalSdate.get()) {
                    Toast.makeText(getContext(), "Minimum 1 Hours Advance Booking Allowed ", Toast.LENGTH_LONG).show();
                    finalSdate.set(false);
                }
            }
        }, sHour,sMin,false).show();
    }


    private void autoloadDate(){
        Date currDate = calendar.getTime();
        Calendar h_cal = Calendar.getInstance();
        h_cal.add(Calendar.HOUR_OF_DAY,1);
        startDateBlock.setDate(new SimpleDateFormat("dd",Locale.US).format(currDate)
                , new SimpleDateFormat("EEEE",Locale.US).format(currDate),
                Integer.parseInt(new SimpleDateFormat("MM", Locale.US).format(currDate)) -1
                , new SimpleDateFormat("yyyy",Locale.US).format(currDate));
        startDateBlock.setTime(new SimpleDateFormat("hh",Locale.US)
                .format(h_cal.getTime()) + ":"+ new SimpleDateFormat("mm",Locale.US).format(currDate));

        Calendar t_cal = Calendar.getInstance();
        t_cal.add(Calendar.DATE,1); // tomorrow
        endDateBlock.setDate(new SimpleDateFormat("dd",Locale.US).format(t_cal.getTime())
                , new SimpleDateFormat("EEEE",Locale.US).format(currDate),
                Integer.parseInt(new SimpleDateFormat("MM", Locale.US).format(currDate)) -1
                , new SimpleDateFormat("yyyy",Locale.US).format(currDate));
        endDateBlock.setTime(new SimpleDateFormat("hh",Locale.US)
                .format(h_cal.getTime()) + ":"+ new SimpleDateFormat("mm",Locale.US).format(currDate));
    }

    private Bundle reFetchData(String[] carList) {
        cPickupL = pickUpLocation.getText().toString();
        cDropL = dropLocation.getText().toString();
        if(switchCompat.isChecked())  cDropL = cPickupL;
        if(userId.equals("007")) {
            cName = name.getText().toString();
            cAddress = address.getText().toString();
            cEmail = email.getText().toString();
            cPhone = phone.getText().toString();
            cPass = pass.getText().toString();
        }

        cPickDate = startDateBlock.getDate();
        cPickTime = startDateBlock.getTime();
        cDropDate = endDateBlock.getDate();
        cDropTime = endDateBlock.getTime();
        long Diff = 0;
        try {
            Date date1 = format.parse(cPickDate);
            Date date2 = format.parse(cDropDate);
            assert date2 != null;
            assert date1 != null;
            Diff = (date2.getTime() - date1.getTime())/ (24 * 60 * 60 * 1000);
        }catch (ParseException e){
            e.printStackTrace();
        }
        if(Diff >=1) finalAmount = String.valueOf(Diff * (p1+bp+tp));
        else finalAmount = String.valueOf(p1+bp+tp);

        String vehRegNo = carList[6];
        CustomerDetail = new String[]{
                cPickupL, cPickDate, cPickTime
                , cName, cAddress, cEmail, cPhone
                , totalAmount.getText().toString()
                , cDropDate, cDropL, vehRegNo,finalAmount};
        template = new String[]{
                "Pickup Loc:","Date:      ","Time:      "
                ,"Name:      ","Address:   ","Email:     ","Contact:   "
                ,"Amount:    ", "ReturnDate:", "Drop Loc:  ", "RC Number: ", "Final Amount"};

        Bundle bundle = new Bundle();
        bundle.putStringArray("BookedCar",carList);
        bundle.putStringArray("CustomerBook",CustomerDetail);
        return bundle;
    }

    public void mailing(View v, Bundle bundle){
        try {
            StringBuilder msg = new StringBuilder();
            for (int i = 0; i < CustomerDetail.length; ++i)
                msg.append(template[i]).append("\t").append(CustomerDetail[i]).append("\n");

            new sendMail("Booking Confirm",
                    String.valueOf(msg),
                    "jadonmaheshpalsingh@gmail.com",
                    this.getContext()).execute();

            Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_booking_to_navigation_checkout, bundle);

        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(v, "Booking Failed! Please Try Again", Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean SetValidation(View v) {
        // Check for a valid Pickup address.
        if (cPickupL.isEmpty()) {
            pickUpLocation.setError(getResources().getString(R.string.pickup_address_error));
            isPickupAddressValid = false;
        } else  {
            isPickupAddressValid = true;
        }

        // Check for a valid name.
        if (cName.isEmpty()) {
            name.setError(getResources().getString(R.string.name_error));
            isNameValid = false;
        } else  {
            isNameValid = true;
        }

        // Check for a valid address.
        if (cAddress.isEmpty()) {
            address.setError(getResources().getString(R.string.address_error));
            isAddressValid = false;
        } else  {
            isAddressValid = true;
        }

        // Check for a valid email address.
        if (cEmail.isEmpty()) {
            email.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(cEmail).matches()) {
            email.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else  {
            isEmailValid = true;
        }

        if(userId.equals("007")){
            if (cPass.isEmpty()) {
                pass.setError(getResources().getString(R.string.password_error));
                isPasswordValid = false;
            } else if (cPass.length() < 6) {
                pass.setError(getResources().getString(R.string.error_invalid_password));
                isPasswordValid = false;
            } else  {
                isPasswordValid = true;
    //            pass.setError(false);
            }
        }

        // Check for a valid phone number.
        if (cPhone.isEmpty()) {
            phone.setError(getResources().getString(R.string.phone_error));
            isPhoneValid = false;
        } else if(!Patterns.PHONE.matcher(cPhone).matches()) {
            phone.setError(getResources().getString(R.string.error_invalid_phone));
            isPhoneValid = false;
        }else {
            isPhoneValid = true;
        }

        if (isNameValid && isPhoneValid && isPickupAddressValid && isEmailValid && isAddressValid) {
            Toast.makeText(v.getContext(), "Validation Successfully"+userId, Toast.LENGTH_SHORT).show();

            if(userId.equals("007") && isPasswordValid) {
                Random rand = new Random();
                n = rand.nextInt(55320) + 6;
                String msg = "Please Enter " + n + " as your OTP for Verification ->";

                new sendMail("Your OTP FOR Shiva Self Drive application ",
                        msg,
                        cEmail,
                        this.getContext()).execute();
            }
            return true;
        }
        return false;
    }

}

