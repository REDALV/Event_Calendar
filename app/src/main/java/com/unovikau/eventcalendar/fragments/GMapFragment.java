package com.unovikau.eventcalendar.fragments;

import android.Manifest;
import android.app.Fragment;

import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.models.Event;

import java.util.List;

public class GMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mGoogleMap;
    private Event event;
    private List<Event> eventsInTwoWeeks;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null){
            if(getArguments().getString("event") != null){
                String jsonEvent = getArguments().getString("event");
                Gson gson = new Gson();
                this.event = gson.fromJson(jsonEvent, Event.class);
            }

            Gson gson = new Gson();
            String jsonEvents = getArguments().getString("eventsInTwoWeeks");
            this.eventsInTwoWeeks = gson.fromJson(jsonEvents, new TypeToken<List<Event>>(){}.getType());
        }

        return inflater.inflate(R.layout.fragment_gmap, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnInfoWindowClickListener(this);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            if(this.event != null){
                LatLng latLng = new LatLng(this.event.getLat(), this.event.getLng());
                googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title(this.event.getName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                // Zoom in, animating the camera.
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
            else{
                for (Event x: eventsInTwoWeeks) {
                    LatLng latLng = new LatLng(x.getLat(), x.getLng());
                    if(x.getDateEnd() != null){
                        googleMap.addMarker(new MarkerOptions().position(latLng)
                                .title(x.getName()).snippet(getString(R.string.event_details_date_interval, x.getDateString(), x.getDateEndString()))).setTag(x);
                    }
                    else{
                        googleMap.addMarker(new MarkerOptions().position(latLng)
                                .title(x.getName()).snippet(getString(R.string.event_details_date, x.getDateString()))).setTag(x);
                    }


                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.424195, 31.014671),10));
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Event dataModel = (Event) marker.getTag();
        Gson gson = new Gson();
        String json = gson.toJson(dataModel);

        FragmentManager fragmentManager = getFragmentManager();

        EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("event", json);
        eventDetailsFragment.setArguments(bundle);

        fragmentManager.beginTransaction().add(R.id.content_frame, eventDetailsFragment).addToBackStack("event_details").commit();
    }
}
