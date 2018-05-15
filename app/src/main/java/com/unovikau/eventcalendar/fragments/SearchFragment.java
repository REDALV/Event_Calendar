package com.unovikau.eventcalendar.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.data_model.Event;
import com.unovikau.eventcalendar.data_model.EventListAdapter;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SearchFragment extends Fragment {

    private ArrayList<Event> eventList;
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("events");
        myRef.keepSynced(true);

        eventList = new ArrayList<>();
        inputSearch = getActivity().findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getStringFilter().filter(cs);
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


        // Read from the database
        /*myRef.addValueEventListener(new ValueEventListener() {*/
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Event post = postSnapshot.getValue(Event.class);
                    eventList.add(post);
                    Log.d("Get Data", post.toString());
                }

                adapter = new EventListAdapter(eventList,getActivity());

                /*
                if(selectedDate == null)
                    adapter.getFilter().filter(String.valueOf(month + 1) + "." + String.valueOf(year));
                else
                    adapter.getFilter().filter(selectedDate);*/

                listView = getView().findViewById(R.id.search_list_view);
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

                        fragmentManager.beginTransaction().replace(R.id.content_frame, eventDetailsFragment).addToBackStack("event_details").commit();
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

}
