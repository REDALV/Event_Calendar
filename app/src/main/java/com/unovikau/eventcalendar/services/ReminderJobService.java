package com.unovikau.eventcalendar.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.arch.persistence.room.Room;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.unovikau.eventcalendar.models.EventReminder;
import com.unovikau.eventcalendar.room.RemindersDB;

import com.unovikau.eventcalendar.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ReminderJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        RemindersDB remindersDB = Room.databaseBuilder(this,RemindersDB.class, "reminders db").allowMainThreadQueries().build();
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        List<EventReminder> reminderList = remindersDB.RemindersDb().getRemindersOnDate(sdf.format(today));

        if(reminderList.size()>0){
            for (EventReminder reminder: reminderList) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.mipmap.ic_launcher))
                        .setSmallIcon(R.drawable.ic_events_list)
                        .setContentTitle(reminder.getEventName())
                        .setContentText(reminder.getEventDate())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(0, mBuilder.build());
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
