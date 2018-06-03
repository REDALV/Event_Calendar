package com.unovikau.eventcalendar.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.unovikau.eventcalendar.models.EventReminder;

@Database(entities = {EventReminder.class}, version = 1)
public abstract class RemindersDB extends RoomDatabase {
    public abstract RemindersDAO RemindersDb();
}
