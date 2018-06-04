package com.unovikau.eventcalendar.models;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class EventReminder {
    @PrimaryKey(autoGenerate = true)
    private int reminderId;
    private String eventId;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventAddress;

    public EventReminder(String eventId, String eventName, String eventDate, String eventTime, String eventAddress) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventAddress = eventAddress;
    }

    public int getReminderId() {
        return reminderId;
    }
    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }
    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public String getEventDate() {
        return eventDate;
    }
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public String getEventAddress() { return eventAddress; }
}
