package com.unovikau.eventcalendar.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.models.CityEvent;
import com.unovikau.eventcalendar.fragments.CalendarFragment;
import com.unovikau.eventcalendar.fragments.EventListFragment;
import com.unovikau.eventcalendar.fragments.GMapFragment;
import com.unovikau.eventcalendar.fragments.SearchFragment;
import com.unovikau.eventcalendar.services.ReminderJobService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    List<CityEvent> eventList;

    Fragment eventListFragment;
    private static final String TAG = "Drawer";
    private static final int MILLISECONDS_IN_TWO_WEEKS = 1209600000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            eventListFragment = getFragmentManager().getFragment(savedInstanceState, "event_list");

        }

        getDataFromFireBase();
        scheduleJob();

        // Запрос разрешения для использования карт
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }

    private void scheduleJob(){
        SharedPreferences preferences = PreferenceManager.
                getDefaultSharedPreferences(this);

        if(!preferences.getBoolean("firstRunComplete", false)){
            //schedule the job only once.
            scheduleReminderJob();

            //update shared preference
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstRunComplete", true);
            editor.apply();
        }

    }

    private void scheduleReminderJob(){
        JobScheduler jobScheduler = (JobScheduler)getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(this,
                ReminderJobService.class);

        JobInfo jobInfo = new JobInfo.Builder(3, componentName)
                .setPeriodic(36000000)
                .setPersisted(true)
                .build();

        jobScheduler.schedule(jobInfo);
    }

    private void getDataFromFireBase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("events");
        myRef.keepSynced(true);

        eventList = new ArrayList<>();

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    CityEvent post = postSnapshot.getValue(CityEvent.class);
                    post.setId(postSnapshot.getKey());
                    eventList.add(post);
                }

                Gson gson = new Gson();
                String json = gson.toJson(eventList);

                if(eventListFragment == null){
                    eventListFragment = new EventListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("events", json);
                    eventListFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, eventListFragment).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        Gson gson = new Gson();
        String json = gson.toJson(eventList);
        Bundle bundle = new Bundle();
        bundle.putString("events", json);

        if (id == R.id.nav_events_list) {

            if(eventListFragment == null){
                eventListFragment = new EventListFragment();
                eventListFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.content_frame, eventListFragment).addToBackStack("event_list").commit();
            }
            else
                fragmentManager.beginTransaction().replace(R.id.content_frame, eventListFragment).commit();


            //fragmentManager.beginTransaction().replace(R.id.content_frame, new EventListFragment()).addToBackStack("event_list").commit();

        } else if (id == R.id.nav_calendar) {

            CalendarFragment calendarFragment = new CalendarFragment();
            calendarFragment.setArguments(bundle);
            fragmentManager.beginTransaction().add(R.id.content_frame, calendarFragment).addToBackStack("calendar").commit();

            //fragmentManager.beginTransaction().replace(R.id.content_frame, new CalendarFragment()).addToBackStack("calendar").commit();
        } else if (id == R.id.nav_map) {
            List<CityEvent> eventsInTwoWeeks = getEventsInTwoWeeks();

            String eventsJson = gson.toJson(eventsInTwoWeeks);
            Bundle bundleGMap = new Bundle();
            bundleGMap.putString("eventsInTwoWeeks", eventsJson);


            GMapFragment gMapFragment = new GMapFragment();
            gMapFragment.setArguments(bundleGMap);
            fragmentManager.beginTransaction().add(R.id.content_frame, gMapFragment).addToBackStack("google_map").commit();

            //fragmentManager.beginTransaction().replace(R.id.content_frame, new GMapFragment()).addToBackStack("google_map").commit();
        } else if (id == R.id.nav_search) {

            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(bundle);
            fragmentManager.beginTransaction().add(R.id.content_frame, searchFragment).addToBackStack("search").commit();
            //fragmentManager.beginTransaction().replace(R.id.content_frame, new SearchFragment()).addToBackStack("search").commit();

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static boolean isDateInTwoWeeks(String date){
        boolean result = false;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date eventDate = sdf.parse(date);
            Date today = new Date();
            Date twoWeeksAfter = new Date(today.getTime() + MILLISECONDS_IN_TWO_WEEKS);

            if(!(eventDate.before(today) || eventDate.after(twoWeeksAfter)))
                result = true;

        }
        catch (ParseException ex){
            Log.i(TAG, ex.getMessage());
        }

        return result;
    }

    private List<CityEvent> getEventsInTwoWeeks(){
        List<CityEvent> resultEvents = new ArrayList<>(0);
        for (CityEvent x: eventList) {
            if(x.getDateEnd() != null){
                if(isDateInTwoWeeks(x.getDateString()) || isDateInTwoWeeks(x.getDateEndString()))
                    resultEvents.add(x);
            }
            else{
                if(isDateInTwoWeeks(x.getDateString()))
                    resultEvents.add(x);
            }
        }
        return resultEvents;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getFragmentManager().putFragment(outState, "event_list", eventListFragment);
    }

    @Override
    public void onRestoreInstanceState(Bundle inState){
        super.onRestoreInstanceState(inState);
        eventListFragment = getFragmentManager().getFragment(inState,"event_list");
    }



}
