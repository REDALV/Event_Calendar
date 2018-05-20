package com.unovikau.eventcalendar.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class CalendarFragment  extends Fragment {

    CalendarView calendarView;
    Button selectDateButton;
    GregorianCalendar selectedDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        selectedDate = new GregorianCalendar();
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.calendarView = (CalendarView) getView().findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = new GregorianCalendar(year,month,dayOfMonth);
            }
        });

        this.selectDateButton = (Button) getView().findViewById(R.id.select_date_button);

        this.selectDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();

                EventListFragment eventListFragment = new EventListFragment();

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                sdf.setCalendar(selectedDate);
                String selected_date = sdf.format(selectedDate.getTime());

                Bundle bundle = new Bundle();
                bundle.putString("selected_date", selected_date);
                bundle.putString("events", getArguments().getString("events"));
                eventListFragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.content_frame, eventListFragment).addToBackStack("event_list").commit();
            }
        });
    }
}
