package com.unovikau.eventcalendar.room;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.unovikau.eventcalendar.models.EventReminder;

import java.util.List;

@Dao
public interface RemindersDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertReminders(List<EventReminder> reminders);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertReminder(EventReminder reminder);

    @Query("DELETE FROM eventReminder WHERE eventDate = :date")
    public void deleteRemindersByDate(String date);

    @Query("DELETE FROM eventReminder WHERE eventId = :eventId")
    public void deleteRemindersByEventId(String eventId);

    @Query("SELECT * FROM eventReminder WHERE eventDate = :date")
    public List<EventReminder> getRemindersOnDate(String date);

    @Query("SELECT * FROM eventReminder WHERE eventId = :eventId")
    public List<EventReminder> getRemindersByEventId(String eventId);

    @Query("SELECT * FROM eventReminder")
    public List<EventReminder> getReminders();
}

