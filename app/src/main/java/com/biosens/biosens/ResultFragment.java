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
    private Cursor cursor;
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

            cursor = db.query("Test",
                    new String[]{"_id","Field", "Culture","Date","ListText","ImageId","PrevImageId"},
                    "USER_UUID= ?",
                    new String[] {user_id},
                    null, null,null);

            CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(),
                    R.layout.list_layout,
                    cursor,
                    new String[]{"Field","Date","ListText","ImageId","PrevImageId"},
                    new int[]{R.id.field_name,R.id.date_rez,R.id.list_rez,R.id.list_image,R.id.prev_image},
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
