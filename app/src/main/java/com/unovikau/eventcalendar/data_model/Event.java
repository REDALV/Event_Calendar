package com.unovikau.eventcalendar.data_model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

@IgnoreExtraProperties
public class Event {

    private String name;
    private String article;
    private String address;
    private GregorianCalendar date;
    private Boolean is_important;
    private Double lat;
    private Double lng;
    private Long type;
    private List<String> images;




    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Event(String name, String article, String address, String date, Boolean is_important, Double lat, Double lng, Long type, List<String> images) {
        String[] array = date.split(".");

        this.date = new GregorianCalendar(Integer.parseInt(array[2]), Integer.parseInt(array[1])-1, Integer.parseInt(array[0]));

        this.name = name;
        this.article = article;
        this.address = address;
        this.is_important = is_important;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.images = images;
    }

    @Override
    public String toString(){
       return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(String date) {
        String[] array = date.split("\\.");
        this.date = new GregorianCalendar(Integer.parseInt(array[2]), Integer.parseInt(array[1])-1, Integer.parseInt(array[0]));
    }

    public Boolean getIs_important() {
        return is_important;
    }

    public void setIs_important(Boolean is_important) {
        this.is_important = is_important;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDateString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        simpleDateFormat.setCalendar(this.date);
        return simpleDateFormat.format(this.date.getTime());
    }
}
