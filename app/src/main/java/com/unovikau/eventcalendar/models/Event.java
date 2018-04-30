package com.unovikau.eventcalendar.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event {

    public String name;
    public String article;
    public String address;
    public String date;
    public String is_important;
    public String lat;
    public String lng;
    public String type;



    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Event(String name, String article, String address, String date, String is_important, String lat, String lng, String type) {
        this.name = name;
        this.article = article;
        this.address = address;
        this.date = date;
        this.is_important = is_important;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
    }

    @Override
    public String toString(){
       return name;
    }

}
