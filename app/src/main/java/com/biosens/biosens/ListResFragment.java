package com.biosens.biosens;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;


public class ListResFragment extends Fragment {
    SQLiteDatabase db;
    SQLiteOpenHelper biosensDatabaseHelper;
    Cursor cursor;
    SessionManagement session;
    int ImageId;
    String ListText;
    int rez;
    private static final DateFormat orig = new SimpleDateFormat("yyyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final DateFormat target = new SimpleDateFormat("dd MMMM yyyy HH:mm");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_res, container, false);

        session = new SessionManagement(inflater.getContext());
        HashMap<String, String> user = session.getUserDetails();

        String row_id = this.getArguments().getString("row_id");

       String user_id = user.get(SessionManagement.KEY_ID);
        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("research",
                    new String[]{"place_id","end_time","have_toxin","Sync"},
                    "user_id= ? and _id= ?",
                    new String[] {user_id,row_id},
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.moveToFirst();
        int haveToxin=cursor.getInt(2);
        String PlaceId=cursor.getString(0);
        String ResearcgDate=cursor.getString(1);
        final int researcSync=cursor.getInt(3);
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
        if(haveToxin==1){
            ImageId=R.drawable.fail;
            ListText="Toxins detected";


        }
        else{
            ImageId=R.drawable.success;
            ListText="Toxins not detected";

        }
        TextView fieldName=(TextView)layout.findViewById(R.id.field_name);
        TextView DateRez=(TextView)layout.findViewById(R.id.date_rez);
        TextView TextRez=(TextView)layout.findViewById(R.id.textRez);

        ImageView photo = (ImageView)layout.findViewById(R.id.field_image);
        ImageView photoRez = (ImageView)layout.findViewById(R.id.rez_image);

        photo.setImageResource(PlacePhoto);
        photo.setContentDescription("Place Photo");


        fieldName.setText(PlaceName);
        try {
            DateRez.setText(convertDate(ResearcgDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextRez.setText(ListText);
        photoRez.setImageResource(ImageId);
        photoRez.setContentDescription("Rez");

        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("measurement",
                    new String[]{"unit_view", "value","unit_value", "boundary_value", "_id","sync"},
                    "user_id= ? and research_id= ?",
                    new String[] {user_id,row_id},
                    null, null,null);

            CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(),
                    R.layout.fragment_res_item,
                    cursor,
                    new String[]{"unit_view", "value", "boundary_value","unit_value","unit_value"},
                    new int[]{R.id.Toxin_name, R.id.Rez_nameValue, R.id.Rez_nameBValue,R.id.textUnitVal, R.id.textUnitValB},
                    0);
            ListView resList = (ListView)layout.findViewById(android.R.id.list);

            resList.setAdapter(listAdapter);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        cursor.moveToFirst();

        final int measurementSync=cursor.getInt(4);

        TextView TextSync = (TextView) layout.findViewById(R.id.textSync);

        if (researcSync == 0 || measurementSync == 0) {
            TextSync.setText("Unsynchronized");



        } else {
            TextSync.setText("Synchronized");

        }


        return layout;
    }

    private static String convertDate(String d) throws ParseException {
        return target.format(orig.parse(d));
    }
}
