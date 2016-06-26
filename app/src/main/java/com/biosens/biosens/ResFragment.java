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
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
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

    final Random random = new Random();
   int ImageId;
    String ResearchId = "", ListText="";




    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_res, container, false);

        session = new SessionManagement(inflater.getContext());
        HashMap<String, String> user = session.getUserDetails();


        final String user_id = user.get(SessionManagement.KEY_ID);





        Bundle bundle = this.getArguments();
        if (bundle != null) {

            ResearchId = bundle.getString("ResearchId", "");
            ListText = bundle.getString("ListText", "");
            ImageId = bundle.getInt("ImageId", 0);


        }




                    try {
                        SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
                        db = biosensDatabaseHelper.getReadableDatabase();

                        cursor = db.query("research",
                                new String[]{"place_id", "end_time"},
                                "_id= ?",
                                new String[]{ResearchId},
                                null, null, null);


                    } catch (SQLiteException e) {
                        Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    cursor.moveToFirst();
                    final String PlaceId = cursor.getString(0);
                    final String ResearcgDate = cursor.getString(1);
                    cursor.close();
                    try {
                        SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
                        db = biosensDatabaseHelper.getReadableDatabase();

                        cursor = db.query("place",
                                new String[]{"name", "photo"},
                                "user_id= ? and _id= ?",
                                new String[]{user_id, PlaceId},
                                null, null, null);


                    } catch (SQLiteException e) {
                        Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    cursor.moveToFirst();
                    final String PlaceName = cursor.getString(0);
                    final int PlacePhoto = cursor.getInt(1);
                    cursor.close();


                    final CursorAdapter listAdapter;


                    try {
                        SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
                        db = biosensDatabaseHelper.getReadableDatabase();

                        cursor = db.query("measurement",
                                new String[]{"unit", "value", "boundary_value", "_id"},
                                "user_id= ? and research_id= ?",
                                new String[]{user_id, ResearchId},
                                null, null, null);

                        listAdapter = new SimpleCursorAdapter(inflater.getContext(),
                                R.layout.fragment_res_item,
                                cursor,
                                new String[]{"unit", "value", "boundary_value"},
                                new int[]{R.id.Toxin_name, R.id.Rez_nameValue, R.id.Rez_nameBValue},
                                0);
                    } catch (SQLiteException e) {
                        Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
                        toast.show();

                        return null;
                    }



                            TextView fieldName = (TextView) layout.findViewById(R.id.field_name);
                            TextView DateRez = (TextView) layout.findViewById(R.id.date_rez);
                            TextView TextRez = (TextView) layout.findViewById(R.id.textRez);

                            ImageView photo = (ImageView) layout.findViewById(R.id.field_image);
                            ImageView photoRez = (ImageView) layout.findViewById(R.id.rez_image);

                            photo.setImageResource(PlacePhoto);
                            photo.setContentDescription("Place Photo");


                            fieldName.setText(PlaceName);
                            DateRez.setText(ResearcgDate);
                            TextRez.setText(ListText);
                            photoRez.setImageResource(ImageId);
                            photoRez.setContentDescription("Rez");


                            ListView resList = (ListView) layout.findViewById(android.R.id.list);

                            resList.setAdapter(listAdapter);


        return layout;
    }




}
