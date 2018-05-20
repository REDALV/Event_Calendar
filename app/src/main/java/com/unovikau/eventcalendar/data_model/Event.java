package com.unovikau.eventcalendar.data_model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

@IgnoreExtraProperties
public class Event {

    private String name;
    private String article;
    private String address;
    private String time;
    private String timeEnd;
    private GregorianCalendar date;
    private GregorianCalendar dateEnd;
    private Boolean isImportant;
    private Double lat;
    private Double lng;
    private Long type;
    private Long subtype;
    private List<String> images;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Event(String name, String article, String address, String time, String timeEnd, String date, String dateEnd,
                 Boolean isImportant, Double lat, Double lng, Long type, Long subtype, List<String> images) {
        String[] array = date.split(".");
        this.date = new GregorianCalendar(Integer.parseInt(array[2]), Integer.parseInt(array[1])-1, Integer.parseInt(array[0]));

        array = dateEnd.split(".");
        if (array.length == 3)
            this.dateEnd = new GregorianCalendar(Integer.parseInt(array[2]), Integer.parseInt(array[1])-1, Integer.parseInt(array[0]));
        else
            this.dateEnd = null;

        this.name = name;
        this.article = article;
        this.address = address;
        this.isImportant = isImportant;
        this.time = time;
        this.timeEnd = timeEnd;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.subtype = subtype;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(String date) {
        String[] array = date.split("\\.");
        this.date = new GregorianCalendar(Integer.parseInt(array[2]), Integer.parseInt(array[1])-1, Integer.parseInt(array[0]));
    }

    public GregorianCalendar getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        String[] array = dateEnd.split("\\.");
        if(array.length == 3)
            this.dateEnd = new GregorianCalendar(Integer.parseInt(array[2]), Integer.parseInt(array[1])-1, Integer.parseInt(array[0]));
        else
            this.dateEnd = null;
    }

    public Long getSubtype() {
        return subtype;
    }

    public void setSubtype(Long subtype) {
        this.subtype = subtype;
    }

    public Boolean getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(Boolean isImportant) {
        this.isImportant = isImportant;
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

    public String getDateEndString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        simpleDateFormat.setCalendar(this.dateEnd);
        return simpleDateFormat.format(this.dateEnd.getTime());
    }
}
