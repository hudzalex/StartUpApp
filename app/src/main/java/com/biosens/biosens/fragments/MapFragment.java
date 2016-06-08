package com.biosens.biosens.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biosens.biosens.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    static final LatLng HAMBURG = new LatLng(0, 0);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;
    SupportMapFragment mapFragment;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(MapFragment.this);


        return v;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(MapFragment.this);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(MapFragment.this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.addMarker(new MarkerOptions().position(HAMBURG)
                .title("Hamburg"));
        map.addMarker(new MarkerOptions()
                .position(KIEL)
                .title("Kiel")
                .snippet("Kiel is cool")
                .icon(BitmapDescriptorFactory
                        .defaultMarker()));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        //...
    }


}
