package com.unovikau.eventcalendar.fragments;

import android.Manifest;
import android.app.Fragment;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.unovikau.eventcalendar.R;
import com.unovikau.eventcalendar.data_model.Event;

public class GMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private Event event;

    private FusedLocationProviderClient mFusedLocationClient;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null){
            String jsonEvent = getArguments().getString("event");
            Gson gson = new Gson();
            this.event = gson.fromJson(jsonEvent, Event.class);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

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
                //LatLng latLng = new LatLng(this.event.getLat(), this.event.getLng());
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                // Zoom in, animating the camera.

                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        }
    }
}
