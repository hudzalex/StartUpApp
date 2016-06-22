package com.biosens.biosens;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HttpService extends Service {
    private static Timer timer = new Timer();
    private Context ctx;
    private SQLiteDatabase db;
    private Cursor cursor;
    boolean syncTest=false;
    SessionManagement session;
  //  String userid;
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
  //  String url = "http://httpbin.org/post";
   // RequestQueue queue = Volley.newRequestQueue(this);
    public void onCreate()
    {
        super.onCreate();
        ctx = this;
      //  session = new SessionManagement(getApplicationContext());
      //  HashMap<String, String> user = session.getUserDetails();


      //  userid = user.get(SessionManagement.KEY_ID);
        startService();

    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 15000);
    }

    private class mainTask extends TimerTask
    {
        int currentTable = 0;

        public void run()
        {

            try {
                SQLiteOpenHelper biosensDatabaseHelper = new BioSensDatabaseHelper(getApplicationContext());
                db = biosensDatabaseHelper.getReadableDatabase();

                cursor = db.query("place",
                        new String[]{"_id","name", "longitude","latitude","user_id","photo","sync"},
                        "sync= ?",
                        new String[]{"0"},
                        null, null,null);

                cursor.moveToFirst();

                if (!cursor.isAfterLast()) {
//                    do {
                        String s = cursor.getString(1);
                        String a=cursor.getString(6);
                        String url = "http://httpbin.org/post";
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        Map<String, String> jsonParams = new HashMap<String, String>();

                        String id = cursor.getString(0);
                        jsonParams.put("id", id);
                        jsonParams.put("name", cursor.getString(1));
                        jsonParams.put("longitude", String.valueOf(cursor.getDouble(2)));
                        jsonParams.put("latitude", String.valueOf(cursor.getDouble(3)));
                        jsonParams.put("userId",cursor.getString(4));
                        jsonParams.put("photo",String.valueOf(cursor.getInt(5)));

                        JsonObjectRequest postRequest = new JsonObjectRequest( Request.Method.POST, url,
                                new JSONObject(jsonParams),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("HttpService", "Response " + String.valueOf(response));
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //   Handle Error
                                    }
                                });
                        queue.add(postRequest);

                        toastHandler.sendEmptyMessage(0);
//                    } while (cursor.moveToNext());
                } else {

                }

            } catch(SQLiteException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }



        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }
    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };
}
