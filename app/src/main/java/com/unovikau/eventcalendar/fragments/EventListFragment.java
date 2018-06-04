package com.unovikau.eventcalendar.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unovikau.eventcalendar.dialogs.MonthYearPickerDialog;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.dialogs.TypePickerDialog;
import com.unovikau.eventcalendar.models.CityEvent;
import com.unovikau.eventcalendar.adapters.EventListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EventListFragment extends Fragment {

    String[] months;
    List<CityEvent> eventList;
    private static EventListAdapter adapter;
    int year;
    int month;
    String selectedDate;
    int selectedType;

    ListView listView;
    FloatingActionButton filterButton;
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
        filterButton = getView().findViewById(R.id.filter_button);

        months = getResources().getStringArray(R.array.months_ru);


        if(this.selectedDate == null){
            current_month_tv.setText(months[month] + " " + year);
        }
        else {
            current_month_tv.setText(this.selectedDate);
        }

        String jsonEvents = getArguments().getString("events");
        Gson gson = new Gson();
        this.eventList = gson.fromJson(jsonEvents, new TypeToken<List<CityEvent>>(){}.getType());

        this.eventList = sortEventList(this.eventList);

        adapter = new EventListAdapter(eventList,getActivity());
        if(selectedDate == null)
            adapter.getFilter().filter(String.valueOf(month + 1) + "." + String.valueOf(year));
        else
            adapter.getDateFilter().filter(selectedDate);



        listView = getView().findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CityEvent dataModel = (CityEvent) listView.getAdapter().getItem(position);
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

        View.OnClickListener oclTypeFilter = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypePickerDialog pd = new TypePickerDialog();
                pd.setSelectedType(selectedType);
                pd.setListener(new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface view, int type) {
                        selectedType = type;
                        adapter.removeFilter();
                        if(selectedDate == null)
                            adapter.getFilter().filter(String.valueOf(month + 1) + "." + String.valueOf(year) + ";;;" + selectedType);
                        else
                            adapter.getDateFilter().filter(selectedDate+ ";;;" + selectedType);
                    }
                });
                pd.show(getFragmentManager(), "MonthYearPickerDialog");
            }
        };

        filterButton.setOnClickListener(oclTypeFilter);



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
                        selectedDate = null;
                        year = selected_year;
                        month = selected_month;
                        current_month_tv.setText(months[selected_month] + " " + selected_year);
                        adapter.removeFilter();
                        adapter.getFilter().filter(String.valueOf(month + 1) + "." + String.valueOf(year) + ";;;" + selectedType);

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

    private List<CityEvent> sortEventList(List<CityEvent> eventList){

        List<CityEvent> pastEvents = new ArrayList<CityEvent>();
        List<CityEvent> activeEvents = new ArrayList<CityEvent>();

        for (CityEvent event: eventList) {
            if(event.isPastEvent())
                pastEvents.add(event);
            else{
                activeEvents.add(event);
            }
        }

        Collections.sort(pastEvents, new DateComparator());
        Collections.sort(activeEvents, new DateComparator());

        activeEvents.addAll(pastEvents);
        return  activeEvents;
    }

    class DateComparator implements Comparator<CityEvent> {


        @Override
        public int compare(CityEvent o1, CityEvent o2) {
            Date firstDate = o1.getDate();
            Date secondDate = o2.getDate();

            if(firstDate.equals(secondDate))
                return 0;
            if(firstDate.before(secondDate))
                return -1;

            return 1;

        }
    }
}
