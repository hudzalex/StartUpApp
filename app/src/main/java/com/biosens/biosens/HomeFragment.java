package com.biosens.biosens;

import android.*;
import android.Manifest;
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
import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.biosens.biosens.BioSensDatabaseHelper;
import com.biosens.biosens.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class HomeFragment extends Fragment implements View.OnClickListener{
	
   

    Cursor cursor;
    
    private SQLiteDatabase db;
    SessionManagement session;
    SQLiteOpenHelper biosensDatabaseHelper;
    private  int rez,ImageId,PrevImageId, inp;
    EditText dateEditText;
    WaitFragment wfrag;
    private LocationManager locationManager;
    private double lat,lon;
    private String ListText, user_id, CultureId, FieldId;
    EditText objectEditText;
    EditText cultureEditText;
    final Random random = new Random();
    public interface onSomeEventListener {
        public int someEvent();
    }

    onSomeEventListener someEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_home, container, false);
        Button startButton = (Button) layout.findViewById(R.id.buttonRead);
        /*dateEditText = (EditText)  layout.findViewById(R.id.editTextDate);
        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialog dialog=new DateDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }

        });*/

        startButton.setOnClickListener(this);

     biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
        db = biosensDatabaseHelper.getWritableDatabase();
        session = new SessionManagement(inflater.getContext());

        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManagement.KEY_ID);
        BioSensDatabaseHelper.insertCult(db,"Пшеница",R.drawable.field_1);
        BioSensDatabaseHelper.insertPlace(db, "Поле 19", 0.0, 0.0,R.drawable.field_1 );
        objectEditText = (EditText) getActivity().findViewById(R.id.editTextField);
        cultureEditText = (EditText) getActivity().findViewById(R.id.editTextCulture);
        try {

            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("place",
                    new String[]{"_id","name"},
                    null,
                    null,
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        objectEditText.setText(cursor.getString(1));
       FieldId=cursor.getString(0);
        cursor.close();
        try {

            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("culture",
                    new String[]{"_id","name"},
                    null,
                    null,
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        cultureEditText.setText(cursor.getString(1));
        CultureId=cursor.getString(0);
        cursor.close();
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
        // Inflate the layout for this fragment
        someEventListener.someEvent();
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

            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
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

    @Override
    public void onClick(View v) {
    // rez = someEventListener.someEvent();
boolean haveToxin=false;
        rez=random.nextInt(2);
        if(rez==0){
            ImageId=R.drawable.success;
            ListText="Toxins are not found";
            haveToxin=false;
        }
        else{
            ImageId=R.drawable.fail;
            ListText="Toxins are found";
            haveToxin=true;
        }


		 try {

            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("Test",
                    new String[]{"Field", "Culture","Affection","Date","Result"},
                    "USER_UUID= ?",
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

        EditText testEditText = (EditText) getActivity().findViewById(R.id.editTextToxity);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");


        Calendar c = Calendar.getInstance();
        String data=dateFormat.format(c.getTime());


        BioSensDatabaseHelper.insertResearch(db,FieldId, data, data, CultureId, user_id,haveToxin ,rez,"Analys");
        try {

            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("research",
                    new String[]{"_id"},
                    null,
                    null,
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.moveToLast();
        BioSensDatabaseHelper.insertMeasurement(db,cursor.getString(0), data, data, testEditText.getText().toString(),rez,lon, lat, user_id,"Analys");
        BioSensDatabaseHelper.insertTest(db, rez, objectEditText.getText().toString(), data, cultureEditText.getText().toString(), testEditText.getText().toString(),ListText ,ImageId,PrevImageId, lon, lat, user_id);
       // BioSensDatabaseHelper.insertPlace(db, cultureEditText.getText().toString(), lon, lat, user_id,PrevImageId );
        FragmentTransaction ftranssct=getFragmentManager().beginTransaction();
        wfrag=new WaitFragment();
        ftranssct.replace(R.id.container, wfrag);
        ftranssct.commit();
    }

}
