package com.biosens.biosens;

import android.*;
import android.app.ListFragment;
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
import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;


public class ResFragment extends ListFragment {
    SQLiteDatabase db;
    SQLiteOpenHelper biosensDatabaseHelper;
    Cursor cursor;
    SessionManagement session;
    private LocationManager locationManager;
    private double lat,lon;
    final Random random = new Random();
    double ResStartValue1=0,ResStartValue2=0,ResStartValue3=0,ResStartValue4=0,ResStartValue5=0,ResStartValue6=0 ;
    String ResearchId="" ;
    boolean toxin1=false;
    boolean toxin2=false;
    boolean toxin3=false;
    boolean toxin4=false;
    boolean toxin5=false;
    boolean toxin6=false;
    double toxin1b=5.0;
    double toxin2b=8.0;
    double toxin3b=2.0;
    double toxin4b=4.0;
    double toxin5b=4.0;
    double toxin6b=1.0;
    double rez1,rez2,rez3,rez4,rez5,rez6;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_res, container, false);

        session = new SessionManagement(inflater.getContext());
        HashMap<String, String> user = session.getUserDetails();


        String user_id = user.get(SessionManagement.KEY_ID);


        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Enable GPS", Toast.LENGTH_SHORT);
            toast.show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }

        if (ContextCompat.checkSelfPermission(inflater.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);

        boolean haveToxin=false;
        String ListText="";
        int ImageId=0;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
             ResStartValue1 = bundle.getDouble("ResStartValue1", 0);
            ResStartValue2 = bundle.getDouble("ResStartValue2", 0);
            ResStartValue3 = bundle.getDouble("ResStartValue3", 0);
            ResStartValue4 = bundle.getDouble("ResStartValue4", 0);
            ResStartValue5 = bundle.getDouble("ResStartValue5", 0);
            ResStartValue6 = bundle.getDouble("ResStartValue6", 0);
           ResearchId = bundle.getString("ResearchId", "");
             toxin1=bundle.getBoolean("Toxin1");
           toxin2=bundle.getBoolean("Toxin2");
          toxin3=bundle.getBoolean("Toxin3");
            toxin4=bundle.getBoolean("Toxin4");
           toxin5=bundle.getBoolean("Toxin5");
           toxin6=bundle.getBoolean("Toxin6");

        }

        new ReadMeasurementBluetoothTask(new ReadMeasurementBluetoothTask.ReadMeasurementBluetoothCallback(){
            @Override
            public void onMeasurement(double[] result) {
                rez1 = result[0];
                rez2 = result[1];
                rez3 = result[2];
                rez4 = result[3];
                rez5 = result[4];
                rez6 = result[5];
            }
        }).execute();
        double rezult1=rez1-ResStartValue1;
        double rezult2=rez2-ResStartValue2;
        double rezult3=rez3-ResStartValue3;
        double rezult4=rez4-ResStartValue4;
        double rezult5=rez5-ResStartValue5;
        double rezult6=rez6-ResStartValue6;
        biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
        db = biosensDatabaseHelper.getWritableDatabase();
        if((rezult1>toxin1b && toxin1==true)  || (rezult2>toxin2b && toxin2==true) || (rezult3>toxin3b && toxin3==true) || (rezult4>toxin4b && toxin4==true) || (rezult5>toxin5b && toxin5==true) || (rezult6>toxin6b && toxin6==true)){
            ImageId=R.drawable.fail;
            ListText="Toxins detected";
            BioSensDatabaseHelper.updateResearch( db,user_id, ResearchId,  true);

        }
        else{
            ImageId=R.drawable.success;
            ListText="Toxins not detected";

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(utc);

        Calendar c = Calendar.getInstance();
        String data=dateFormat.format(c.getTime());


        if(toxin1==true){
            BioSensDatabaseHelper.insertMeasurement(db,ResearchId, data, data, "mycotoxin-t2",rezult1,toxin1b,lon, lat, user_id,"Analys");
           // if(rezult1>toxin1b){}
        }
        if(toxin2==true){
            BioSensDatabaseHelper.insertMeasurement(db,ResearchId, data, data, "mycotoxin-don",rezult2,toxin2b,lon, lat, user_id,"Analys");
        }
        if(toxin3==true){
            BioSensDatabaseHelper.insertMeasurement(db,ResearchId, data, data, "mycotoxin-zearalenone",rezult3,toxin3b,lon, lat, user_id,"Analys");
        }
        if(toxin4==true){
            BioSensDatabaseHelper.insertMeasurement(db,ResearchId, data, data, "mycotoxin-patulin",rezult4,toxin4b,lon, lat, user_id,"Analys");
        }
        if(toxin5==true){
            BioSensDatabaseHelper.insertMeasurement(db,ResearchId, data, data, "aflatoxin-b1",rezult5,toxin5b,lon, lat, user_id,"Analys");
        }
        if(toxin6==true){
            BioSensDatabaseHelper.insertMeasurement(db,ResearchId, data, data, "ochratoxin-a",rezult6,toxin6b,lon, lat, user_id,"Analys");
        }



        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("research",
                    new String[]{"place_id","end_time"},
                    "user_id= ? and _id= ?",
                    new String[] {user_id,ResearchId},
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
cursor.moveToFirst();
        String PlaceId=cursor.getString(0);
        String ResearcgDate=cursor.getString(1);
cursor.close();
        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("place",
                    new String[]{"name","photo"},
                    "user_id= ? and _id= ?",
                    new String[] {user_id,PlaceId},
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.moveToFirst();
        String PlaceName=cursor.getString(0);
        int PlacePhoto = cursor.getInt(1);
        cursor.close();
        TextView fieldName=(TextView)layout.findViewById(R.id.field_name);
        TextView DateRez=(TextView)layout.findViewById(R.id.date_rez);
        TextView TextRez=(TextView)layout.findViewById(R.id.textRez);

        ImageView photo = (ImageView)layout.findViewById(R.id.field_image);
        ImageView photoRez = (ImageView)layout.findViewById(R.id.rez_image);

        photo.setImageResource(PlacePhoto);
        photo.setContentDescription("Place Photo");


        fieldName.setText(PlaceName);
        DateRez.setText(ResearcgDate);
        TextRez.setText(ListText);
        photoRez.setImageResource(ImageId);
        photoRez.setContentDescription("Rez");

        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("measurement",
                    new String[]{"unit", "value","boundary_value","_id"},
                    "user_id= ? and research_id= ?",
                    new String[] {user_id,ResearchId},
                    null, null,null);

            CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(),
                    R.layout.fragment_res_item,
                    cursor,
                    new String[]{"unit","value","boundary_value"},
                    new int[]{R.id.Toxin_name,R.id.Rez_nameValue,R.id.Rez_nameBValue},
                    0);
            ListView resList = (ListView)layout.findViewById(android.R.id.list);

            resList.setAdapter(listAdapter);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

    /*    rez=cursor.getInt(4);
        if(rez==0){
            textrez.setText("Toxins are not found");
            photo.setImageResource(R.drawable.positive);
            photo.setContentDescription("Good");
        }
        else{
            textrez.setText("Toxins are found");
            photo.setImageResource(R.drawable.danger);
            photo.setContentDescription("Bad");
        }*/
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



}
