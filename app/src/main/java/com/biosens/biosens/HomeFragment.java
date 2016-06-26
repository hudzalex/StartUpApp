package com.biosens.biosens;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.biosens.biosens.BioSensDatabaseHelper;
import com.biosens.biosens.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;


public class HomeFragment extends Fragment implements View.OnClickListener{
	
   

    Cursor cursor;
    
    private SQLiteDatabase db;
    SessionManagement session;
    SQLiteOpenHelper biosensDatabaseHelper;
    private  double rez1,rez2,rez3,rez4,rez5,rez6;
  //  EditText dateEditText;
    WaitFragment wfrag;

    private String  user_id, CultureId;
    CheckBox checkBoxToxity1,checkBoxToxity2,checkBoxToxity3,checkBoxToxity4,checkBoxToxity5,checkBoxToxity6;
    boolean toxin1, toxin2,toxin3,toxin4,toxin5,toxin6;


    EditText cultureEditText;
    Spinner spinner;

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
        toxin1=false; toxin2=false;toxin3=false;toxin4=false;toxin5=false;toxin6=false;
        checkBoxToxity1 = (CheckBox)layout.findViewById(R.id.checkBoxToxity1);
        checkBoxToxity2 = (CheckBox)layout.findViewById(R.id.checkBoxToxity2);
        checkBoxToxity3 = (CheckBox)layout.findViewById(R.id.checkBoxToxity3);
        checkBoxToxity4 = (CheckBox)layout.findViewById(R.id.checkBoxToxity4);
        checkBoxToxity5 = (CheckBox)layout.findViewById(R.id.checkBoxToxity5);
        checkBoxToxity6 = (CheckBox)layout.findViewById(R.id.checkBoxToxity6);
        startButton.setOnClickListener(this);

     biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
        db = biosensDatabaseHelper.getWritableDatabase();
        session = new SessionManagement(inflater.getContext());

        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManagement.KEY_ID);

        cultureEditText = (EditText) layout.findViewById(R.id.editTextCulture);
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

        spinner = (Spinner) layout.findViewById(R.id.spinner_place);

        android.widget.SimpleCursorAdapter  adapter = new android.widget.SimpleCursorAdapter(layout.getContext(),
                android.R.layout.simple_spinner_item,
                cursor,
                new String[] {"name"},
                new int[] {android.R.id.text1}, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Field");


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

        cursor.moveToFirst();
        cultureEditText.setText(cursor.getString(1));
        CultureId=cursor.getString(0);
        cursor.close();



        // Inflate the layout for this fragment
        someEventListener.someEvent();
        return layout;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    ReadMeasurementBluetoothTask bluetoothTask;


    @Override
    public void onClick(View v) {

        final SQLiteCursor selectedItem= (SQLiteCursor)spinner.getSelectedItem();
        final String placeId = selectedItem.getString(0);


        if(checkBoxToxity1.isChecked()){
            toxin1=true;
        }
        if (checkBoxToxity2.isChecked()){
            toxin2=true;
        }
        if (checkBoxToxity3.isChecked()){
            toxin3=true;
        }
        if (checkBoxToxity4.isChecked()){
            toxin4=true;
        }
        if (checkBoxToxity5.isChecked()){
            toxin5=true;
        }
        if (checkBoxToxity6.isChecked()){
            toxin6=true;
        }

        AsyncTask<Void,Void,Void> genericTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    double[] result = ReadMeasurementBluetoothTask.measure();

                    rez1 = result[0];
                    rez2 = result[1];
                    rez3 = result[2];
                    rez4 = result[3];
                    rez5 = result[4];
                    rez6 = result[5];



                    boolean haveToxin=false;





                    //EditText testEditText = (EditText) getActivity().findViewById(R.id.editTextToxity);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    TimeZone utc = TimeZone.getTimeZone("UTC");
                    dateFormat.setTimeZone(utc);

                    Calendar c = Calendar.getInstance();
                    String data=dateFormat.format(c.getTime());

//        String placeName = selectedItem.getString(1);



                    String researchId = BioSensDatabaseHelper.insertResearch(db, placeId, data, data, CultureId, user_id,haveToxin,"Analys").toString();


                    // BioSensDatabaseHelper.insertMeasurement(db,researchId.toString(), data, data, "Mycotoxin T2",rez,lon, lat, user_id,"Analys");
                    //  BioSensDatabaseHelper.insertTest(db, rez, placeName, data, cultureEditText.getText().toString(), "Mycotoxin T2",ListText ,ImageId,PrevImageId, lon, lat, user_id);
                    Bundle bundle = new Bundle();
                    bundle.putString("ResearchId", researchId);
                    bundle.putDouble("ResStartValue1", rez1);
                    bundle.putDouble("ResStartValue2", rez2);
                    bundle.putDouble("ResStartValue3", rez3);
                    bundle.putDouble("ResStartValue4", rez4);
                    bundle.putDouble("ResStartValue5", rez5);
                    bundle.putDouble("ResStartValue6", rez6);
                    bundle.putBoolean("Toxin1", toxin1);
                    bundle.putBoolean("Toxin2", toxin2);
                    bundle.putBoolean("Toxin3", toxin3);
                    bundle.putBoolean("Toxin4", toxin4);
                    bundle.putBoolean("Toxin5", toxin5);
                    bundle.putBoolean("Toxin6", toxin6);
                    WaitFragment Wfragment =new WaitFragment();
                    Wfragment.setArguments(bundle);
                    FragmentTransaction ftranssct=getFragmentManager().beginTransaction();

                    ftranssct.replace(R.id.container, Wfragment );
                    ftranssct.commit();


                } catch (InterruptedException e) {

                }

                return null;
            }
        };

        genericTask.execute();


//        AsyncTaskCompat.executeParallel( bluetoothTask );

//        bluetoothTask.execute();




    }

}
