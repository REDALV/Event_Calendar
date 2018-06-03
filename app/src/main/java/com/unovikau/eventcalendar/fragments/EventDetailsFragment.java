package com.unovikau.eventcalendar.fragments;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.models.Event;
import com.unovikau.eventcalendar.adapters.EventPhotosAdapter;
import com.unovikau.eventcalendar.models.EventReminder;
import com.unovikau.eventcalendar.room.RemindersDB;

public class EventDetailsFragment extends Fragment {

    TextView eventName;
    TextView eventDate;
    TextView eventTime;
    TextView eventPlace;
    TextView eventArticle;
    ViewPager eventPhotos;
    Button reminderButton;

    private Event event;

    private RemindersDB remindersDB;
    int remindersCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        remindersDB = Room.databaseBuilder(getActivity(),RemindersDB.class, "reminders db").allowMainThreadQueries().build();
        return inflater.inflate(R.layout.fragment_event_details, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventName = (TextView) getView().findViewById(R.id.event_name);
        eventDate = (TextView) getView().findViewById(R.id.event_date);
        eventTime = (TextView) getView().findViewById(R.id.event_time);
        eventPlace = (TextView) getView().findViewById(R.id.event_place);
        eventArticle = (TextView) getView().findViewById(R.id.event_article);
        eventPhotos = (ViewPager) getView().findViewById(R.id.event_photos);
        reminderButton = (Button) getView().findViewById(R.id.reminder_button);

        if(getArguments() != null){
            String jsonEvent = getArguments().getString("event");
            Gson gson = new Gson();
            this.event = gson.fromJson(jsonEvent, Event.class);

            this.eventName.setText(this.event.getName());
            if(this.event.getDateEnd() == null)
                this.eventDate.setText(getString(R.string.event_details_date, this.event.getDateString()));
            else
                this.eventDate.setText(getString(R.string.event_details_date_interval, this.event.getDateString(), this.event.getDateEndString()));
            if(this.event.getTimeEnd().equals(""))
                this.eventTime.setText(getString(R.string.event_details_time, this.event.getTime()));
            else
                this.eventTime.setText(getString(R.string.event_details_time_interval, this.event.getTime(), this.event.getTimeEnd()));
            this.eventPlace.setText(getString(R.string.event_details_place, this.event.getAddress()));
            this.eventArticle.setText(this.event.getArticle());

            //EventPhotosAdapter eventPhotosAdapter = new EventPhotosAdapter(getActivity(), this.event.getImages());

            this.remindersCount = remindersDB.RemindersDb().getRemindersByEventId(event.getId()).size();
            changeButtonColor();

            if(this.event.getImages() != null){
                EventPhotosAdapter eventPhotosAdapter = new EventPhotosAdapter(getActivity(), this.event.getImages());
                this.eventPhotos.setAdapter(eventPhotosAdapter);
                this.eventPhotos.setOffscreenPageLimit(5);
            }
            else{
                this.eventPhotos.setVisibility(View.GONE);
            }

            this.reminderButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(remindersCount > 0){
                        remindersDB.RemindersDb().deleteRemindersByEventId(event.getId());
                        remindersCount = 0;
                    }
                    else{
                        remindersDB.RemindersDb().insertReminder(new EventReminder(event.getId(),event.getName(), event.getDateString()));
                        Toast.makeText(getActivity(),
                                "В день начала события вам придет напоминание", Toast.LENGTH_SHORT).show();
                        remindersCount = 1;
                    }

                    changeButtonColor();
                }
            });
        }
    }

    private void changeButtonColor(){
        if(this.remindersCount > 0){
            reminderButton.setBackgroundColor(Color.GREEN);
            reminderButton.setText("Не напоминать");
        }
        else{
            reminderButton.setBackgroundColor(Color.YELLOW);
            reminderButton.setText("Напомнить");
        }
    }


}
