package com.biosens.biosens;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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


public class HomeFragment extends Fragment implements View.OnClickListener {


    Cursor cursor;

    private SQLiteDatabase db;
    SessionManagement session;
    SQLiteOpenHelper biosensDatabaseHelper;
    private LocationManager locationManager;
    private double rez1, rez2, rez3, rez4, rez5, rez6;
    //  EditText dateEditText;
    WaitFragment wfrag;

    private String user_id, CultureId;
    CheckBox checkBoxToxity1, checkBoxToxity2, checkBoxToxity3, checkBoxToxity4, checkBoxToxity5, checkBoxToxity6;
    boolean toxin1, toxin2, toxin3, toxin4, toxin5, toxin6;


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
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
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
        toxin1 = false;
        toxin2 = false;
        toxin3 = false;
        toxin4 = false;
        toxin5 = false;
        toxin6 = false;
        checkBoxToxity1 = (CheckBox) layout.findViewById(R.id.checkBoxToxity1);
        checkBoxToxity2 = (CheckBox) layout.findViewById(R.id.checkBoxToxity2);
        checkBoxToxity3 = (CheckBox) layout.findViewById(R.id.checkBoxToxity3);
        checkBoxToxity4 = (CheckBox) layout.findViewById(R.id.checkBoxToxity4);
        checkBoxToxity5 = (CheckBox) layout.findViewById(R.id.checkBoxToxity5);
        checkBoxToxity6 = (CheckBox) layout.findViewById(R.id.checkBoxToxity6);
        startButton.setOnClickListener(this);


        biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
        db = biosensDatabaseHelper.getWritableDatabase();
        session = new SessionManagement(inflater.getContext());

        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Enable GPS", Toast.LENGTH_SHORT);
            toast.show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }

        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManagement.KEY_ID);

        cultureEditText = (EditText) layout.findViewById(R.id.editTextCulture);
        try {

            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("place",
                    new String[]{"_id", "name"},
                    null,
                    null,
                    null, null, null);


        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        spinner = (Spinner) layout.findViewById(R.id.spinner_place);

        android.widget.SimpleCursorAdapter adapter = new android.widget.SimpleCursorAdapter(layout.getContext(),
                android.R.layout.simple_spinner_item,
                cursor,
                new String[]{"name"},
                new int[]{android.R.id.text1}, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Field");


        try {

            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("culture",
                    new String[]{"_id", "name"},
                    null,
                    null,
                    null, null, null);


        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        cursor.moveToFirst();
        cultureEditText.setText(cursor.getString(1));
        CultureId = cursor.getString(0);
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

        final SQLiteCursor selectedItem = (SQLiteCursor) spinner.getSelectedItem();
        if (selectedItem == null) {
            Toast.makeText(getActivity().getBaseContext(), "Select place", Toast.LENGTH_LONG).show();
            return;
        }


        final String placeId = selectedItem.getString(0);

        boolean noSelected = !checkBoxToxity1.isChecked()
                && !checkBoxToxity2.isChecked()
                && !checkBoxToxity3.isChecked()
                && !checkBoxToxity4.isChecked()
                && !checkBoxToxity5.isChecked()
                && !checkBoxToxity6.isChecked();

        if (noSelected) {
            Toast.makeText(getActivity().getBaseContext(), "Select toxin", Toast.LENGTH_LONG).show();
            return;
        }


        if (checkBoxToxity1.isChecked()) {
            toxin1 = true;
        }
        if (checkBoxToxity2.isChecked()) {
            toxin2 = true;
        }
        if (checkBoxToxity3.isChecked()) {
            toxin3 = true;
        }
        if (checkBoxToxity4.isChecked()) {
            toxin4 = true;
        }
        if (checkBoxToxity5.isChecked()) {
            toxin5 = true;
        }
        if (checkBoxToxity6.isChecked()) {
            toxin6 = true;
        }


        Bundle bundle = new Bundle();
        // bundle.putString("ResearchId", researchId);
        bundle.putString("cultureId", CultureId);
        bundle.putString("user_id", user_id);
        bundle.putString("place_id", placeId);
        bundle.putBoolean("Toxin1", toxin1);
        bundle.putBoolean("Toxin2", toxin2);
        bundle.putBoolean("Toxin3", toxin3);
        bundle.putBoolean("Toxin4", toxin4);
        bundle.putBoolean("Toxin5", toxin5);
        bundle.putBoolean("Toxin6", toxin6);
        WaitFragment Wfragment = new WaitFragment();
        Wfragment.setArguments(bundle);
        FragmentTransaction ftranssct = getFragmentManager().beginTransaction();

        ftranssct.replace(R.id.container, Wfragment);
        ftranssct.commit();
//        AsyncTaskCompat.executeParallel( bluetoothTask );

//        bluetoothTask.execute();


    }

}
