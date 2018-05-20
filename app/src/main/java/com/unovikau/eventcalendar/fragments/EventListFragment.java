package com.unovikau.eventcalendar.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unovikau.eventcalendar.MonthYearPickerDialog;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.data_model.Event;
import com.unovikau.eventcalendar.data_model.EventListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class EventListFragment extends Fragment {

    String[] months;

    List<Event> eventList;
    ListView listView;
    private static EventListAdapter adapter;

    int year;
    int month;
    String selectedDate;

    TextView current_month_tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString("selected_date") != null){
            this.selectedDate = getArguments().getString("selected_date");
        }
        else
            this.selectedDate = null;

        Calendar cal = Calendar.getInstance();
        if (savedInstanceState == null) {

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
        } else {
            year= savedInstanceState.getInt("year", cal.get(Calendar.YEAR));
            month= savedInstanceState.getInt("month", cal.get(Calendar.MONTH));
        }
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        current_month_tv = (TextView) getView().findViewById(R.id.current_month);

        months = getResources().getStringArray(R.array.months_ru);


        if(this.selectedDate == null){
            /*
            // Set current month and year
            months = getResources().getStringArray(R.array.months_ru);
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);*/
            current_month_tv.setText(months[month] + " " + year);
        }
        else {
            current_month_tv.setText(this.selectedDate);
        }

        String jsonEvents = getArguments().getString("events");
        Gson gson = new Gson();
        this.eventList = gson.fromJson(jsonEvents, new TypeToken<List<Event>>(){}.getType());

        adapter = new EventListAdapter(eventList,getActivity());

        if(selectedDate == null)
            adapter.getFilter().filter(String.valueOf(month + 1) + "." + String.valueOf(year));
        else
            adapter.getFilter().filter(selectedDate);

        listView = getView().findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event dataModel = (Event) listView.getAdapter().getItem(position);
                Gson gson = new Gson();
                String json = gson.toJson(dataModel);

                FragmentManager fragmentManager = getFragmentManager();

                EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("event", json);
                eventDetailsFragment.setArguments(bundle);

                fragmentManager.beginTransaction().add(R.id.content_frame, eventDetailsFragment).addToBackStack("event_details").commit();
            }
        });

        // Selection month listener
        View.OnClickListener oclDate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setMonth(month, year);
                pd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selected_year, int selected_month,
                                          int dayOfMonth) {
                        year = selected_year;
                        month = selected_month;
                        current_month_tv.setText(months[selected_month] + " " + selected_year);
                        adapter.getFilter().filter(String.valueOf(month + 1) + "." + String.valueOf(year));
                    }
                });
                pd.show(getFragmentManager(), "MonthYearPickerDialog");
            }
        };
        current_month_tv.setOnClickListener(oclDate);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("month", month);
        outState.putInt("year", year);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            month = savedInstanceState.getInt("month");
            year = savedInstanceState.getInt("year");
        }

    }
}
