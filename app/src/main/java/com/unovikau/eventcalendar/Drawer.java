package com.unovikau.eventcalendar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.unovikau.eventcalendar.data_model.Event;
import com.unovikau.eventcalendar.data_model.EventListAdapter;
import com.unovikau.eventcalendar.fragments.CalendarFragment;
import com.unovikau.eventcalendar.fragments.EventDetailsFragment;
import com.unovikau.eventcalendar.fragments.EventListFragment;
import com.unovikau.eventcalendar.fragments.GMapFragment;
import com.unovikau.eventcalendar.fragments.SearchFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    List<Event> eventList;

    Fragment eventListFragment;

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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("events");
        myRef.keepSynced(true);

        eventList = new ArrayList<>();


        // Read from the database
        /*myRef.addValueEventListener(new ValueEventListener() {*/
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Event post = postSnapshot.getValue(Event.class);
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
            List<Event> eventsInTwoWeeks = getEventsInTwoWeeks();

            String eventsJson = gson.toJson(eventsInTwoWeeks);
            Bundle bundleGmap = new Bundle();
            bundleGmap.putString("eventsInTwoWeeks", eventsJson);


            GMapFragment gMapFragment = new GMapFragment();
            gMapFragment.setArguments(bundleGmap);
            fragmentManager.beginTransaction().add(R.id.content_frame, gMapFragment).addToBackStack("google_map").commit();

            //fragmentManager.beginTransaction().replace(R.id.content_frame, new GMapFragment()).addToBackStack("google_map").commit();
        } else if (id == R.id.nav_search) {

            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, searchFragment).addToBackStack("search").commit();
            //fragmentManager.beginTransaction().replace(R.id.content_frame, new SearchFragment()).addToBackStack("search").commit();

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isDateInTwoWeeks(GregorianCalendar date){
        GregorianCalendar twoWeeksAfter = new GregorianCalendar();
        twoWeeksAfter.add(Calendar.DAY_OF_MONTH, 14);
        return date.after(new GregorianCalendar()) && date.before(twoWeeksAfter);
    }

    private List<Event> getEventsInTwoWeeks(){
        List<Event> resultEvents = new ArrayList<>(0);
        for (Event x: eventList) {
            if(x.getDateEnd() != null){
                if(isDateInTwoWeeks(x.getDate()) || isDateInTwoWeeks(x.getDateEnd()))
                    resultEvents.add(x);
            }
            else{
                if(isDateInTwoWeeks(x.getDate()))
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
