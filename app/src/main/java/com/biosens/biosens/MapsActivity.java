package com.biosens.biosens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SQLiteDatabase db;
    SQLiteOpenHelper biosensDatabaseHelper;
    Cursor cursor;
    SessionManagement session;
    Location location;
    double latitude;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng myPosition;
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        myPosition = new LatLng(latitude, longitude);
        // Add a marker in Sydney and move the camera
        LatLng sydneyGood = new LatLng(-44, 151);
        LatLng sydneyBad = new LatLng(latitude+1,longitude);
        session = new SessionManagement(this);
        HashMap<String, String> user = session.getUserDetails();


        String user_id = user.get(SessionManagement.KEY_ID);
        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(this);
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("Test",
                    new String[]{"Longitude", "Latitude","Result","Date","Field"},
                    "USER_UUID= ?",
                    new String[] {user_id},
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                double longlat = cursor.getDouble(0);
                double lat = cursor.getDouble(1);
                LatLng rezPositions = new LatLng(lat,longlat);
                if(cursor.getInt(3)==0){
                    mMap.addMarker(new MarkerOptions().position(rezPositions ).title("Toxin are not found | Date "+cursor.getString(3)+" | Field "+cursor.getString(4)).icon(
                        BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));}
                else{
                    mMap.addMarker(new MarkerOptions().position(rezPositions ).title("Toxin are found | Date"+cursor.getString(3)+" | Field "+cursor.getString(4)).icon(
                            BitmapDescriptorFactory
                                    .defaultMarker()));
                }

            } while (cursor.moveToNext());
        }
      /*  mMap.addMarker(new MarkerOptions().position(myPosition).title("Marker in good").icon(
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addMarker(new MarkerOptions().position(sydneyBad).title("Marker in bad").icon(
                BitmapDescriptorFactory.defaultMarker()).flat(false));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        cursor.close();
    }

    private void init() {
    }

    public void onBackPressed(View v) {
        super.onBackPressed();
    }


}
