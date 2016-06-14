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


public class ListResFragment extends Fragment {
    SQLiteDatabase db;
    SQLiteOpenHelper biosensDatabaseHelper;
    Cursor cursor;
    SessionManagement session;
    int rez;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_list_res, container, false);

        session = new SessionManagement(inflater.getContext());
        HashMap<String, String> user = session.getUserDetails();

        int row_id = this.getArguments().getInt("row_id");

       String user_id = user.get(SessionManagement.KEY_ID);
        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("Test",
                    new String[]{"Field", "Culture","Affection","Date","Result"},
                    "USER_UUID= ? and rowid= ?",
                    new String[] {user_id,String.valueOf(row_id)},
                    null, null,null);


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
            return layout;
        }
        cursor.moveToFirst();
        TextView field=(TextView)layout.findViewById(R.id.textViewobj2);
        TextView cult=(TextView)layout.findViewById(R.id.textViewCult2);
        TextView date=(TextView)layout.findViewById(R.id.textViewDate2);
        TextView affect=(TextView)layout.findViewById(R.id.textViewTest2);
        TextView textrez=(TextView)layout.findViewById(R.id.textRez);
        ImageView photo = (ImageView)layout.findViewById(R.id.imageView);


        field.setText(cursor.getString(0));
        date.setText(cursor.getString(3));
        cult.setText(cursor.getString(1));
        affect.setText(cursor.getString(2));
        rez=cursor.getInt(4);
        if(rez==0){
            textrez.setText("Toxins are not found");
            photo.setImageResource(R.drawable.positive);
            photo.setContentDescription("Good");
        }
        else{
            textrez.setText("Toxins are found");
            photo.setImageResource(R.drawable.danger);
            photo.setContentDescription("Bad");
        }
        return layout;
    }


}
