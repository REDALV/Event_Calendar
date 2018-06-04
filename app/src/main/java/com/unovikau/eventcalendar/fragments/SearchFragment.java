package com.unovikau.eventcalendar.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.models.CityEvent;
import com.unovikau.eventcalendar.adapters.EventListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<CityEvent> eventList;
    private ListView listView;
    private EventListAdapter adapter;
    EditText inputSearch;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String jsonEvents = getArguments().getString("events");
        Gson gson = new Gson();
        this.eventList = gson.fromJson(jsonEvents, new TypeToken<List<CityEvent>>(){}.getType());

        adapter = new EventListAdapter(eventList,getActivity());

        listView = getView().findViewById(R.id.search_list_view);
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

                fragmentManager.beginTransaction().replace(R.id.content_frame, eventDetailsFragment).addToBackStack("event_details").commit();
            }
        });

        eventList = new ArrayList<>();
        inputSearch = getActivity().findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getNameFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

}
