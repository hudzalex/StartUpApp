package com.biosens.biosens;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.biosens.biosens.BioSensDatabaseHelper;
import com.biosens.biosens.R;

import java.util.HashMap;


public class ResultFragment extends ListFragment{
    private SQLiteDatabase db;
    private Cursor cursor,cursor1;
    String placeName,Date,ListText,plase_id;
    int placePhoto,ImageId;
    int haveToxin;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManagement session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session = new SessionManagement(inflater.getContext());
        HashMap<String, String> user = session.getUserDetails();


        String user_id = user.get(SessionManagement.KEY_ID);

        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("research",
                    new String[]{"_id","place_id", "culture_id","end_time","have_toxin"},
                    "user_id= ?",
                    new String[] {user_id},
                    null, null,"rowid DESC");
/*cursor.moveToFirst();
            if(!cursor.isAfterLast()){
                do{
                    Date=cursor.getString(3);
                    haveToxin=cursor.getInt(4);
                    plase_id=cursor.getString(1);
                    try {
                        SQLiteOpenHelper biosensDatabaseHelper1 = new BioSensDatabaseHelper(inflater.getContext());
                        db = biosensDatabaseHelper1.getReadableDatabase();

                        cursor1 = db.query("place",
                                new String[]{"_id", "place_name","photo"},
                                "user_id= ? and _id= ?",
                                new String[]{user_id, plase_id},
                                null, null, null);
                    }catch(SQLiteException e) {
                        Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    placeName=cursor1.getString(1);
                    placePhoto=cursor1.getInt(2);
                    if(haveToxin==1){
                        ImageId=R.drawable.fail;
                        ListText="Toxins detected";


                    }
                    else{
                        ImageId=R.drawable.success;
                        ListText="Toxins not detected";

                    }
                }


                while (cursor.moveToNext());
            }*/

            CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(),
                    R.layout.list_layout,
                    cursor,
                    new String[]{"place_id","end_time","have_toxin"},
                    new int[]{R.id.field_name,R.id.date_rez,R.id.list_rez},
                    0);
            setListAdapter(listAdapter);

        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onListItemClick(ListView l,
                                View v,
                                int position,
                                long id) {
       Cursor cursorList = (Cursor) l.getItemAtPosition(position);
        String Id = cursorList.getString(cursorList.getColumnIndexOrThrow("_id"));
        Bundle bundle = new Bundle();

        bundle.putString("row_id", Id);
        ListResFragment fragInfo = new  ListResFragment();
        fragInfo.setArguments(bundle);
        FragmentTransaction ftransact=getFragmentManager().beginTransaction();
        ftransact.replace(R.id.container,fragInfo);
        ftransact.commit();
    }
}
