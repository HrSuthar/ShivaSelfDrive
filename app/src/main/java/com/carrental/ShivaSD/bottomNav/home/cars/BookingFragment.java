package com.carrental.ShivaSD.bottomNav.home.cars;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.carrental.ShivaSD.bottomNav.home.cars.mailer.sendMail;
import com.carrental.ShivaSD.ui.customview.DateBlock;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingFragment extends Fragment {

    FrameLayout mWrapperFL;
    TextView bookingCar;
    private EditText pickUpLocation, dropLocation;
    SwitchCompat switchCompat;
    LinearLayout linearLayout;
    CardView cardViewCustomerDetails;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_USERID = "phone";

    private DateBlock startDateBlock, endDateBlock;
    String userId;
    TextView dailyPrice,bookPrice, extraHour, tax, totalAmount;
    EditText name, address, email, phone;
    String cName, cAddress, cEmail, cPhone, cPickDate,cPickTime, cDropDate, cDropTime, cPickupL, cDropL;
    String[] CustomerDetail,template;

    RadioButton payLater, payNow;
    RadioGroup paymentGrp;
    boolean isNameValid, isEmailValid, isPhoneValid,isAddressValid,isPickupAddressValid;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("USER");

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

        cardViewCustomerDetails = root.findViewById(R.id.customer_card_details);
        name =  root.findViewById(R.id.customer_name);
        address = root.findViewById(R.id.customer_address);
        email = root.findViewById(R.id.customer_email);
        phone = root.findViewById(R.id.customer_phone);
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

        bookingCar = root.findViewById(R.id.booking_car_name);
        bookingCar.setText(carList[0]);
        pickUpLocation = root.findViewById(R.id.pick_up_address);
        dropLocation = root.findViewById(R.id.drop_address);
        startDateBlock = root.findViewById(R.id.pick_up_date);
        endDateBlock = root.findViewById(R.id.destination_date);
        switchCompat = root.findViewById(R.id.same_location);
        linearLayout = root.findViewById(R.id.drop_linerlayout);

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) ->
                linearLayout.setVisibility(!isChecked ? View.VISIBLE : View.GONE));

        autoloadDate();

        double p1 = Integer.parseInt(carList[4].substring(1));
        double ehp = p1*0.08;
        double tp = p1*0.18;
        double bp = 500;
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

        startDateBlock.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                startDateBlock.setDate(String.valueOf(dayOfMonth), new SimpleDateFormat("EEEE", Locale.US).format(calendar.getTime()),
                        month, String.valueOf(year));
            }, sYear, sMonth, sDay).show();

            new TimePickerDialog(getContext(), (view, hourOfDay, minute) ->
                    startDateBlock.setTime(hourOfDay + ":"+minute), sHour,sMin,false).show();
        });

        endDateBlock.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                endDateBlock.setDate(String.valueOf(dayOfMonth), new SimpleDateFormat("EEEE", Locale.US).format(calendar.getTime()),
                        month, String.valueOf(year));
            }, dYear, dMonth, dDay).show();

            new TimePickerDialog(getContext(), (view, hourOfDay, minute) ->
                    startDateBlock.setTime(hourOfDay + ":"+minute), sHour,sMin,false).show();
        });


        paymentGrp.setOnCheckedChangeListener((group, checkedId) -> {
            Toast.makeText(getContext(),String.valueOf(checkedId),Toast.LENGTH_LONG).show();
            if(checkedId == R.id.pay_now){
                startActivity(new Intent(getActivity(), PaymentActivity.class)
                        .putExtra("Amount", totalAmount.getText().toString()));
            }
        });
        confirmButton.setOnClickListener(v -> {
            Bundle bundle = reFetchData(carList);
            if(SetValidation(v)){
                try {
                    StringBuilder msg = new StringBuilder();
                    for(int i=0; i<CustomerDetail.length; ++i)
                        msg.append(template[i]).append("\t").append(CustomerDetail[i]).append("\n");

                    new sendMail("Booking Confirm",
                             String.valueOf(msg),
                            "hrutviksuthar007@gmail.com",
                            this.getContext()).execute();

                    Navigation.findNavController(v)
                            .navigate(R.id.action_navigation_booking_to_navigation_checkout, bundle);

                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(v,"Booking Failed! Please Try Again",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void autoloadDate(){
        Date currDate = calendar.getTime();
        startDateBlock.setDate(new SimpleDateFormat("d",Locale.US).format(currDate)
                , new SimpleDateFormat("EEEE",Locale.US).format(currDate),
                Integer.parseInt(new SimpleDateFormat("MM", Locale.US).format(currDate)) -1
                , new SimpleDateFormat("yyyy",Locale.US).format(currDate));
        startDateBlock.setTime(new SimpleDateFormat("hh",Locale.US).format(currDate) + ":"+ new SimpleDateFormat("mm",Locale.US).format(currDate));

        Calendar t_cal = Calendar.getInstance();
        t_cal.add(Calendar.DATE,1); // tomorrow
        endDateBlock.setDate(new SimpleDateFormat("d",Locale.US).format(t_cal.getTime())
                , new SimpleDateFormat("EEEE",Locale.US).format(currDate),
                Integer.parseInt(new SimpleDateFormat("MM", Locale.US).format(currDate)) -1
                , new SimpleDateFormat("yyyy",Locale.US).format(currDate));
        endDateBlock.setTime(new SimpleDateFormat("hh",Locale.US).format(currDate) + ":"+ new SimpleDateFormat("mm",Locale.US).format(currDate));

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
        }
        cPickDate = startDateBlock.getDate();
        cPickTime = startDateBlock.getTime();
        cDropDate = endDateBlock.getDate();
        cDropTime = endDateBlock.getTime();
        String vehRegNo = carList[6];
        CustomerDetail = new String[]{
                cPickupL, cPickDate, cPickTime
                , cName, cAddress, cEmail, cPhone
                , totalAmount.getText().toString()
                , cDropDate, cDropL, vehRegNo};
        template = new String[]{
                "Pickup Loc:","Date:      ","Time:      "
                ,"Name:      ","Address:   ","Email:     ","Contact:   "
                ,"Amount:    ", "ReturnDate:", "Drop Loc:  ", "RC Number: "};

        Bundle bundle = new Bundle();
        bundle.putStringArray("BookedCar",carList);
        bundle.putStringArray("CustomerBook",CustomerDetail);
        return bundle;
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

        if (isNameValid && isPhoneValid && isPickupAddressValid) {
            Toast.makeText(v.getContext(), "Validation Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}

