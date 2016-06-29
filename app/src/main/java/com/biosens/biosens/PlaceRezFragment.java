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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class PlaceRezFragment extends Fragment {
    // Inflate the layout for this fragment
    SQLiteDatabase db;

    Cursor cursor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View layout=inflater.inflate(R.layout.fragment_place_rez, container, false);
        String row_id = this.getArguments().getString("row_id");
        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("place",
                    new String[]{"name","photo"},
                    " _id= ?",
                    new String[] {row_id},
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


        ImageView photo = (ImageView)layout.findViewById(R.id.field_image);


        photo.setImageResource(PlacePhoto);
        photo.setContentDescription("Place Photo");
        fieldName.setText(PlaceName);

        return layout;
    }



}
