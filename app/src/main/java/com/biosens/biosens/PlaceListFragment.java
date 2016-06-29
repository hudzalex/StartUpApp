package com.biosens.biosens;

import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.HashMap;


public class PlaceListFragment extends ListFragment {
    private SQLiteDatabase db;
    private Cursor cursor;
    PlaceFragment pfragInser;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManagement session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session = new SessionManagement(inflater.getContext());
        View layout=inflater.inflate(R.layout.fragment_place_list, container, false);

        HashMap<String, String> user = session.getUserDetails();
        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pfragInser=new PlaceFragment();

                FragmentTransaction ftransact=getFragmentManager().beginTransaction();
                ftransact.replace(R.id.container,pfragInser);
                ftransact.commit();
            }
        });

        String user_id = user.get(SessionManagement.KEY_ID);

        try {
            SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(inflater.getContext());
            db = biosensDatabaseHelper.getReadableDatabase();

            cursor = db.query("place",
                    new String[]{"name","photo","_id"},
                    "user_id= ?",
                    new String[] {user_id},
                    null, null,null);

            CursorAdapter listAdapter = new SimpleCursorAdapter(inflater.getContext(),
                    R.layout.fragment_place_item,
                    cursor,
                    new String[]{"name","photo"},
                    new int[]{R.id.place_name,R.id.place_image},
                    0);

            ListView placeList = (ListView)layout.findViewById(android.R.id.list);

            placeList.setAdapter(listAdapter);

        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(inflater.getContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        return layout;
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
        PlaceRezFragment fragInfo = new  PlaceRezFragment();
        fragInfo.setArguments(bundle);
        FragmentTransaction ftransact=getFragmentManager().beginTransaction();
        ftransact.replace(R.id.container,fragInfo);
        ftransact.commit();
    }
}
