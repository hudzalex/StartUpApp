package com.biosens.biosens;

import android.*;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;


public class PlaceFragment extends Fragment {
    Cursor cursor;
    private SQLiteDatabase db;
    SessionManagement session;
    SQLiteOpenHelper biosensDatabaseHelper;
    private  int PrevImageId, inp;
      private LocationManager locationManager;
    private double lat,lon;
    private String  user_id;
    EditText nameEditText;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_home, container, false);





        biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
        db = biosensDatabaseHelper.getWritableDatabase();
        session = new SessionManagement(inflater.getContext());

        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManagement.KEY_ID);

        nameEditText = (EditText) layout.findViewById(R.id.editTextName);



        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Enable GPS", Toast.LENGTH_SHORT);
            toast.show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }

        if (ContextCompat.checkSelfPermission(inflater.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
        }

        // Inflate the layout for this fragment

        return layout;
    }



    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };
    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            lat=location.getLatitude();
            lon=location.getLongitude();

        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            lat=location.getLatitude();
            lon=location.getLongitude();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void onClickInsertPlace() {




        try {

            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("place",
                    new String[]{"name"},
                    "user_id= ?",
                    new String[] {String.valueOf(user_id)},
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }


        inp=cursor.getCount();

        if(inp%2==0){
            PrevImageId=R.drawable.field_1;
        }else{
            PrevImageId=R.drawable.field_2;
        }
        cursor.close();




        BioSensDatabaseHelper.insertPlace(db, nameEditText.getText().toString(), lon, lat,PrevImageId,user_id );


    }

}
