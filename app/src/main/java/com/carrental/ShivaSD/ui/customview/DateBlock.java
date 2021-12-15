package com.carrental.ShivaSD.ui.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.carrental.ShivaSD.R;

public class DateBlock extends LinearLayout {

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private TextView dayText;
    private TextView yearText;
    private TextView timeText;
    String strDate,strMonth,strYear;


    public DateBlock(Context context) {
        super(context);
        initializeViewComponents();
    }

    public DateBlock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViewComponents();
    }

    public DateBlock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViewComponents();
    }

    private void initializeViewComponents(){
        View view = inflate(getContext(), R.layout.box_date, this);
        dayText = view.findViewById(R.id.day_in_text);
        yearText = view.findViewById(R.id.month_in_text);
        timeText = view.findViewById(R.id.time_in_text);
    }

    public void setTextContent(TextView anyText, String textContent){
        anyText.setText(textContent);
    }

    public void setDate(String date, String weekday, int month, String year){
        dayText.setText(String.format("%s %s", date, MONTHS[month]));
        yearText.setText(String.format("%s, %s",weekday,year));
        strMonth = MONTHS[month];
        strDate = date;
        strYear = year;
    }
    public void setTime(String time){
        timeText.setText(String.format("Time: %s",time));
    }

    public String getDate(){
        return String.format("%s %s, %s",strMonth,strDate,strYear);
    }
    public String getTime(){
        return timeText.getText().toString();
    }
}

