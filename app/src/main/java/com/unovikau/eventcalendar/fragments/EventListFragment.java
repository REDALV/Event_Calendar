package com.unovikau.eventcalendar.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.unovikau.eventcalendar.MonthYearPickerDialog;
import com.unovikau.eventcalendar.R;

import java.util.Calendar;

public class EventListFragment extends Fragment {

    String[] months;

    TextView current_month_tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        current_month_tv = (TextView) getView().findViewById(R.id.current_month);

        // Set current month and year
        months = getResources().getStringArray(R.array.months_ru);
        Calendar cal = Calendar.getInstance();
        int current_year = cal.get(Calendar.YEAR);
        int current_month = cal.get(Calendar.MONTH);
        current_month_tv.setText(months[current_month] + " " + current_year);


        // Selection month listener
        View.OnClickListener oclDate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pd = new MonthYearPickerDialog();

                pd.setListener(new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        current_month_tv.setText(months[monthOfYear - 1] + " " + year);
                    }
                });
                pd.show(getFragmentManager(), "MonthYearPickerDialog");

            }
        };
        current_month_tv.setOnClickListener(oclDate);

    }
}
