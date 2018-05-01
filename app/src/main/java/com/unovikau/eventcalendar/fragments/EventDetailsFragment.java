package com.unovikau.eventcalendar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.data_model.Event;

public class EventDetailsFragment extends Fragment {

    TextView eventName;
    TextView eventDate;
    TextView eventArticle;

    private Event event;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventName = (TextView) getView().findViewById(R.id.event_name);
        eventDate = (TextView) getView().findViewById(R.id.event_date);
        eventArticle = (TextView) getView().findViewById(R.id.event_article);

        if(getArguments() != null){
            String jsonEvent = getArguments().getString("event");
            Gson gson = new Gson();
            this.event = gson.fromJson(jsonEvent, Event.class);

            this.eventName.setText(this.event.getName());
            this.eventDate.setText(this.event.getDateString());
            this.eventArticle.setText(this.event.getArticle());
        }

    }
}
